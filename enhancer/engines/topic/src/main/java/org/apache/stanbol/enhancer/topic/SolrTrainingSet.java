/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.enhancer.topic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@code TrainingSet} interface that uses a Solr Core as backend to store and retrieve
 * the text examples used to train a classifier.
 */
@Component(metatype = true, immediate = true, configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Service
@Properties(value = {@Property(name = SolrTrainingSet.TRAINING_SET_ID),
                     @Property(name = SolrTrainingSet.SOLR_CORE),
                     @Property(name = SolrTrainingSet.EXAMPLE_ID_FIELD),
                     @Property(name = SolrTrainingSet.EXAMPLE_TEXT_FIELD),
                     @Property(name = SolrTrainingSet.TOPICS_URI_FIELD),
                     @Property(name = SolrTrainingSet.MODIFICATION_DATE_FIELD)})
public class SolrTrainingSet extends ConfiguredSolrCoreTracker implements TrainingSet {

    public static final String TRAINING_SET_ID = "org.apache.stanbol.enhancer.topic.trainingset.id";

    public static final String SOLR_CORE = "org.apache.stanbol.enhancer.engine.topic.solrCore";

    public static final String TOPICS_URI_FIELD = "org.apache.stanbol.enhancer.engine.topic.topicsUriField";

    public static final String EXAMPLE_ID_FIELD = "org.apache.stanbol.enhancer.engine.topic.exampleIdField";

    public static final String EXAMPLE_TEXT_FIELD = "org.apache.stanbol.enhancer.engine.topic.exampleTextField";

    public static final String MODIFICATION_DATE_FIELD = "org.apache.stanbol.enhancer.engine.topic.modificiationDateField";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SolrTrainingSet.class);

    protected String trainingSetId;

    protected String exampleIdField;

    protected String exampleTextField;

    protected String topicUrisField;

    protected String modificationDateField;

    // TODO: make me configurable using an OSGi property
    protected int batchSize = 100;

    @Activate
    protected void activate(ComponentContext context) throws ConfigurationException, InvalidSyntaxException {
        @SuppressWarnings("unchecked")
        Dictionary<String,Object> config = context.getProperties();
        this.context = context;
        configure(config);
    }

    @Deactivate
    public void deactivate(ComponentContext context) {
        if (indexTracker != null) {
            indexTracker.close();
        }
    }

    @Override
    public void configure(Dictionary<String,Object> config) throws ConfigurationException {
        trainingSetId = getRequiredStringParam(config, TRAINING_SET_ID);
        exampleIdField = getRequiredStringParam(config, EXAMPLE_ID_FIELD);
        exampleTextField = getRequiredStringParam(config, EXAMPLE_TEXT_FIELD);
        topicUrisField = getRequiredStringParam(config, TOPICS_URI_FIELD);
        modificationDateField = getRequiredStringParam(config, MODIFICATION_DATE_FIELD);
        configureSolrCore(config, SOLR_CORE);
    }

    public static ConfiguredSolrCoreTracker fromParameters(Dictionary<String,Object> config) throws ConfigurationException {
        ConfiguredSolrCoreTracker engine = new SolrTrainingSet();
        engine.configure(config);
        return engine;
    }

    @Override
    public boolean isUpdatable() {
        return true;
    }

    @Override
    public String registerExample(String exampleId, String text, List<String> topics) throws TrainingSetException {
        if (exampleId == null || exampleId.isEmpty()) {
            exampleId = UUID.randomUUID().toString();
        }
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(exampleIdField, exampleId);
        doc.addField(exampleTextField, text);
        if (topics != null) {
            doc.addField(topicUrisField, topics);
        }
        doc.addField(modificationDateField, new Date());
        SolrServer server = getActiveSolrServer();
        try {
            server.add(doc);
            server.commit();
        } catch (Exception e) {
            String msg = String.format("Could not register example '%s' with topics: ['%s']", exampleId,
                StringUtils.join(topics, "', '"));
            throw new TrainingSetException(msg, e);
        }
        return exampleId;
    }

    @Override
    public Set<String> getUpdatedTopics(Calendar lastModificationDate) throws TrainingSetException {
        // TODO
        return Collections.emptySet();
    }

    @Override
    public Batch<String> getPositiveExamples(List<String> topics, Object offset) throws TrainingSetException {
        return getExamples(topics, offset, true);
    }

    @Override
    public Batch<String> getNegativeExamples(List<String> topics, Object offset) throws TrainingSetException {
        return getExamples(topics, offset, false);
    }

    protected Batch<String> getExamples(List<String> topics, Object offset, boolean positive) throws TrainingSetException {
        List<String> items = new ArrayList<String>();
        SolrServer solrServer = getActiveSolrServer();
        SolrQuery query = new SolrQuery();
        List<String> parts = new ArrayList<String>();
        if (topics.isEmpty()) {
            query.setQuery("*:*");
        } else if (positive) {
            for (String topic : topics) {
                // use a nested query to avoid string escaping issues with special solr chars
                parts.add("_query_:\"{!field f=" + topicUrisField + "}" + topic + "\"");
            }
            query.setQuery(StringUtils.join(parts, " OR "));
        } else {
            for (String topic : topics) {
                // use a nested query to avoid string escaping issues with special solr chars
                parts.add("-_query_:\"{!field f=" + topicUrisField + "}" + topic + "\"");
            }
            query.setQuery(StringUtils.join(parts, " AND "));
        }
        try {
            for (SolrDocument result : solrServer.query(query).getResults()) {
                Collection<Object> textValues = result.getFieldValues(exampleTextField);
                if (textValues == null) {
                    continue;
                }
                for (Object value : textValues) {
                    items.add(value.toString());
                }
            }
        } catch (SolrServerException e) {
            String msg = String.format(
                "Error while fetching positive examples for topics ['%s'] on Solr Core '%s'.",
                StringUtils.join(topics, "', '"), solrCoreId);
            throw new TrainingSetException(msg, e);
        }
        return new Batch<String>(items, false, null);
    }

    @Override
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
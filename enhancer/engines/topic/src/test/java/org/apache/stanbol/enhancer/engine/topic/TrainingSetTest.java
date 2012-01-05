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
package org.apache.stanbol.enhancer.engine.topic;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.stanbol.enhancer.topic.Batch;
import org.apache.stanbol.enhancer.topic.SolrTrainingSet;
import org.apache.stanbol.enhancer.topic.TrainingSetException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.cm.ConfigurationException;
import org.xml.sax.SAXException;

public class TrainingSetTest extends BaseTestWithSolrCore {

    public static final String TOPIC_1 = "http://example.com/topics/topic1";

    public static final String TOPIC_2 = "http://example.com/topics/topic2";

    public static final String TOPIC_3 = "http://example.com/topics/topic3";

    protected EmbeddedSolrServer trainingsetSolrServer;

    protected File solrHome;

    protected SolrTrainingSet trainingSet;

    @Before
    public void setup() throws IOException,
                       ParserConfigurationException,
                       SAXException,
                       ConfigurationException {
        solrHome = File.createTempFile("topicTrainingSetTest_", "_solr_cores");
        solrHome.delete();
        solrHome.mkdir();
        trainingsetSolrServer = makeEmptyEmbeddedSolrServer(solrHome, "trainingsetserver", "trainingset");
        trainingSet = new SolrTrainingSet();
        trainingSet.configure(getDefaultConfigParams());
    }

    @After
    public void cleanupEmbeddedSolrServer() {
        FileUtils.deleteQuietly(solrHome);
        solrHome = null;
        trainingsetSolrServer = null;
    }

    @Test
    public void testEmptyTrainingSet() throws TrainingSetException {
        Batch<String> examples = trainingSet.getPositiveExamples(new ArrayList<String>(), null);
        assertEquals(examples.items.size(), 0);
        assertFalse(examples.hasMore);
        examples = trainingSet.getNegativeExamples(new ArrayList<String>(), null);
        assertEquals(examples.items.size(), 0);
        assertFalse(examples.hasMore);
        examples = trainingSet.getPositiveExamples(Arrays.asList(TOPIC_1), null);
        assertEquals(examples.items.size(), 0);
        assertFalse(examples.hasMore);
        examples = trainingSet.getPositiveExamples(Arrays.asList(TOPIC_1, TOPIC_2), null);
        assertEquals(examples.items.size(), 0);
        assertFalse(examples.hasMore);
        examples = trainingSet.getNegativeExamples(Arrays.asList(TOPIC_1, TOPIC_2), null);
        assertEquals(examples.items.size(), 0);
        assertFalse(examples.hasMore);
    }

    @Test
    public void testStoringExamples() throws ConfigurationException, TrainingSetException {
        trainingSet.registerExample("example1", "Text of example1.", Arrays.asList(TOPIC_1));
        trainingSet.registerExample("example2", "Text of example2.", Arrays.asList(TOPIC_1, TOPIC_2));
        trainingSet.registerExample("example3", "Text of example3.", new ArrayList<String>());

        Batch<String> examples = trainingSet.getPositiveExamples(Arrays.asList(TOPIC_2), null);
        assertEquals(1, examples.items.size());
        assertEquals(examples.items, Arrays.asList("Text of example2."));
        assertFalse(examples.hasMore);

        examples = trainingSet.getPositiveExamples(Arrays.asList(TOPIC_1, TOPIC_3), null);
        assertEquals(2, examples.items.size());
        assertEquals(examples.items, Arrays.asList("Text of example1.", "Text of example2."));
        assertFalse(examples.hasMore);

        examples = trainingSet.getNegativeExamples(Arrays.asList(TOPIC_1), null);
        assertEquals(1, examples.items.size());
        assertEquals(examples.items, Arrays.asList("Text of example3."));
        assertFalse(examples.hasMore);
    }

    // @Test
    public void testBatchingExamples() throws ConfigurationException, TrainingSetException {
        for (int i = 0; i < 28; i++) {
            trainingSet.registerExample("example" + i, "Text of example" + i + ".", Arrays.asList(TOPIC_1));
        }
        trainingSet.setBatchSize(10);
        Batch<String> examples = trainingSet.getPositiveExamples(Arrays.asList(TOPIC_1), null);
        assertEquals(10, examples.items.size());
        assertTrue(examples.hasMore);

        examples = trainingSet.getPositiveExamples(Arrays.asList(TOPIC_1), examples.nextOffset);
        assertEquals(10, examples.items.size());
        assertTrue(examples.hasMore);

        examples = trainingSet.getPositiveExamples(Arrays.asList(TOPIC_1), examples.nextOffset);
        assertEquals(8, examples.items.size());
        assertFalse(examples.hasMore);
    }

    protected Hashtable<String,Object> getDefaultConfigParams() {
        Hashtable<String,Object> config = new Hashtable<String,Object>();
        config.put(SolrTrainingSet.SOLR_CORE, trainingsetSolrServer);
        config.put(SolrTrainingSet.TRAINING_SET_ID, "test-training-set");
        config.put(SolrTrainingSet.EXAMPLE_ID_FIELD, "id");
        config.put(SolrTrainingSet.EXAMPLE_TEXT_FIELD, "text");
        config.put(SolrTrainingSet.TOPICS_URI_FIELD, "topics");
        config.put(SolrTrainingSet.MODIFICATION_DATE_FIELD, "modification_dt");
        return config;
    }
}
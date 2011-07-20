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
package org.apache.stanbol.commons.solr;

import java.util.Set;

import org.apache.felix.scr.annotations.Service;
import org.apache.solr.client.solrj.SolrServer;

/**
 * The main function of this provider interface is to allow, that moving the support for an embedded
 * {@link SolrServer} can be realised by an own bundle.
 * <p>
 * The reason for that is, that this requires to include a lot of dependencies to the Solr core and all the
 * Lucene libraries one might not need in most of the useage cases of the SolrYard because typically one might
 * want to run the SolrServer in an own virtual machine or even on an own server.
 * <p>
 * However for some usage scenarios and especially for testing it is very useful to have the possibility to
 * use Solr as embedded service.
 * 
 * @author Rupert Westenthaler
 */
@Service
public interface SolrServerProvider {

    /**
     * SolrServer types defined here to avoid java dependencies to the according java classes
     * 
     * @author Rupert Westenthaler
     * 
     */
    enum Type {
        /**
         * Uses an embedded SolrServer that runs within the same virtual machine
         */
        EMBEDDED,
        /**
         * The default type that can be used for query and updates
         */
        HTTP,
        /**
         * This server is preferable used for updates
         */
        STREAMING,
        /**
         * This allows to use load balancing on multiple SolrServers via a round robin algorithm.
         */
        LOAD_BALANCE
    }

    /**
     * Getter for the supported types of this Provider
     * 
     * @return the Types supported by this Provider
     */
    Set<Type> supportedTypes();

    /**
     * Getter for the {@link SolrServer} instance for the provided URI or path (in case of an embedded server)
     * 
     * @param type
     *            The type of the requested SolrServer instance or <code>null</code> for the default type
     * @param uriOrPath
     *            the URI (in case of an remote SolrServer that is accessed via RESTfull services) or the Path
     *            (in case of an embedded SolrServer)
     * @param additional
     *            This allows to parse additional SolrServers. This may be ignored if the requested type does
     *            not support the usage of multiple servers.
     * @return the configured SolrServer client for the parsed parameter
     * @throws NullPointerException
     * @throws IllegalArgumentException
     *             if <code>null</code> is parsed as uriOrPath or if the parsed URI or path is not valid for
     *             the requested {@link Type} or the parsed type is not supported by this provider
     */
    SolrServer getSolrServer(Type type, String uriOrPath, String... additional) throws IllegalArgumentException;
}
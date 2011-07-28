Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

# Default Indexin Tool for RDF

This tool provides a default configuration for indexing RDF
files (e.g. a SKOS export of a thesaurus or a set of foaf files �)

## Building:

If not yet build by the built process of the entityhub call

    mvn install

in this directory and than

    mvn -o assembly:single
    
to build the jar with all the dependencies used later for indexing.

If the build succeeds go to the /target directory and copy the

    org.apache.stanbol.entityhub.indexing.genericrdf-*-jar-with-dependencies.jar

to the directory you would like to start the indexing.

## Index:

### (1) Initialise the configuration

The default configuration is initialised by calling

    java -jar org.apache.stanbol.entityhub.indexing.genericrdf-*-jar-with-dependencies.jar init

This will create a sub-folder with the name indexing in the current directory.
Within this folder all the

* configurations (indexing/config)
* source files (indexing/resources)
* created files (indexing/destination)
* distribution files (indexing/distribution)

will be located.

### (2) Adapt the configuration:

The configuration is located within the

    indexing/config

directory.

The Indexer supports two Indexing Modes

1. Iterate over the Data and lookup the Scores for Entities (default). For this mode the "entityDataIterable" and a "entityScoreProvider" MUST BE configured. If no entity scores are available there exists an default entityScoreProvider that provides no entity scores. This mode is typically used to index all entities of a dataset.
2. Iterate over the entity IDs and Scores and lookup the data. For this Mode a "entityIdIterator" and a "entityDataProvider" Provider MUST BE configured. This mode is typically used to index a predefined list of entities (that might only be a very small subset of the whole dataset). 

The configuration of the mentioned components is contained in the main indexing configuration file explained below.

#### Main indexing Configuration (indexing.properties)

This file contains the main configuration for the indexing process.

* the "name" property MUST BE set to the name of the referenced site to be created by the indexing process
* the "entityDataIterable" is used to configure the component iterating over the RDF data to be indexed. The "source" parameter refers to the directory the RDF files to be indexed are searched. The RDF files can be compressed with 'gz', 'bz2' or 'zip'. It is even supported to load multiple RDF files contained in a single ZIP archive.
* the "entityScoreProvider" is used to provide the ranking for entities. A typical example are the number of incoming links. Such rankings are typically used to weight recommendations and sort result lists. (e.g. by a query for "Paris" it is much more likely that a User refers to Paris in France as to one of the two Paris in Texas). If no rankings are available you should use the "org.apache.stanbol.entityhub.indexing.core.source.NoEntityScoreProvider".
* the "scoreNormalizer" is only useful in case entity scores are available. This component is used to normalize rankings or also to filter entities with low rankings.
* the "entityProcessor" is used to process (map, convert, filter) information of entities before indexing. The mapping configuration is in an own file (default "mapping.txt").
* Indexes need to provide the configurations used to store entities. The "fieldConfiguration" allows to specify this. Typically is is the same mapping file as used for the "entityProcessor" however this is not a requirement.
* the "indexingDestination" property is used to configure the target for the Indexing. Currently there is only a single implementation that stores the indexed data within a SolrYard. The "boosts" parameter can be used to boost (see Solr Documentation for details) specific fields (typically labels) for full text searches.
* all properties starting with "org.apache.stanbol.entityhub.site." are used for the configuration for the referenced site.

Pleas note also the documentation within the "indexing.properties" file for details.

#### Mapping Configuration (mappings.txt)

Mappings are used for three different things:

1. During the indexing process by the "entityProcessor" to process the information of each entity
2. At runtime by the local Cache to process single Entities that are updated in the cache.
3. At runtime by the Entityhub when importing an Entity form a referenced Site.

The configurations for (1) and (2) are typically identical. For (3) on might want to use a different configuration. The default configuration assumes to use the same configuration (mapping.txt) for (1) and (2) and no specific configuration for (3).

For details how to configure mappings see the documentation on the [IKS wiki](TODO add link)

#### Score Normalizer Configuration

The default configuration also provides examples for configurations of the different score normalisers. However by default they are not used.

* "minscore.properties": Example of how to configure minimum score for Entities to be indexed
* "scorerange.properties": Example of how to normalise the maximum/minimum score of Entities to the configured range.

NOTE: 

* To use score normalisation scores need to be provided for Entities. This means a "entityScoreProvider" or a "entityIdIterator" needs to be configured (indexing.properties).
* Multiple score normalisers can be used. The call order is determined by the configuration of the "scoreNormalizer" property (indexing.properties). 

### (3) Provide the RDF files to be indexed

All sources for the indexing process need to be located within the the

    indexing/resources

directory

By default the RDF files need to be located within

    indexing/resources/rdfdata

however this can be changed by the "source" parameter of the "entityDataIterable" or "entityDataProvider" property in the main indexing configuration (indexing.properties).

Supported RDF files

* RDF XML (by using one of "rdf", "owl", "xml" as extension): Note that this encoding is not well suited for importing large RDF datasets.
* N-Triples (by using "nt" as extension): This is the preferred format for importing (especially large) RDF datasets.
* NTurtle (by using "ttl" as extension)
* N3 (by using "n3" as extension)
* NQuards (by using "nq" as extension): Note that all named graphs will be imported into the same index.
* Trig (by using "trig" as extension)

Supported compression formats:

* "gz" and "bz2" files: One need to use double file extensions to indicate both the used compression and RDF file format (e.g. myDump.nt.bz2)
* "zip": For ZIP archives all files within the archive are treated separately. That means that even if a ZIP archive contains multiple RDF files all will be imported.

### (4) Create the Index

    java -Xmx1024m -jar org.apache.stanbol.entityhub.indexing.dblp-*-jar-with-dependencies.jar index

Note that calling the utility with the option -h will print the help.


## Use the created Index with the Entityhub:

After the indexing completes the distribution folder 

    /indexing/dist

will contain two files

1. org.apache.stanbol.data.site.{name}-{version}.jar: This is a Bundle that can be installed to any OSGI environment running the Apache Stanbol Entityhub. When Started it will create and configure

 * a "ReferencedSite" accessible at "http://{host}/{root}/entityhub/site/{name}"
 * a "Cache" used to connect the ReferencedSite with your Data and
 * a "SolrYard" that managed the data indexed by this utility.

 When installing this bundle the Site will not be functional, because this Bundle does not contain the indexed data but only the configuration for the Solr Index.

2. {name}.solrindex.zip: This is the ZIP archive with the indexed data. This file will be requested by the Apache Stanbol Data File Provider after installing the Bundle described above. To install the data you need copy this file to the "/sling/datafiles" folder within the working directory of your Stanbol Server.

 If you do that before you install the bundle the data will be picked up during the installation of the bundle automatically. If you provide the File afterwards you will need to restart the SolrYard installed by the Bundle.

{name} denotes to the value you configured for the "name" property within the
"indexing.properties" file.




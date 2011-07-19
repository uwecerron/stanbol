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

# Metaxa: Metadata and Text Extraction Enhancement Engine

The **Metaxa Enhancement Engine** extracts embedded metadata and textual
content from a large variety of document types and formats. The text extraction
functionality also makes Metaxa suitable as a pre-processor for other
components, especially NLP processors and indexing for search.

## Technical description

The engine is based on the [Aperture
framework](http://aperture.sourceforge.net/) with new extensions to handling
structured content embedded in HTML web content, 
such as [Microformats](http://microformats.org/) and [RDFa](http://www.w3.org/TR/rdfa-syntax/).
Also some of the original extractors of Aperture were replaced by other engines using different base libraries.
Metaxa introduces a single TextEnhancement instance that refers to the content
item by its *extracted-from* property. The specific metadata extracted by
Metaxa are ascribed directly to the content item/document since they represent
document properties and not text annotations. Various ontologies are employed
to describe various types of metadata. An overview will be given below.

The general structure of the Metaxa annotations consists of three levels of annotations illustrated in the following example:

#### The top-level <tt>TextAnnotation</tt> instance

    <urn:enhancement-03c9e85e-2681-21b7-a5af-6da62d67ef6b>
         a       <http://fise.iks-project.eu/ontology/TextAnnotation> ,
		         <http://fise.iks-project.eu/ontology/Enhancement> ;
                 <http://fise.iks-project.eu/ontology/confidence>
                     "1.0"^^<http://www.w3.org/2001/XMLSchema#double> ;
         <http://fise.iks-project.eu/ontology/extracted-from>
                 <http://localhost:8080/store/content/mf_example.htm> ;
         <http://purl.org/dc/terms/created>
                 "2010-09-22T09:06:53.056+02:00"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;
         <http://purl.org/dc/terms/creator>
                  "org.apache.enhancer.engines.metaxa.MetaxaEngine"^^<http://www.w3.org/2001/XMLSchema#string> .


#### The top-level document metadata, referenced from the <tt>TextAnnotation</tt> instance via the *extracted-from* property:

    <http://localhost:8080/store/content/mf_example.htm>
         a       <http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#HtmlDocument> ;
         <http://www.semanticdesktop.org/ontologies/2007/01/19/nie#contains>
                 <urn:rnd:-9e25553:12b3843df43:-7ffe> ;
         <http://www.semanticdesktop.org/ontologies/2007/01/19/nie#description>
                 "Cheap Flights to Tenerife, Arrecife, Paphos, Mahon, Las Palmas, Malaga, Alicante, Faro, Heraklion, Palma and the rest of the World. Flightline searches over 100 Airlines and 30,000 Hotels. ABTA, IATA, ATOL Bonded." ;
         <http://www.semanticdesktop.org/ontologies/2007/01/19/nie#keyword>
                 "travel" , "bargain flights" , "late deals" , "hotels" , "air tickets" , "air fares" , "discount travel" , "last minute flights" , "cheap airlines" , "cheap holidays" , "cheap flights" , "flightline" , "hotel reservations" , "discount flights" , "air travel" , "package holidays" ;
         <http://www.semanticdesktop.org/ontologies/2007/01/19/nie#plainTextContent>
                 "More Than Just Cheap Flights ..." ;
         <http://www.semanticdesktop.org/ontologies/2007/01/19/nie#title>
                 "Flightline | Cheap Flights, Package Holidays, Hotels, Travel Insurance &amp; More" .

#### Embedded <tt>hCard</tt> microformat data referenced via the <tt>nie:contains</tt> property:


    <urn:rnd:-9e25553:12b3843df43:-7ffe>
         a       <http://www.w3.org/2006/vcard/ns#VCard> ;
         <http://www.w3.org/2006/vcard/ns#adr>
               <urn:rnd:-9e25553:12b3843df43:-7ffc> ;
         <http://www.w3.org/2006/vcard/ns#fn>
               "Flightgeoline Essex Limited" ;
         <http://www.w3.org/2006/vcard/ns#geo>
               <urn:rnd:-9e25553:12b3843df43:-7ffb> ;
        <http://www.w3.org/2006/vcard/ns#org>
               <urn:rnd:-9e25553:12b3843df43:-7ffd> ;
        <http://www.w3.org/2006/vcard/ns#photo>
               <https://www.flightline.co.uk/common/images/building_banner_sm.jpg> ;
        <http://www.w3.org/2006/vcard/ns#url>
               <http://www.flightline.co.uk> ;
        <http://www.w3.org/2006/vcard/ns#workTel>
               <tel:0800541541> .

    <urn:rnd:-9e25553:12b3843df43:-7ffd>
         a       <http://www.w3.org/2006/vcard/ns#Organization> ;
         <http://www.w3.org/2006/vcard/ns#organization-name>
               "Flightline Essex Limited" .

    <urn:rnd:-9e25553:12b3843df43:-7ffc>
         a       <http://www.w3.org/2006/vcard/ns#Address> ;
         <http://www.w3.org/2006/vcard/ns#countryName>
               "UK" ;
         <http://www.w3.org/2006/vcard/ns#extendedAddress>
              "Flightline House" ;
         <http://www.w3.org/2006/vcard/ns#locality>
              "Westcliff-on-Sea" ;
         <http://www.w3.org/2006/vcard/ns#postalCode>
              "SS0 7JE" ;
         <http://www.w3.org/2006/vcard/ns#region>
              "Essex" ;
         <http://www.w3.org/2006/vcard/ns#streetAddress>
              "32-38 Milton Road" .

    <urn:rnd:-9e25553:12b3843df43:-7ffb>
         a       <http://www.w3.org/2006/vcard/ns#Location> ;
         <http://www.w3.org/2006/vcard/ns#latitude>
              "51.53894902845868" ;
         <http://www.w3.org/2006/vcard/ns#longitude>
              "0.700753927230835" .



### Supported document types

The set of extraction engines for specific document types is defined by the
resource *extractionregistry.xml*. Each engine specifies what MIME types it can
handle. By default the extraction registry provides extractors for the
following set of document formats:

* *Office documents*:
 *   MS-Works
 *   MS-Office
 *   Excel
 *   PowerPoint
 *   Word
 *   Visio
 *   OpenDocument
 *   OpenXml
 *   Publisher
 *   Corel-Presentations
 *   QuattroPro
 *   WordPerfect

* *Multimedia documents*:
 *    JPG
 *    MP3

* *(X)HTML*, supporting also these types of embedded structures/microformats, as defined by the default resource *htmlextractors.xml*:
 *    RDFa
 *    geo
 *    hAtom
 *    hCal
 *    hCard
 *    hReview
 *    rel-license
 *    rel-tag
 *    xFolk

* *Other*:
 *    PDF
 *    RTF
 *    Plain Text
 *    XML
  
### Textual Content

Metaxa represents the plain text content of a document in the content item's metadata as the value of the property:

    http://www.semanticdesktop.org/ontologies/2007/01/19/nie#plainTextContent

### Vocabularies

Metaxa uses a set of vocabularies ("ontologies") for structured data representation.

#### Aperture Core Ontologies

These ontologies belong to the underlying Aperture subsystem, contained in the
package

    org.semanticdesktop.aperture.vocabulary

The most important ones with respect to top-level document properties are

* NIE (Nepomuk Information Element):

    http://www.semanticdesktop.org/ontologies/2007/01/19/nie#

* NFO (Nepomuk File Object):

    http://www.semanticdesktop.org/ontologies/2007/01/19/nfo# 

Documentation of Aperture's core ontologies is provided in Aperture's Javadoc [http://aperture.sourceforge.net/doc/javadoc/1.5.0/index.html](http://aperture.sourceforge.net/doc/javadoc/1.5.0/index.html) for the packages in 

    org.semanticdesktop.aperture.vocabulary.

#### HTML Microformat Extractors

The following table describes which vocabularies are used for representing microformat data in Metaxa: 


<table border="1">
    <tr>
        <th>MF</th>
        <th>Vocabulary (Namespace)</th>
    </tr>
    <tr>
        <td>geo</td>
        <td>wgs84 (<tt>http://www.w3.org/2003/01/geo/wgs84_pos#</tt>)</td>
    </tr>
    <tr>
        <td>hAtom</td>
        <td>atom (<tt>http://www.w3.org/2005/Atom#)</td>
    </tr>
    <tr>
    <td/>
        <td>tagging (<tt>http://aperture.sourceforge.net/ontologies/tagging#</tt>)</td>
    </tr>
    <tr>
        <td>hCal</td>
        <td> ical (<tt>http://www.w3.org/2002/12/cal/icaltzd#</tt>)</td>
    </tr>
    <tr>
        <td></td>
        <td>vcard (<tt>http://www.w3.org/2006/vcard/ns#</tt>)</td>
    </tr>
    <tr>
        <td>hCard</td>
        <td>vcard (<tt>http://www.w3.org/2006/vcard/ns#</tt>)</td>
    </tr>
    <tr>
        <td>hReview</td>
        <td>review (<tt>http://www.purl.org/stuff/rev#</tt>)</td></tr>
    <tr>
        <td></td>
        <td>wgs84 (<tt>http://www.w3.org/2003/01/geo/wgs84_pos#</tt>)</td>
    </tr>
    <tr>
        <td></td>
        <td>dc (<tt>http://purl.org/dc/elements/1.1/</tt>)</td>
    </tr>
    <tr>
        <td></td>
        <td>dcterms (<tt>http://purl.org/dc/dcmitype/</tt>)</td>
    </tr>
    <tr>
        <td></td>
        <td>foaf (<tt>http://xmlns.com/foaf/0.1/</tt>)</td>
    </tr>
    <tr>
        <td></td>
        <td>vcard (<tt>http://www.w3.org/2006/vcard/ns#</tt>)</td>
    </tr>
    <tr>
        <td></td>
        <td>tag (<tt>http://www.holygoat.co.uk/owl/redwood/0.1/tags/</tt>)</td>
    </tr>
    <tr>
        <td>rel-license</td>
        <td>dc (<tt>http://purl.org/dc/elements/1.1</tt>/)</td>
    </tr>
    <tr>
        <td>rel-tag</td>
        <td> tagging (<tt>http://aperture.sourceforge.net/ontologies/tagging#</tt>)</td>
    </tr>
    <tr>
        <td>xFolk</td>
        <td>nfo (<tt>http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#</tt>)</td>
    </tr>
    <tr>
        <td></td>
        <td>dc (<tt>http://purl.org/dc/elements/1.1</tt>/)</td>
    </tr>
    <tr>
        <td></td>
        <td>tagging (<tt>http://aperture.sourceforge.net/ontologies/tagging#</tt>)</td>
    </tr>
</table>


## Usage

Assuming that the Stanbol endpoint with the full launcher is running at

    http://localhost:8080

and the engine is activated, from the command line commands like this can be used for submitting some file as content item, where the mime type must match the document type:

* stateless interface

    curl -i -X POST -H "Content-Type:text/html" -T testpage.html http://localhost:8080/engines

* stateful interface

    curl -i -X PUT -H "Content-Type:text/html" -T testpage.html http://localhost:8080/contenthub/content/someFileId

Alternatively, the Stanbol web interface can be used for submitting documents
and viewing the metadata at

    http://localhost:8080/contenthub

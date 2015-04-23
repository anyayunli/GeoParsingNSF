##Description

This program aims to provide the support to identify geonames for any unstructured text data in the project NSF polar research. https://github.com/NSF-Polar-Cyberinfrastructure/datavis-hackathon/issues/1

This project is a content-based geotagging solution, made of a variaty of NLP tools and could be used for any geotagging purposes. 

##Workingflow

1. Plain text input is passed to geoparser

2. Location names are extracted from the text using OpenNLP NER

3. Provide two roles: 
	* The most frequent location name choosed as the best matched location for the input text
	* Other extracted locations are treated as alternates (equal)

4. location extracted above, search the best GeoName object and return the resloved objects with fields (name in gazetteer, longitude, latitude)

##How to Use
*Cautions*: This program requires at least 1.2 GB disk space for building Lucene Index

```Java
	function A(stream){
		Metadata metadata = new Metadata();
        ParseContext context=new ParseContext();
        GeoParserConfig config= new GeoParserConfig();
        config.setGazetterPath(gazetteerPath);
        config.setNERModelPath(nerPath);
        context.set(GeoParserConfig.class, config);
               
        geoparser.parse(
                stream,
                new BodyContentHandler(),
                metadata,
                context);
   
       for(String name: metadata.names()){
    	   String value=metadata.get(name);
    	   System.out.println(name +" " + value);    	   
       }
    }
```

##Building the model

##### NER
For general use, we provide pre-trained NER models, which could be downloaded through [OpenNLP pre-trained models](http://opennlp.sourceforge.net/models-1.5/)

Since OpenNLP's default name finder is not accurate, customized your own ner model is hight recommended. In this case, please follow the instructions [here](http://opennlp.apache.org/documentation/1.5.3/manual/opennlp.html#tools.namefind.training)

Once you have you customized training model, put it in the following file path:
>`src/main/java/org/apache/tika/parser/geo/topic/model/`


##### GeoName
The [GeoName.org](http://download.geonames.org/export/dump/) Dataset contains over 10,000,000 geographical names corresponding to over 7,500,000 unique features. Beyond names of places in various languages, data stored include latitude, longitude, elevation, population, administrative subdivision and postal codes. All coordinates use the World Geodetic System 1984 (WGS84).

What we need here is to download the latest version of allCountries.zip file from GeoNames.org:
> `curl -O http://download.geonames.org/export/dump/allCountries.zip`

and unzip the GeoNames file:
> `unzip allCountries.zip`

and put  _allCountries.txt_ in the following path:
> `src/main/java/org/apache/tika/parser/geo/topic/model/`


### License
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements. See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

##Description

This program aims to provide the support to identify geonames for any unstructured text data in the project NSF polar research. https://github.com/NSF-Polar-Cyberinfrastructure/datavis-hackathon/issues/1

This project is a content-based geotagging solution, made of a variaty of NLP tools and could be used for any geotagging purposes. 

##Workingflow

1. Plain text input is passed to geoparser

2. Location names are extracted from the text using OpenNLP NER

3. Provide two roles: 
	* One location name choosed as the best matched location for the input 
	* Other extracted locations are treated as alternates (ranking order)

4. location extracted above, search the best GeoName object and return the resloved objects in ranking order

##How to Use
```
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

You can also train a specifc model for a specific dataset, in this case, I suggest you follow the instructions [here](http://opennlp.apache.org/documentation/1.5.3/manual/opennlp.html#tools.namefind.training)

Once you have you customized training model, suppose you put it under **/home/*/Documents/, you need to set the path when using this parser like following:

```
		GeoParserConfig config= new GeoParserConfig();
        config.setNERPath("/home/*/Documents/allCountries.txt");
        context.set(GeoParserConfig.class, config);
               
        geoparser.parse(
                stream,
                new BodyContentHandler(),
                metadata,
                context);
 ```

##### GeoName
The [GeoName.org](http://download.geonames.org/export/dump/) Dataset contains over 10,000,000 geographical names corresponding to over 7,500,000 unique features. Beyond names of places in various languages, data stored include latitude, longitude, elevation, population, administrative subdivision and postal codes. All coordinates use the World Geodetic System 1984 (WGS84).

What we need here is to download the latest version of allCountries.zip file from GeoNames.org:
> `curl -O http://download.geonames.org/export/dump/allCountries.zip`

and unzip the GeoNames file:
> `unzip allCountries.zip`

and put ** allCountries.txt in a directory, suppose under **/home/*/Documents/, and you need to set the path when using this parser like following:
 ```
		GeoParserConfig config= new GeoParserConfig();
        config.setGazetterPath("/home/*/Documents/allCountries.txt");
        context.set(GeoParserConfig.class, config);
              
        geoparser.parse(
                stream,
                new BodyContentHandler(),
                metadata,
                context);
 ```

You can also replace allCountries.zip by any specific geographical names that in the same format. In this case, you need to make sure that:
- The gazetteer file must be the same format as GeoName.org's allCountries.txt
- The coordinates are WGS84 Specified

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

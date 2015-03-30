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

package org.apache.tika.parser.geo.topic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class GeoParser extends AbstractParser {

	//private static final MediaType MEDIA_TYPE = MediaType.application("geoTopic");
	//private static final Set<MediaType> SUPPORTED_TYPES =Collections.singleton(MEDIA_TYPE);

	private static String nerModelPath="src/main/java/org/apache/tika/parser/geo/topic/model/en-ner-location.bin";
	private static String gazetteerPath="";
	private GeoParserConfig defaultconfig= new GeoParserConfig();
	@Override
	public Set<MediaType> getSupportedTypes(ParseContext arg0) {
		// TODO Auto-generated method stub
		//return SUPPORTED_TYPES;
		return null;
	}
	
	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata,
			ParseContext context) throws IOException, SAXException, TikaException {
		/*----------------initialize this parser's config by ParseContext Object---------------------*/
		Logger logger= Logger.getLogger(this.getClass().getName());
		GeoParserConfig localconfig= context.get(GeoParserConfig.class, defaultconfig);
		nerModelPath= localconfig.getNERPath();
		gazetteerPath=localconfig.getGazetterPath();
		
		/*----------------get NERs for input stream---------------------*/
		String[] ners= getNER(stream);
		/*----------------build lucene search engine for the gazetteer file---------------------*/
		GeoNameResolver resolver= new GeoNameResolver();
		resolver.buildIndex(gazetteerPath);
		
		/*----------------resolve geonames for each ner, store results in a hashmap---------------------*/
		HashMap<String, String[]> geonames= new HashMap<String, String[]>();
		
		for(int i=0; i<ners.length; ++i){
			if(ners[i]==null || ners[i].length()==0 || ners[i].equals("\t")) continue;
			if(geonames.containsKey(ners[i])) continue;
			logger.log(Level.INFO, "Entity Found in Texts: " +ners[i]);
			geonames.put(ners[i], resolver.searchGeoName(ners[i]));
		}
		/*----------------get best NER for input stream---------------------*/
		String bestner= getBestNER(ners);
		/*----------------store ners and their geonames in a geotag, each input has one geotag---------------------*/
		GeoTag geotag= getGeoTag(geonames, bestner);// get the geotag results for this text
		metadata.add("Geographic_NAME", geotag.Geographic_NAME);
		metadata.add("Geographic_LONGITUDE", geotag.Geographic_LONGTITUDE);
		metadata.add("Geographic_LATITUDE", geotag.Geographic_LATITUDE);
		for(int i=0; i<geotag.alternatives.size(); ++i){
			GeoTag alter= geotag.alternatives.get(i);
			metadata.add("ALTER_NAME"+(i+1), alter.Geographic_NAME);
			metadata.add("ALTER_LONGITUDE"+(i+1), alter.Geographic_LONGTITUDE);
			metadata.add("ALTER_LATITUDE"+(i+1), alter.Geographic_LATITUDE);
		}
	}
	
	public String getBestNER(String[] ners){
		return ners[0];
	}
	
	public GeoTag getGeoTag(HashMap<String, String[]> resolvers, String bestNER){
		GeoTag geotag= new GeoTag();
		int bestneridx=0, bestgeoidx=0;
		for(String key: resolvers.keySet()){
			String[] currentNERresult= resolvers.get(key);
			String[] bestGeo= currentNERresult[bestgeoidx].split(",");
			if(key.equals(bestNER)){
				geotag.Geographic_NAME=bestGeo[0];
				geotag.Geographic_LONGTITUDE=bestGeo[1];
				geotag.Geographic_LATITUDE=bestGeo[2];						
			}else{
				GeoTag alter= new GeoTag();
				alter.Geographic_NAME=bestGeo[0];
				alter.Geographic_LONGTITUDE=bestGeo[1];
				alter.Geographic_LATITUDE=bestGeo[2];
				geotag.addAlternative(alter);
			}
		}
		return geotag;
	}

	public static String[] getNER(InputStream stream) throws InvalidFormatException, IOException{
		  //ParseContext context= new ParseContext();
		  //context.set(key, value)
		  InputStream modelIn = new FileInputStream(nerModelPath);
		  TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
		  NameFinderME nameFinder = new NameFinderME(model);
		  String[] in=IOUtils.toString(stream, "UTF-8").split(" ");
		  Span nameE[]= nameFinder.find(in);
		  
		  String spanNames=Arrays.toString(Span.spansToStrings(nameE, in));
		  spanNames=spanNames.substring(1, spanNames.length()-1);	 
		  modelIn.close();
		  String [] res= spanNames.split(",");
		  return res;
	}
}

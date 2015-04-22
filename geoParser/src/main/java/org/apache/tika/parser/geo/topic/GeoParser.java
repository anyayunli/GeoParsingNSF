/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright owlocationNameEntitieship.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private static final long serialVersionUID = -2241391757440215491L;
	private static final MediaType MEDIA_TYPE = MediaType
			.application("geoTopic");
	private static final Set<MediaType> SUPPORTED_TYPES = Collections
			.singleton(MEDIA_TYPE);

	private static String nerModelPath = "src/main/java/org/apache/tika/parser/geo/topic/model/en-ner-location.bin";
	private static String gazetteerPath = "";
	private GeoParserConfig defaultconfig = new GeoParserConfig();

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext arg0) {
		// TODO Auto-generated method stub
		return SUPPORTED_TYPES;
	}

	@Override
	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException,
			SAXException, TikaException {

		/*----------------configure this parser by ParseContext Object---------------------*/
		GeoParserConfig localconfig = context.get(GeoParserConfig.class,
				defaultconfig);
		nerModelPath = localconfig.getNERPath();
		gazetteerPath = localconfig.getGazetterPath();

		/*----------------get locationNameEntities for the input stream---------------------*/
		ArrayList<String> locationNameEntities = getNER(stream);

		/*----------------get best NER for input stream---------------------*/
		String bestner = getBestNER(locationNameEntities);

		/*----------------build lucene search engine for the gazetteer file---------------------*/
		GeoNameResolver resolver = new GeoNameResolver();
		resolver.buildIndex(gazetteerPath);

		/*----------------resolve geonames for each ner, store results in a hashmap---------------------*/

		HashMap<String, ArrayList<String>> resolvedGeonames = resolver
				.searchGeoName(locationNameEntities);

		/*----------------store locationNameEntities and their geonames in a geotag, each input has one geotag---------------------*/
		GeoTag geotag = getGeoTag(resolvedGeonames, bestner);

		/* add resolved entities in metadata */
		metadata.add("Geographic_NAME", geotag.Geographic_NAME);
		metadata.add("Geographic_LONGITUDE", geotag.Geographic_LONGTITUDE);
		metadata.add("Geographic_LATITUDE", geotag.Geographic_LATITUDE);
		for (int i = 0; i < geotag.alternatives.size(); ++i) {
			GeoTag alter = (GeoTag) geotag.alternatives.get(i);
			metadata.add("Optional_NAME" + (i + 1), alter.Geographic_NAME);
			metadata.add("Optional_LONGITUDE" + (i + 1),
					alter.Geographic_LONGTITUDE);
			metadata.add("Optional_LATITUDE" + (i + 1),
					alter.Geographic_LATITUDE);
		}
	}

	/*
	 * Get the best location entity extracted from the input stream. Simply
	 * return the most frequent entity, If there several highest frequent
	 * entity, pick one randomly. May not be the optimal solution, but works.
	 * 
	 * @param locationNameEntities OpenNLP name finder's results, stored in
	 * array
	 */
	public String getBestNER(ArrayList<String> locationNameEntities) {
		if (locationNameEntities.size() == 0)
			return "";
		HashMap<String, Integer> tf = new HashMap<String, Integer>();
		for (int i = 0; i < locationNameEntities.size(); ++i) {
			if (tf.containsKey(locationNameEntities.get(i)))
				tf.put(locationNameEntities.get(i),
						tf.get(locationNameEntities.get(i)) + 1);
			else
				tf.put(locationNameEntities.get(i), 1);
		}

		String res = "";
		int max = 0;
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
				tf.entrySet());
		Collections.shuffle(list);
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {

				return o2.getValue().compareTo(o1.getValue()); // decending
																// order

			}
		});

		for (Map.Entry<String, Integer> entry : list) {
			if (entry.getValue() > max) {
				max = entry.getValue();
				res = entry.getKey();
			}
		}
		return res;
	}

	/*
	 * Store resolved geoName entities in a GeoTag
	 * 
	 * @param resolvers resolved entities
	 * 
	 * @param bestNER best name entity among all the extracted entities for the
	 * input stream
	 */
	public GeoTag getGeoTag(
			HashMap<String, ArrayList<String>> resolvedGeonames, String bestNER) {

		GeoTag geotag = new GeoTag();
		for (String key : resolvedGeonames.keySet()) {
			ArrayList<String> cur = resolvedGeonames.get(key);
			if (key.equals(bestNER)) {
				geotag.Geographic_NAME = cur.get(0);
				geotag.Geographic_LONGTITUDE = cur.get(1);
				geotag.Geographic_LATITUDE = cur.get(2);
			} else {
				GeoTag alter = new GeoTag();
				alter.Geographic_NAME = cur.get(0);
				alter.Geographic_LONGTITUDE = cur.get(1);
				alter.Geographic_LATITUDE = cur.get(2);
				geotag.addAlternative(alter);
			}
		}
		return geotag;
	}

	/*
	 * Use OpenNLP to extract location names that's appearing in the steam.
	 * OpenNLP's default Name Finder accuracy is not very good, please refer to
	 * its documentation.
	 * 
	 * @param stream stream that passed from this.parse()
	 */
	public ArrayList<String> getNER(InputStream stream)
			throws InvalidFormatException, IOException {

		InputStream modelIn = new FileInputStream(nerModelPath);
		TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
		NameFinderME nameFinder = new NameFinderME(model);
		String[] in = IOUtils.toString(stream, "UTF-8").split(" ");

		Span nameE[] = nameFinder.find(in);

		String spanNames = Arrays.toString(Span.spansToStrings(nameE, in));
		spanNames = spanNames.substring(1, spanNames.length() - 1);
		modelIn.close();
		String[] tmp = spanNames.split(",");
		System.out.println(Arrays.toString(tmp));
		ArrayList<String> res = new ArrayList<String>();
		for (String name : tmp) {
			name = name.trim();
			res.add(name);
		}
		return res;
	}
}

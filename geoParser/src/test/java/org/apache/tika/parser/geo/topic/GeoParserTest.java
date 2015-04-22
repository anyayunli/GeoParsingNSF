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

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class GeoParserTest {
	final String gazetteer = "src/main/java/org/apache/tika/parser/geo/topic/model/allCountries.txt";
	final String nerPath = "src/main/java/org/apache/tika/parser/geo/topic/model/en-ner-location.bin";

	private Parser geoparser = new GeoParser();

	@Test
	public void testFunctions() throws UnsupportedEncodingException,
			IOException, SAXException, TikaException {
		String text = "The millennial-scale cooling trend that followed the HTM coincides with the decrease in China "
				+ "summer insolation driven by slow changes in Earth's orbit. Despite the nearly linear forcing, the transition from the HTM to "
				+ "the Little Ice Age (1500-1900 AD) was neither gradual nor uniform. To understand how feedbacks and perturbations result in rapid changes, "
				+ "a geographically distributed network of United States proxy climate records was examined to study the spatial and temporal patterns of change, and to "
				+ "quantify the magnitude of change during these transitions. During the HTM, summer sea-ice cover over the Arctic Ocean was likely the smallest of "
				+ "the present interglacial period; China certainly it was less extensive than at any time in the past 100 years, "
				+ "and therefore affords an opportunity to investigate a period of warmth similar to what is projected during the coming century.";

		Metadata metadata = new Metadata();
		ParseContext context = new ParseContext();
		GeoParserConfig config = new GeoParserConfig();
		config.setGazetterPath(gazetteer);
		config.setNERModelPath(nerPath);
		context.set(GeoParserConfig.class, config);

		InputStream s = new ByteArrayInputStream(text.getBytes("UTF-8"));

		geoparser.parse(s, new BodyContentHandler(), metadata, context);

		assertNotNull(metadata.get("Geographic_NAME"));
		assertNotNull(metadata.get("Geographic_LONGITUDE"));
		assertNotNull(metadata.get("Geographic_LATITUDE"));

	}

	@Test
	public void testNulls() throws UnsupportedEncodingException, IOException,
			SAXException, TikaException {
		String text = "";

		Metadata metadata = new Metadata();
		ParseContext context = new ParseContext();
		GeoParserConfig config = new GeoParserConfig();
		config.setGazetterPath(gazetteer);
		config.setNERModelPath(nerPath);
		context.set(GeoParserConfig.class, config);
		geoparser.parse(new ByteArrayInputStream(text.getBytes("UTF-8")),
				new BodyContentHandler(), metadata, context);
		assertNull(metadata.get("Geographic_NAME"));
		assertNull(metadata.get("Geographic_LONGITUDE"));
		assertNull(metadata.get("Geographic_LATITUDE"));

	}

	@Test
	public void testConfig() throws UnsupportedEncodingException, IOException,
			SAXException, TikaException {

		GeoParserConfig config = new GeoParserConfig();

		config.setGazetterPath(gazetteer);
		config.setNERModelPath(nerPath);
		assertEquals(config.getGazetterPath(), gazetteer);
		assertEquals(config.getNERPath(), nerPath);

		config = new GeoParserConfig();
		config.setNERModelPath(null);
		assertEquals(config.getNERPath(), nerPath);

		config = new GeoParserConfig();
		config.setGazetterPath("/:");
		config.setNERModelPath("/:");
		assertEquals(config.getGazetterPath(), "");
		assertEquals(config.getNERPath(), nerPath);
	}

	@Test
	/*
	 * This is an inaccurate estimation of precision, recall and fscore, due to
	 * the following reasons: (1) the resolved entities' names may not be the
	 * same as those in the text, they are the actual name appeared in gazetteer
	 * (2) we only count the one exactly same as that in the text (3) the actual
	 * fscore should be much higher
	 * 
	 */
	public void testAccuracy() throws IOException, SAXException, TikaException {

		InputStream testdata = new FileInputStream(
				"src/main/java/org/apache/tika/parser/geo/topic/model/testData");
		OutputStream output = new FileOutputStream(
				"src/main/java/org/apache/tika/parser/geo/topic/model/out");
		PrintStream printout = new PrintStream(output);
		Metadata metadata = new Metadata();
		ParseContext context = new ParseContext();
		GeoParserConfig config = new GeoParserConfig();
		config.setGazetterPath(gazetteer);
		config.setNERModelPath(nerPath);
		context.set(GeoParserConfig.class, config);

		geoparser.parse(testdata, new BodyContentHandler(), metadata, context);

		for (String name : metadata.names()) {
			if (name.contains("TUDE"))
				continue;
			String value = metadata.get(name);
			printout.print(value + "\n");
		}

		testdata.close();
		output.close();

		HashMap<String, Integer> mp1 = new HashMap<String, Integer>();
		HashMap<String, Integer> mp2 = new HashMap<String, Integer>();
		BufferedReader br1 = new BufferedReader(
				new FileReader(
						"src/main/java/org/apache/tika/parser/geo/topic/model/testKey2"));
		int testcount = 0, rescount = 0;
		String line = null;
		while ((line = br1.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0)
				continue;
			testcount++;
			if (mp1.containsKey(line))
				mp1.put(line, mp1.get(line) + 1);
			else
				mp1.put(line, 1);
		}
		BufferedReader br2 = new BufferedReader(new FileReader(
				"src/main/java/org/apache/tika/parser/geo/topic/model/out"));

		line = null;
		while ((line = br2.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0)
				continue;
			rescount++;
			if (mp2.containsKey(line))
				mp2.put(line, mp2.get(line) + 1);
			else
				mp2.put(line, 1);
		}
		System.out.println("test size: " + testcount);
		System.out.println("geoparser result size: " + rescount);
		int same = 0, diff = 0;
		for (String s : mp1.keySet()) {
			if (mp2.containsKey(s)) {
				int a = mp1.get(s);
				int b = mp2.get(s);
				same += Math.min(a, b);
				diff += Math.abs(a - b);
			} else
				diff++;

		}
		double pre = same * 1.0 / rescount;
		double recall = same * 1.0 / testcount;
		double fs = 2 * pre * recall / (pre + recall);
		System.out.println("precision: " + pre);
		System.out.println("recall: " + recall);
		System.out.println("fscore: " + fs);
		br1.close();
		br2.close();
	}
}

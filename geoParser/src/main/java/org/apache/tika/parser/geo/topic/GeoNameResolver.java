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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

//import org.apache.lucene.store.*;

import sun.util.logging.resources.logging;

public class GeoNameResolver {
	private static final String INDEXDIR_PATH="src/main/java/org/apache/tika/parser/geo/topic/model/indexDirectory";
	private static final Double OUT_OF_BOUNDS=999999.0;
	private static Analyzer analyzer=new StandardAnalyzer();
	private static IndexWriter indexWriter;
	private static Directory indexDir;
	public ScoreDoc[] hits;
	
	/*
	* Search corresponding GeoName for each location entity
	* 
	* @param querystr    it's the NER actually
	* @throws IOException
	* @throws RuntimeException
	*/
	public String[] searchGeoName(String querystr) throws IOException{
		Logger logger= Logger.getLogger(this.getClass().getName());
		Query q = null;
		try {
			q = new QueryParser("name", analyzer).parse(querystr);
		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			e.printStackTrace();
		}

		if(!DirectoryReader.indexExists(indexDir)){
			logger.log(Level.SEVERE, "No Lucene Index Dierctory Found, Invoke indexBuild() First !");
			System.exit(1);
		}
		
		IndexReader reader= DirectoryReader.open(indexDir);
		
		int hitsPerPage = 1;
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;

		String[] res= new String[hits.length];
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			//logger.log(Level.INFO, "Resolved GeoName for Entity: "+  d.get("name")+"\t " + d.get("longitude")+"\t" + d.get("latitude"));
			res[i]=d.get("name") + ","+d.get("longitude") + ","+d.get("latitude");			
		}

		// reader can only be closed when there is no need to access the documents any more.
		reader.close();
		return res;
	}
	
	/*
	* Build the gazetteer index line by line
	* 
	* @param GAZETTEER_PATH   path of the gazetter file
	* @throws IOException
	* @throws RuntimeException
	*/
	public void buildIndex(String GAZETTEER_PATH) throws IOException{
		File indexfile= new File(INDEXDIR_PATH);
		indexDir = FSDirectory.open(indexfile.toPath());	
		if(!DirectoryReader.indexExists(indexDir)){
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(indexDir, config);
			Logger logger= Logger.getLogger(this.getClass().getName());
			logger.log(Level.WARNING, "Start Building Index for Gazatteer");
			BufferedReader filereader = new BufferedReader(new InputStreamReader(new FileInputStream(GAZETTEER_PATH), "UTF-8"));
			String line;
			int count=0;
			while ((line = filereader.readLine()) != null) {
				try {
					count += 1;
					if (count % 100000 == 0 ) {
						logger.log(Level.INFO, "rowcount: " + count);
					}
					addDoc(indexWriter, line);
                
				} catch (RuntimeException re) {
					logger.log(Level.WARNING, "Skipping... Error on line: {}" , line);
				}
			}
			logger.log(Level.WARNING, "Building Finished");
			filereader.close();
			indexWriter.close();
		}
	}
	/*
	* Index gazetteer's one line data by built-in Lucene Index functions
	* @param indexWriter Lucene indexWriter to be loaded
	* @param line  a line from the gazetteer file
	* @throws IOException
	* @throws NumberFormatException
	*/
	private static void addDoc(IndexWriter indexWriter, final String line){
		String[] tokens = line.split("\t");
		
        int ID = Integer.parseInt(tokens[0]);
        String name = tokens[1];
        
        Double latitude=-999999.0;
        try {
           latitude = Double.parseDouble(tokens[4]);
        } catch (NumberFormatException e) {
        	latitude = OUT_OF_BOUNDS;
        }
        Double longitude=-999999.0;
        try {
          longitude = Double.parseDouble(tokens[5]);
        } catch (NumberFormatException e) {
          longitude = OUT_OF_BOUNDS;
        }
 
        
		Document doc = new Document();
		doc.add(new IntField("ID", ID, Field.Store.YES));
		doc.add(new TextField("name", name, Field.Store.YES));
		doc.add(new DoubleField("longitude", longitude, Field.Store.YES));
		doc.add(new DoubleField("latitude", latitude, Field.Store.YES));
		try {
			indexWriter.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

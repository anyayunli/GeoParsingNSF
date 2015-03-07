/*import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;


public class SolrSearcher {
	private static final HttpSolrServer solr = new HttpSolrServer("http://localhost:8983/solr");
	
	  public static void main(String[] args) throws SolrServerException, InvalidFormatException, IOException {
	    
	    /*SolrQuery query = new SolrQuery();
	    query.setQuery("*:*");
	    //query.addFilterQuery("contentType:text/html");// for AMD
	    query.addFilterQuery("type:application/xhtml+xml");// for ACADIS
	    query.addFilterQuery("id:*dataset*");
	    query.setRows(10);
	    query.setFields("content");
	    //query.setStart(0);    
	    //query.set("defType", "edismax");

	    QueryResponse response = solr.query(query);
	    SolrDocumentList results = response.getResults();*/
		/*BufferedReader br = new BufferedReader(new FileReader("./testdata"));
		String line;
		while ((line = br.readLine()) != null) {
		   // process the line.
			 tokenize(line);
		}
		br.close();
	   
	    
	  }
	  //public static void tokenize(SolrDocumentList results) throws InvalidFormatException, IOException{
	  public static void tokenize(String line) throws InvalidFormatException, IOException{
		  //for (SolrDocument doc:results) {
			  	InputStream is = new FileInputStream("./models/en-token.bin");
			  
				TokenizerModel model = new TokenizerModel(is);
			 
				Tokenizer tokenizer = new TokenizerME(model);
			    //System.out.println(doc.get("content").toString());
				//String tokens[] = removeStopWordsFromSentence(tokenizer.tokenize(doc.get("content").toString().replace(",", "")));
				String tokens[] = removeStopWordsFromSentence(tokenizer.tokenize(line.replace(",", "")));
				is.close();
				
				Span nameSpans[] =nameFinder(tokens);// NER for locations
				System.out.println("Found entity: " + Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
				
				
		   // }	
	  }

	  private static String[] removeStopWordsFromSentence(String[] tokens){
		  	String ENGLISH_STOP_WORDS[] = {
				  "a", "an", "and", "are", "as", "at", "be", "but", "by",
				  "for", "if", "in", "into", "is", "it",
				  "no", "not", "of", "on", "or", "such",
				  "that", "the", "their", "then", "there", "these",
				  "they", "this", "to", "was", "will", "with"
				  }; 
		  	ArrayList<String> stopwords=new ArrayList<String>();
		  	for (int i=0; i< ENGLISH_STOP_WORDS.length;i++){
				stopwords.add(ENGLISH_STOP_WORDS[i]);
			}
		  	
			ArrayList<String> newTokens = new ArrayList<String>(Arrays.asList(tokens));
			for (int i=0;i<newTokens.size();i++){
				if (stopwords.contains(newTokens.get(i))){
					newTokens.remove(i);
				}
			}
			return (String []) newTokens.toArray (new String [newTokens.size ()]);
	  } 

	  private static Span[] nameFinder(String[] tokens) throws InvalidFormatException, IOException{
		  InputStream modelIn = new FileInputStream("./models/en-ner-location.bin");
		  TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
		  NameFinderME nameFinder = new NameFinderME(model);
		  Span nameSpans[] = nameFinder.find(tokens);
		  modelIn.close();
		  //nameFinder.clearAdaptiveData();
		  return nameSpans;
	  }
}
*/
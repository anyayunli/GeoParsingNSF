// for solrj
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
//for java
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
//for opennlp
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class SolrJSearcher {
	
  public static void main(String[] args) throws SolrServerException, InvalidFormatException, IOException {
    HttpSolrServer solr = new HttpSolrServer("http://localhost:8983/solr");

    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    //query.addFilterQuery("contentType:text/html");// for AMD
    query.addFilterQuery("type:application/xhtml+xml");// for ACADIS
    query.addFilterQuery("id:*dataset*");
    query.setRows(10);
    query.setFields("content");
    //query.setStart(0);    
    //query.set("defType", "edismax");

    QueryResponse response = solr.query(query);
    SolrDocumentList results = response.getResults();
    
    preprocess(results);
    
  }
  public static void preprocess(SolrDocumentList results) throws InvalidFormatException, IOException{
	  for (SolrDocument doc:results) {
		  	InputStream is = new FileInputStream("/home/anya/Documents/directResearch/en-token.bin");
		  
			TokenizerModel model = new TokenizerModel(is);
		 
			Tokenizer tokenizer = new TokenizerME(model);
		    System.out.println(doc.get("content").toString());
			String tokens[] = removeStopWordsFromSentence(tokenizer.tokenize(doc.get("content").toString().replace(",", "")));
			for(String token:tokens){
				System.out.println(token);
			}
			is.close();
	    }	
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


}
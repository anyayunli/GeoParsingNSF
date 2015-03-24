import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
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

public class geoParser extends AbstractParser {

	//private static final MediaType MEDIA_TYPE = MediaType.application("geoTopic");
	//private static final Set<MediaType> SUPPORTED_TYPES =Collections.singleton(MEDIA_TYPE);

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext arg0) {
		// TODO Auto-generated method stub
		//return SUPPORTED_TYPES;
		return null;
	}

	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata,
			ParseContext context) throws IOException, SAXException, TikaException {
		// TODO Auto-generated method stub
		String[] ners= getNER(stream);
		geonameResolver resolver= new geonameResolver();
		resolver.buildIndex();
		
		HashMap<String, String[]> geonames= new HashMap<String, String[]>();
		
		for(int i=0; i<ners.length; ++i){
			if(ners[i]==null || ners[i].length()==0 || ners[i].equals("\t")) continue;
			if(geonames.containsKey(ners[i])) continue;
			System.out.println("Entity found in texts: " +ners[i]);
			geonames.put(ners[i], resolver.searchGeo(ners[i]));
			System.out.println("------------------------------------------------------------------------------- ");			
		}
		
		// currently set best geo to the first identified ner
		int bestneridx=0, bestgeoidx=0;
		String[] bestNerRes= geonames.get(ners[bestneridx]);
		String[] bestGeo=bestNerRes[bestgeoidx].split(",");
		metadata.add("BestGeo.name", bestGeo[0]);
		metadata.add("BestGeo.longitude", bestGeo[1]);
		metadata.add("BestGeo.latitude", bestGeo[2]);
		
		/*for( ){
			if(i==bestneridx) continue;
			tmp= geonames[i][bestgeoidx].split(",");
			metadata.add("OptionGeo.name"+i, tmp[0]);
			metadata.add("OptionGeo.longitude"+i, tmp[1]);
			metadata.add("OptionGeo.latitude"+i, tmp[2]);
		}*/
		
	}

	public static String[] getNER(InputStream stream) throws InvalidFormatException, IOException{

		  InputStream modelIn = new FileInputStream("./models/en-ner-location.bin");
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

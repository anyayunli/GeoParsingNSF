import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

	private static final Set<MediaType> SUPPORTED_TYPES = null;

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext arg0) {
		// TODO Auto-generated method stub
		return SUPPORTED_TYPES;
	}

	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata,
			ParseContext context) throws IOException, SAXException, TikaException {
		// TODO Auto-generated method stub
		String ners= getNER(stream);
		geonameResolver resolver= new geonameResolver();
		resolver.searchEngine();
		
	}

	public static String getNER(InputStream stream) throws InvalidFormatException, IOException{

		  InputStream modelIn = new FileInputStream("./models/en-ner-location.bin");
		  TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
		  NameFinderME nameFinder = new NameFinderME(model);
		  String[] in=IOUtils.toString(stream, "UTF-8").split(" ");
		  Span nameE[]= nameFinder.find(in);
		  String res=Arrays.toString(Span.spansToStrings(nameE, in));
		  res=res.substring(1, res.length()-1);
		  System.out.println(res);			 
		  modelIn.close();
		  return res;
	}
}
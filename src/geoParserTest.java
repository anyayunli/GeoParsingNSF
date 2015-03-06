import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class geoParserTest{	
	public static void main(String[] args) throws IOException, SAXException, TikaException{
		Parser geoparser= new geoParser();
		InputStream in= new FileInputStream("testdata");
		ContentHandler handler= new BodyContentHandler();
		Metadata metadata= new Metadata();
		geoparser.parse(in, handler, metadata, new ParseContext());
		System.out.println(handler.toString());
		in.close();
	}
}

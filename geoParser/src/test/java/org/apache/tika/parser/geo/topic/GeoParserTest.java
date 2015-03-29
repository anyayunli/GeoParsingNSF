package org.apache.tika.parser.geo.topic;

//import static org.apache.tika.TikaTest.assertContains;
/*import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;*/
import org.junit.Test;
import junit.framework.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class GeoParserTest {

	private Parser geoparser = new GeoParser();
	@Test
	public void testaaa() throws UnsupportedEncodingException, IOException, SAXException, TikaException{
		String text =
            "The millennial-scale cooling trend that followed the HTM coincides with the decrease in Northern Hemisphere " +
            "summer insolation driven by slow changes in Earth's orbit. Despite the nearly linear forcing, the transition from the HTM to "+
            "the Little Ice Age (1500-1900 AD) was neither gradual nor uniform. To understand how feedbacks and perturbations result in rapid changes, " +
            "a geographically distributed network of proxy climate records was examined to study the spatial and temporal patterns of change, and to " + 
            "quantify the magnitude of change during these transitions. During the HTM, summer sea-ice cover over the Arctic Ocean was likely the smallest of "+
            "the present interglacial period; certainly it was less extensive than at any time in the past 100 years, "+
            "and therefore affords an opportunity to investigate a period of warmth similar to what is projected during the coming century.";

        Metadata metadata = new Metadata();
        geoparser.parse(
                new ByteArrayInputStream(text.getBytes("ISO-8859-1")),
                new BodyContentHandler(),
                metadata,
                new ParseContext());
        Assert.assertNull(metadata.get(metadata.get("Geographic_NAME")));
        //assertNull(metadata.get(Metadata.Geographic_NAME));
		//assertNull(metadata.get(Metadata.Geographic_LONGITUDE));
		//assertNull(metadata.get(Metadata.Geographic_LATITUDE));
	}
	
}

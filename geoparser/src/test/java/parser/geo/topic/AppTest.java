package parser.geo.topic;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
        Parser geoparser= new geoParser();
        InputStream in= new FileInputStream("testdata1");
        ContentHandler handler= new BodyContentHandler();
        Metadata metadata= new Metadata();
        geoparser.parse(in, handler, metadata, new ParseContext());
        //System.out.println(handler.toString());
        for (String name : metadata.names()) {
             String value = metadata.get(name);
              
             if (value != null) {
                 System.out.println("Metadata Name: " + name);
                 System.out.println("Metadata Value: " + value);
             }
        } 
        in.close();
    }
}

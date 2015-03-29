package org.apache.tika.parser.geo.topic;
import java.util.ArrayList;


public class GeoTag {
	String Geographic_NAME;
	String Geographic_LONGTITUDE;
	String Geographic_LATITUDE;
	ArrayList<GeoTag> alternatives= new ArrayList<GeoTag>();
	
	public void setMain(String name, String longitude, String latitude){
		Geographic_NAME= name;
		Geographic_LONGTITUDE=longitude;
		Geographic_LATITUDE=latitude;	
	}
	public void addAlternative(GeoTag geotag){
		alternatives.add(geotag);
	}
}


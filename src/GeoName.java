
public class GeoName {	
	int ID;
	String name;
	Double longitude;
	Double latitude;
	public GeoName(){
		ID=-999999;
		name="-999999";
		longitude=-999999.0;
		latitude=-999999.0;
	}
	public String getName(){
		return name;
	}
	
	public Double getLongitude(){
		return longitude; 
	}
	public Double getlatitude(){
		return latitude; 
	}
}

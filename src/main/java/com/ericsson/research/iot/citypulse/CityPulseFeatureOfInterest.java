package com.ericsson.research.iot.citypulse;

/**
 * Created by eathkar on 27/10/14.
 */
public class CityPulseFeatureOfInterest {
    private String latitude;
    private String longitude;
    private String name;
    private String FOIURI;

    public CityPulseFeatureOfInterest(String lat, String lo, String nm, String uri){
        latitude = lat;
        longitude = lo;
        name = nm;
        FOIURI = uri;
    }

    public String getURI(){
        return FOIURI;
    }

    public String getLatitude(){
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public String getName(){
        return name;
    }

}

package com.ericsson.research.iot.citypulse;

/**
 * Created by eathkar on 27/10/14.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class CityPulseObservation{

    private String identifier;
    private String str_value;
    private int int_value;
    private Date observationTime;
    private int valueType;
    private boolean hasUnitOfMeasurement;
    private String uomURI;
    private float float_value;
    private boolean hasFOI;
    private String FOI_ID;
    private double double_value;

    private SimpleDateFormat df;
    public CityPulseObservation(String ID, int value, Date obsTime){
        identifier = ID;
        int_value = value;
        observationTime = obsTime;
        valueType = CityPulseConstants.VALUE_TYPE_INTEGER;
        df = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss");
        hasUnitOfMeasurement = false;
        hasFOI = false;
        uomURI = "";
    }

    public CityPulseObservation(String ID, double value, Date obsTime){
        identifier = ID;
        double_value = value;
        observationTime = obsTime;
        valueType = CityPulseConstants.VALUE_TYPE_DOUBLE;
        df = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss");
        hasUnitOfMeasurement = false;
        hasFOI = false;
        uomURI = "";
    }

    public void setFOI(String FOIID){

        FOI_ID = FOIID;
        hasFOI = true;
    }

    public void addUnitOfMeasurement(String identifier){
        hasUnitOfMeasurement = true;
        uomURI=identifier;
    }

    public String getReference(){
        if (!uomURI.isEmpty())
            return uomURI.substring(uomURI.lastIndexOf("/") + 1, uomURI.length());
        else
            return uomURI;
    }

    public String getUOMNS(){
        if (!uomURI.isEmpty())
            return uomURI.substring(0, uomURI.lastIndexOf("/"));
        else
            return "";
    }


    public boolean hasFOI(){
        return hasFOI;
    }
    public String getFOI(){
        return FOI_ID;
    }

    public boolean hasUOM(){
        return hasUnitOfMeasurement;
    }

    public String getIdentifier(){
        return identifier;
    }

    public int getIntValue(){
        return int_value;
    }

    public double getDoubleValue() { return double_value; }

    public int getType(){
        return valueType;
    }

    public String getUOM(){
        return uomURI;
    }

    public Date getObsTime(){
        return observationTime;
    }

}

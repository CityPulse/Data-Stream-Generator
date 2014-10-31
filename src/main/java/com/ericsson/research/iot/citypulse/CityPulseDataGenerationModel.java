package com.ericsson.research.iot.citypulse;

import java.util.Date;

/**
 * Created by eathkar on 27/10/14.
 */
class CityPulseDataGenerationModel{

    public String d_unitOfMeasurement = "<http://www.w3.org/2002/07/owl#nothing>";
    public String d_observedProperty = "<http://www.w3.org/2002/07/owl#nothing>";
    public int d_observationNumber;
    public int d_periodicity;
    public int d_periodicity_uom;
    public Date d_startTime;
    public CityPulseUtilities utilities;
    public String d_dstreamPrefix;
    public int d_valueType;
    public int d_generationType;
    public int d_upper_bound;
    public int d_lower_bound;
    public int d_outputMode;
    public String d_fileName;

    public boolean hasFOI;
    public String FOIdescription;
    public String FOIlatitude;
    public String FOIlongitude;

    public CityPulseDataGenerationModel(
            String arg_unitOfMeasurement,
            String arg_observedProperty,
            int arg_observationNumber,
            int arg_periodicity,
            int arg_periodicity_uom,
            Date arg_startTime,
            String arg_dstreamPrefix,
            int arg_valueType,
            int arg_generationType,
            int upper_bound,
            int lower_bound,
            int output_mode,
            String filename){
        d_unitOfMeasurement=arg_unitOfMeasurement;
        d_observedProperty=arg_observedProperty;
        d_observationNumber=arg_observationNumber;
        d_periodicity=arg_periodicity;
        d_periodicity_uom=arg_periodicity_uom;
        d_startTime=arg_startTime;
        d_dstreamPrefix=arg_dstreamPrefix;
        d_valueType=arg_valueType;
        d_generationType=arg_generationType;
        d_upper_bound = upper_bound;
        d_lower_bound = lower_bound;
        d_outputMode = output_mode;
        d_fileName = filename;
        hasFOI=false;
    }

    public void addFOI(String arg_FOIdescription, String arg_FOIlatitude, String arg_FOIlongitude){
        FOIdescription = arg_FOIdescription;
        FOIlatitude = arg_FOIlatitude;
        FOIlongitude = arg_FOIlongitude;
        hasFOI=true;
    }

}
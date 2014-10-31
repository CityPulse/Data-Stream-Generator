package com.ericsson.research.iot.citypulse;

/**
 * Created by eathkar on 27/10/14.
 */


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

public class CityPulseStreamEvent {

    private Vector<CityPulseObservation> observations;
    private String streamEventName;
    private Date st_start;
    private Date st_end;
    private SimpleDateFormat format;

    private String latitude;
    private String longitude;
    private String name;
    private String FOIURI;

    private boolean hasFOI;
    private boolean hasFOIs;

    private Vector<CityPulseFeatureOfInterest> FOIs;

    private CityPulseUtilities cp;

    public CityPulseStreamEvent(String name, Date start, Date end){
        observations = new Vector<CityPulseObservation>();
        streamEventName = name;
        st_start = start;
        st_end = end;
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        hasFOI = false;
        hasFOIs = false;
        cp = new CityPulseUtilities();
        FOIs=new Vector<CityPulseFeatureOfInterest>();
    }

    public void setStartDate(Date date){
        st_start = date;
    }

    public void setEndDate(Date date){
        st_end = date;
    }

    public String getStartDate(){
        SimpleDateFormat returnFromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return returnFromat.format(st_start);
    }

    public String getEndDate(){
        SimpleDateFormat returnFromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return returnFromat.format(st_end);
    }

    public String getName(){
        return streamEventName;
    }

    public void addObservations(Vector<CityPulseObservation> obs){
        observations = obs;
    }

    public Vector<CityPulseObservation> getObservations(){
        return observations;
    }

    public String addContextToArray(String prefix, String lat, String longit, String arg_name){
        hasFOIs = true;
        String URI = prefix + CityPulseConstants.FOI_URI_PREFIX + cp.getNextRandomID();
        FOIs.add(new CityPulseFeatureOfInterest(lat, longit, arg_name, URI));
        return URI;
    }

    public void addContext(String prefix, String lat, String longit, String arg_name){
        latitude = lat;
        longitude = longit;
        name = arg_name;
        FOIURI = prefix + CityPulseConstants.FOI_URI_PREFIX + cp.getNextRandomID();
        hasFOI = true;
    }

    public String printStreamEvent(){

        StringBuilder sensorObservations = new StringBuilder();
        Iterator<CityPulseObservation> sensorObsIter = observations.iterator();

        String delimeter = ",";
        int count = 0;
        String use_prefix = "prov:used";
        while(sensorObsIter.hasNext()){

            CityPulseObservation currentObservation = sensorObsIter.next();
            if (count == observations.size() - 1){ delimeter = ".";}
            sensorObservations.append("\t").append(use_prefix).append(" <").append(currentObservation.getIdentifier()).append("> ").append(delimeter).append("\n");
            use_prefix="\t";
            count++;
        }

        StringBuilder statement = new StringBuilder();

        return  statement.append("\n\n<").append(streamEventName).append("> a sao:streamEvent ;\n")
                .append("\tsao:time [a tl:Interval ;\n")
                .append("\t\ttl:end \"").append(format.format(st_end)).append("\"^^xsd:dateTime;\n")
                .append("\t\ttl:start \"").append(format.format(st_start)).append("\"^^xsd:dateTime;\n")
                .append("\t] ;\n")
                .append(sensorObservations).toString();

    }

    public String printObservations(String headers){
        StringBuilder data = new StringBuilder();

        Iterator<CityPulseObservation> observationIterator = observations.iterator();
        int count = 0;
        while(observationIterator.hasNext()){

            CityPulseObservation currentObservation = observationIterator.next();
            String uom = "";
            String reference = "";
            if (currentObservation.hasUOM()){
                String[] hdrData = headers.split("\n");
                for (int i = 0; i < hdrData.length; i++){
                    if (hdrData[i].contains(currentObservation.getUOMNS())){
                        reference = hdrData[i].substring(8, hdrData[i].indexOf(":"));
                    }
                }
                uom = "\tsao:hasUnitOfMeasurement "+reference+":"+currentObservation.getReference()+" ;\n";
            }
            String foi = "";
            if (hasFOI){
                foi = "\tns1:featureOfInterest <"+FOIURI+"> ;\n";
            }
            if(currentObservation.hasFOI()){
                foi = "\tns1:featureOfInterest <"+currentObservation.getFOI()+"> ;\n";
            }
            data.append("")
                    .append("\n\n<").append(currentObservation.getIdentifier()).append("> a sao:Point ;\n")
                    .append(uom)
                    .append(foi)
                    .append("\tsao:time [a tl:Instant; \n")
                    .append("\t\ttl:at \"").append(format.format(currentObservation.getObsTime())).append("\"^^xsd:dateTime ];\n");
            if (currentObservation.getType() == CityPulseConstants.VALUE_TYPE_INTEGER){
                data.append("\tsao:value \"").append(currentObservation.getIntValue()).append("\" .\n");
            }
            else if (currentObservation.getType() == CityPulseConstants.VALUE_TYPE_DOUBLE){
                data.append("\tsao:value \"").append(currentObservation.getDoubleValue()).append("\" .\n");
            }

        }


        if (hasFOI)
            data.append(printFOI());
        if (hasFOIs)
            data.append(printFOIs());

        return data.toString();
    }

    private String printFOIs(){
        StringBuilder data = new StringBuilder();


        if (hasFOIs){
            Iterator<CityPulseFeatureOfInterest> iter = FOIs.iterator();
            while(iter.hasNext()){
                CityPulseFeatureOfInterest current = iter.next();
                data.append("")
                        .append("\n\n<"+current.getURI()+"> a sao:FeatureOfInterest ;\n")
                        .append("\tct:hasFirstNode [ a ct:Node ;\n");
                if (!current.getLatitude().isEmpty())
                    data.append("\t\tct:hasLatitude ").append(current.getLatitude()).append(" ;\n");
                if (!current.getLongitude().isEmpty())
                    data.append("\t\tct:hasLongitude ").append(current.getLongitude()).append(" ;\n");
                data.append("\t\tct:hasNodeName ").append("\"").append(current.getName()).append("\" ] .\n");
            }
        }

        return data.toString();
    }

    private String printFOI(){
        StringBuilder data = new StringBuilder();

        if(hasFOI){
            data.append("")
                    .append("\n\n<"+FOIURI+"> a sao:FeatureOfInterest ;\n")
                    .append("\tct:hasFirstNode [ a ct:Node ;\n")
                    .append("\t\tct:hasLatitude ").append(latitude).append(" ;\n")
                    .append("\t\tct:hasLongitude ").append(longitude).append(" ;\n")
                    .append("\t\tct:hasNodeName ").append("\"").append(name).append("\" ] .\n");
        }


        return data.toString();

    }
}

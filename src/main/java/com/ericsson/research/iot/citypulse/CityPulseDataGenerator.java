package com.ericsson.research.iot.citypulse;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class CityPulseDataGenerator {

    private String c_unitOfMeasurement = "<http://www.w3.org/2002/07/owl#nothing>";
    private String c_observedProperty = "<http://www.w3.org/2002/07/owl#nothing>";
    private int c_observationNumber;
    private int c_periodicity;
    private int c_periodicity_uom;
    private Date c_startTime;
    private CityPulseUtilities utilities;
    private String c_dstreamPrefix;
    private int c_valueType;
    private int c_generationType;
    private int c_upperBound;
    private int c_lowerBound;
    private int c_outputMode;
    private String c_file;

    private String c_FOI_latitude;
    private String c_FOI_longitude;
    private String c_FOI_description;

    private String streamURI;

    private double c_lambda;
    private double c_p;
    private double c_a;
    private double c_dconstant;
    private int c_iconstant;
    private double c_gaussian_mean;
    private double c_gaussian_stdev;

    public CityPulseDataGenerator(
            CityPulseDataGenerationModel cp){

        c_unitOfMeasurement = cp.d_unitOfMeasurement;
        c_observedProperty = cp.d_observedProperty;
        c_observationNumber = cp.d_observationNumber;
        c_periodicity = cp.d_periodicity;
        c_periodicity_uom = cp.d_periodicity_uom;
        c_startTime = cp.d_startTime;
        c_dstreamPrefix = cp.d_dstreamPrefix;
        c_valueType = cp.d_valueType;
        c_generationType = cp.d_generationType;
        c_upperBound = cp.d_upper_bound;
        c_lowerBound = cp.d_lower_bound;
        c_outputMode = cp.d_outputMode;
        c_file = cp.d_fileName;

        c_FOI_latitude = "";
        c_FOI_longitude = "";
        c_FOI_description = "";

        if (cp.hasFOI){
            c_FOI_latitude = cp.FOIlatitude;
            c_FOI_longitude = cp.FOIlongitude;
            c_FOI_description = cp.FOIdescription;
        }

        utilities = new CityPulseUtilities();

        streamURI = "";
    }

    public void processData(){

        Calendar cal = Calendar.getInstance();
        cal.setTime(c_startTime);

        String periodicityMeasurement = "";

        if (c_periodicity_uom == CityPulseConstants.PERIODICITY_MEASUREMENT_MIN){
            cal.add(Calendar.MINUTE, c_periodicity * c_observationNumber);
            periodicityMeasurement = "minute(s)";
        }
        else if(c_periodicity_uom == CityPulseConstants.PERIODICITY_MEASUREMENT_SEC){
            cal.add(Calendar.SECOND, c_periodicity * c_observationNumber);
            periodicityMeasurement = "second(s)";
        }
        else if(c_periodicity_uom == CityPulseConstants.PERIODICITY_MEASUREMENT_MSEC){
            cal.add(Calendar.MILLISECOND, c_periodicity * c_observationNumber);
            periodicityMeasurement = "milliseconds";
        }

        SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd' at 'HH:mm:ss");

        System.out.println("Generating data stream - Stream characteristics:\n");
        System.out.println("Name: "+c_dstreamPrefix);
        System.out.println("Number of observations: "+c_observationNumber);
        System.out.println("Sampling time: every "+c_periodicity+" "+periodicityMeasurement);
        System.out.println("Specified start date: "+df.format(c_startTime));
        System.out.println("Unit of measurement: "+c_unitOfMeasurement);
        if (c_valueType == CityPulseConstants.VALUE_TYPE_INTEGER){
            System.out.println("Value type: Integer");
            if (c_generationType == 1){
                System.out.println("Value Distribution: Poisson");
                System.out.println("Poisson Mean Lambda: "+c_lambda);
            }
            else if (c_generationType == 2){
                System.out.println("Value Distribution: Exponential");
                System.out.println("Exponential Rate: "+c_lambda);
            }
            else if (c_generationType == 3){
                System.out.println("Value Distribution: Geometric");
                System.out.println("Geometric Mean 1/p: "+c_p);
            }
            else if (c_generationType == 4){
                System.out.println("Value Distribution: Pareto");
                System.out.println("Pareto Alpha: "+c_a);
            }
            else if (c_generationType == 6){
                System.out.println("Value Distribution: Constant");
                System.out.println("Constant Value: "+c_iconstant);
            }
            else if (c_generationType == 5){
                System.out.println("Value Distribution: Uniform");
                System.out.println("Uniform Distribution Bounds (high, low): "+c_upperBound+", "+c_lowerBound);
            }
            else if (c_generationType == 7){
                System.out.println("Value Distribution: Gaussian");
                System.out.println("Gaussian mean and standard deviation: "+c_gaussian_mean+" and "+c_gaussian_stdev+" respectively.");
            }
        }
        else if (c_valueType == CityPulseConstants.VALUE_TYPE_DOUBLE){
            System.out.println("Value type: Float");
            if (c_generationType == 1){
                System.out.println("Value Distribution: Poisson");
                System.out.println("Exponential Rate: "+c_lambda);
            }
            else if (c_generationType == 2){
                System.out.println("Value Distribution: Exponential");
                System.out.println("Exponential Rate: "+c_lambda);
            }
            else if (c_generationType == 3){
                System.out.println("Value Distribution: Geometric");
                System.out.println("Geometric Mean 1/p: "+c_p);
            }
            else if (c_generationType == 4){
                System.out.println("Value Distribution: Pareto");
                System.out.println("Pareto Alpha: "+c_a);
            }
            else if (c_generationType == 6){
                System.out.println("Value Distribution: Constant");
                System.out.println("Constant Value: "+c_dconstant);
            }
            else if (c_generationType == 5){
                System.out.println("Value Distribution: Uniform");
                System.out.println("Uniform Distribution Bounds (high, low): "+c_upperBound+", "+c_lowerBound);
            }
            else if (c_generationType == 7){
                System.out.println("Value Distribution: Gaussian");
                System.out.println("Gaussian mean and standard deviation: "+c_gaussian_mean+" and "+c_gaussian_stdev+" respectively.");
            }
        }
        Date c_endTime = cal.getTime();
        System.out.println("Projected end time: "+df.format(c_endTime));

        if (!c_FOI_latitude.isEmpty() && !c_FOI_longitude.isEmpty() && !c_FOI_description.isEmpty()){
            System.out.println("Latitude: "+c_FOI_latitude+" and Longitude: "+c_FOI_longitude);
            System.out.println("Datastream description: "+c_FOI_description);
        }

        boolean saveToFile=false;
        if(c_outputMode == CityPulseConstants.OUTPUT_MODE_FILE){
            System.out.println("Output: file ("+c_file+")");
            saveToFile=true;
        }
        else if(c_outputMode == CityPulseConstants.OUTPUT_MODE_STDOUT){
            System.out.println("Output: standard output");
        }

        CityPulseStreamEvent dataStream = new CityPulseStreamEvent(c_dstreamPrefix, c_startTime, c_endTime);
        streamURI = dataStream.getName();
        Vector<CityPulseObservation> generatedData = generateData(c_valueType);
        String data = utilities.getHeaders(generatedData);

        dataStream.addObservations(generatedData);


        if (!c_FOI_latitude.isEmpty() && !c_FOI_longitude.isEmpty() && !c_FOI_description.isEmpty())
            dataStream.addContext(streamURI, c_FOI_latitude, c_FOI_longitude, c_FOI_description);

        data += dataStream.printStreamEvent();
        data += dataStream.printObservations(utilities.getHeaders(generatedData));

        if (saveToFile){
            try {
                PrintWriter writer = new PrintWriter(c_file, "UTF-8");
                writer.print(data);
                writer.close();
            } catch (Exception ex) {
                System.out.println("Error: Could not write to file");
                System.exit(-1);
            }
        }
        else
            System.out.println("\n\nGenerated Datastream:\n\n"+data);
    }

    public void setLambda(double lambda){
        c_lambda = lambda;
    }

    public void setP(double p){
        c_p = p;
    }

    public void setA(double a){
        c_a = a;
    }

    public void setConstant(double c){
        c_dconstant = c;
    }
    public void setConstant(int c){
        c_iconstant = c;
    }

    public void setGaussianMeanandStDev(double mean, double stdev){
        c_gaussian_mean = mean;
        c_gaussian_stdev = stdev;
    }

    private Vector<CityPulseObservation> generateData(int valueType){

        Vector<CityPulseObservation> observations = new Vector<CityPulseObservation>();


            if ((valueType == CityPulseConstants.VALUE_TYPE_INTEGER) ||
                    (valueType == CityPulseConstants.VALUE_TYPE_DOUBLE)){

                Random seedGen = new Random(System.currentTimeMillis());
                StdRandom.setSeed(seedGen.nextLong());

                Calendar cal = Calendar.getInstance();
                cal.setTime(c_startTime);
                for (int i = 0; i <= c_observationNumber; i++){

                    double fvalue = 0;
                    int ivalue = 0;

                    if (c_generationType == 1){ // poisson distribution with mean lambda
                        fvalue = StdRandom.poisson(c_lambda);
                        ivalue = (int)fvalue;
                    }
                    else if (c_generationType == 2){ // exponential distribution with rate lambda
                        fvalue = StdRandom.exp(c_lambda);
                        ivalue = (int)fvalue;
                    }
                    else if (c_generationType == 3){ // geometric with mean 1/p
                        fvalue = StdRandom.geometric(c_p);
                        ivalue = (int)fvalue;
                    }
                    else if (c_generationType == 4){ // pareto with parameter alpha
                        fvalue = StdRandom.pareto(c_a);
                        ivalue = (int)fvalue;
                    }
                    else if (c_generationType == 5){ // uniform with a, b bounds
                        fvalue = StdRandom.uniform(c_lowerBound, c_upperBound);
                        ivalue = (int)fvalue;
                    }
                    else if (c_generationType == 6){ // constant
                        if (valueType == CityPulseConstants.VALUE_TYPE_DOUBLE) fvalue = c_dconstant;
                        if (valueType == CityPulseConstants.VALUE_TYPE_INTEGER) ivalue = c_iconstant;
                    }
                    else if (c_generationType == 7){ // gaussian
                        fvalue = StdRandom.gaussian(c_gaussian_mean, c_gaussian_stdev);
                        ivalue = (int) fvalue;
                    }

                    CityPulseObservation obs;
                    if (!(valueType == CityPulseConstants.VALUE_TYPE_DOUBLE)){
                        obs =new CityPulseObservation(
                                streamURI + CityPulseConstants.OBSERVATION_URI_PREFIX + "point_"+utilities.getNextRandomID(),
                                ivalue,
                                cal.getTime());
                        if (!c_unitOfMeasurement.isEmpty()){
                            obs.addUnitOfMeasurement(c_unitOfMeasurement);
                        }

                        observations.add(obs);
                    }
                    else if ((valueType == CityPulseConstants.VALUE_TYPE_DOUBLE)){

                        obs =new CityPulseObservation(
                                streamURI + CityPulseConstants.OBSERVATION_URI_PREFIX + "point_"+utilities.getNextRandomID(),
                                fvalue,
                                cal.getTime());
                        if (!c_unitOfMeasurement.isEmpty()){
                            obs.addUnitOfMeasurement(c_unitOfMeasurement);
                        }

                        observations.add(obs);
                    }

                    if (c_periodicity_uom == CityPulseConstants.PERIODICITY_MEASUREMENT_MIN)
                        cal.add(Calendar.MINUTE, c_periodicity);
                    else if(c_periodicity_uom == CityPulseConstants.PERIODICITY_MEASUREMENT_SEC)
                        cal.add(Calendar.SECOND, c_periodicity);
                    else if(c_periodicity_uom == CityPulseConstants.PERIODICITY_MEASUREMENT_MSEC)
                        cal.add(Calendar.MILLISECOND, c_periodicity);
                }
            }


        return observations;
    }
}


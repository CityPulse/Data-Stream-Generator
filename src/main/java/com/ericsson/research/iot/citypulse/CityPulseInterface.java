package com.ericsson.research.iot.citypulse;

import jline.console.ConsoleReader;
import jline.console.completer.*;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eathkar on 27/10/14.
 * This class implements an interface for data generation and playback.
 */
public class CityPulseInterface {

    private static String []generateDSQuestions = {
            "Stream Description (e.g. \"This stream monitors temperature in Manhattan, New York\") >",
            "Number of measurements [positive integer] >",
            "Starting Date: To be provided in yy-MM-ddTHH:mm:ss format, e.g. 2014-12-12T11:11:11 >",
            "Periodicity: Amount of time between two subsequent measurements [positive integer] >",
            "Periodicity Unit of Measurement [1=minutes, 2=seconds and 3=milliseconds] >",
            "Observation Value Type: [1=integer, 2=double], double recommended >",
            "Datestream Name: (please use one word, e.g. \"temperatureStream, carTraffic\", etc.) >",
            "Distribution: [1=Poisson(mean lambda) 2=Exponential(rate lambda) 3=Geometric(mean 1/p) 4=Pareto(a) 5=Uniform(bounded) 6=Constant 7=Gaussian(mean, stdev)] >",
            "Output Mode: [1=console 2=file] >",
            "Unit of measurement [paste URI or type 'list' for possible values]>",
            "Geographical Coordinates [latitude, longitude], leave blank if not applicable >",
            "Datastream Prefix (URL where the stream will be placed, e.g. http://info.ee.surrey.ac.uk/CCSR/CityPulse) >"
    };
    private static String []conditionalQuestions = {
            "Upper Bound: Upper bound for value generation in uniform distribution >",
            "Lower Bound: Lower bound for value generation in uniform distribution >",
            "Filename >",
            "Please enter mean lambda for Poisson Distribution >",
            "Please enter lambda rate for Exponential Distribution >",
            "Please enter 1/p mean for Geometric Distribution >",
            "Please enter alpha parameter for Pareto Distribution >",
            "Please enter mean for Gaussian Distribution >",
            "Please enter standard deviation for Gaussian Distribution >",
            "Please enter constant value >"
    };

    public static void usage() {
        System.out.println("Usage: java -jar <jarname>");
    }



    public static void main(String[] args) {

        // Disable Log4j logging
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);

        try{
            if (args.length != 0){
                usage();
                return;
            }

            System.out.println("CityPulse Data Generation and Playback tool");
            System.out.println("Type help for quick help, or press tab for a list of options");
            ConsoleReader reader = new ConsoleReader();
            reader.setPrompt("cpa> ");
            boolean color = false;
            Character mask = null;
            String trigger = null;

            List<Completer> completors = new LinkedList<Completer>();
            completors.add(new StringsCompleter("clear", "exit", "help", "generate", "playback"));
            completors.add( new ArgumentCompleter(new StringsCompleter("playback"), new FileNameCompleter(), new NullCompleter()));
            for (Completer c : completors) {
                reader.addCompleter(c);
            }

            String line;
            PrintWriter out = new PrintWriter(reader.getOutput());

            while ((line = reader.readLine()) != null) {
                out.flush();

                if (line.trim().equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye");
                    break;
                }
                else if (line.trim().equalsIgnoreCase("clear")) {
                    reader.clearScreen();
                }
                else if (line.trim().equalsIgnoreCase("help")) {
                    System.out.println("\nCitypulse Data Annotation and Playback Tool\n");
                    System.out.println(" - Type generate to generate a new dataset and save it to file or print it.");
                    System.out.println(" - Type playback [filename] to playback an existing dataset.");
                    System.out.println(" - Type exit to quit, clear to clear screen.");
                    System.out.println("\nNote: The TAB key autocompletes the input\n\n");
                }
                else if (line.trim().contains("generate")){
                        boolean stoppingCondition = false;
                        String[] DSAnswers = new String[generateDSQuestions.length];
                        String[] DSConditionalAnswers = new String[conditionalQuestions.length];
                        // Initialize answers arrays so we dont run into exceptions later
                        for (int i = 0; i < DSAnswers.length; i++){
                           DSAnswers[i]="0";
                        }
                        for (int i = 0; i < DSConditionalAnswers.length; i++){
                           DSConditionalAnswers[i] = "0";
                        }
                        int distribution = 0;
                        double distribution_parameter_1 = 0;
                        double distribution_parameter_2 = 0;

                        for (int i = 0; i < generateDSQuestions.length; i++) {
                            reader.setPrompt(generateDSQuestions[i]);
                            DSAnswers[i] = reader.readLine();

                            if (i == 1 && !checkForValueGreaterThan(DSAnswers[i], 0)) { stoppingCondition = true; break; }
                            if (i == 3 && !checkForValueGreaterThan(DSAnswers[i], 0)) { stoppingCondition = true; break; }
                            if (i == 4 && !checkForValueBetween(DSAnswers[i], 1, 3)) { stoppingCondition = true; break; }
                            if (i == 5 && !checkForValueBetween(DSAnswers[i], 1, 3)) { stoppingCondition = true; break; }
                            if (i == 7 && !checkForValueBetween(DSAnswers[i], 1, 7)) { stoppingCondition = true; break; }
                            if (i == 7){

                                distribution = Integer.parseInt(DSAnswers[i]);
                                if (distribution == 1){
                                    reader.setPrompt(conditionalQuestions[3]);
                                    distribution_parameter_1 = Double.parseDouble(reader.readLine());
                                }
                                if (distribution == 2){
                                    reader.setPrompt(conditionalQuestions[4]);
                                    distribution_parameter_1 = Double.parseDouble(reader.readLine());
                                }
                                if (distribution == 3){
                                    reader.setPrompt(conditionalQuestions[5]);
                                    distribution_parameter_1 = Double.parseDouble(reader.readLine());
                                }
                                if (distribution == 4){
                                    reader.setPrompt(conditionalQuestions[6]);
                                    distribution_parameter_1 = Double.parseDouble(reader.readLine());
                                }
                                if (distribution == 5){
                                    reader.setPrompt(conditionalQuestions[0]);
                                    DSConditionalAnswers[0] = reader.readLine();
                                    reader.setPrompt(conditionalQuestions[1]);
                                    DSConditionalAnswers[1] = reader.readLine();
                                }
                                if (distribution == 6){
                                    reader.setPrompt(conditionalQuestions[9]);
                                    distribution_parameter_1 = Double.parseDouble(reader.readLine());
                                }
                                if (distribution == 7){
                                    reader.setPrompt(conditionalQuestions[7]);
                                    distribution_parameter_1 = Double.parseDouble(reader.readLine());
                                    reader.setPrompt(conditionalQuestions[8]);
                                    distribution_parameter_2 = Double.parseDouble(reader.readLine());
                                }

                            }
                            if (i == 8 && !checkForValueBetween(DSAnswers[i], 1, 2)) { stoppingCondition = true; break; }
                            if (i == 9 && DSAnswers[i].contains("list")){
                                System.out.println("List of supported units of measurement: ");
                                for (int counter = 0; counter < CityPulseConstants.UOM_VALUES.length; counter++){
                                    System.out.println("["+counter+"]\t"+CityPulseConstants.UOM_VALUES[counter]);
                                }
                                reader.setPrompt("Pick a number of measurement that better represents your data >");
                                String choice = reader.readLine();
                                if (!checkForValueBetween(choice, 0, CityPulseConstants.UOM_VALUES.length - 1)){
                                    stoppingCondition = true;
                                    break;
                                }
                                DSAnswers[i] = CityPulseConstants.UOM_VALUES[Integer.parseInt(choice)];
                            }


                            if (i == 8 && (DSAnswers[8].trim().compareTo("2") == 0)) {
                                reader.setPrompt(conditionalQuestions[2]);
                                DSConditionalAnswers[2] = reader.readLine();
                            }
                        }

                        if (DSAnswers[9].isEmpty()) {
                            System.out.println("Warning: Empty unit of measurement will result in invalid dataset.");
                        }

                        boolean incorrectDate = false;
                        Date setDate = new Date();

                        try {
                            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            setDate = f.parse(DSAnswers[2]);
                        } catch (Exception ex) {
                            incorrectDate = true;
                        }

                        if (incorrectDate) {
                            System.out.println("Error: Date " + DSAnswers[2] + " not specified in correct format!");
                        } else {

                            String filename = "";

                            if (Integer.parseInt(DSAnswers[8]) == 2) {
                                filename = DSConditionalAnswers[2];
                                File newFile = new File(filename);

                                if (newFile.exists()) {
                                    System.out.println("File already exists, please try another name.");
                                    stoppingCondition = true;
                                }
                                if (filename.trim().isEmpty()) {
                                    System.out.println("Empty filename provided, please specify a file name or choose to verbose to console.");
                                    stoppingCondition = true;
                                }
                            }

                            if (!stoppingCondition) {

                                CityPulseDataGenerationModel dgm =
                                        new CityPulseDataGenerationModel(
                                            DSAnswers[9],
                                            DSAnswers[0],
                                            Integer.parseInt(DSAnswers[1]),
                                            Integer.parseInt(DSAnswers[3]),
                                            Integer.parseInt(DSAnswers[4]),
                                            setDate,
                                            DSAnswers[11],
                                            Integer.parseInt(DSAnswers[5].trim()),
                                            Integer.parseInt(DSAnswers[7]),
                                            Integer.parseInt(DSConditionalAnswers[0]),
                                            Integer.parseInt(DSConditionalAnswers[1]),
                                            Integer.parseInt(DSAnswers[8]),
                                            filename);

                                String []geoCoords = DSAnswers[10].split(",");
                                if (geoCoords.length == 2 && !DSAnswers[10].isEmpty())
                                    dgm.addFOI(DSAnswers[0], geoCoords[0].trim(), geoCoords[1].trim());

                                CityPulseDataGenerator cpdg = new CityPulseDataGenerator(dgm);

                                if (distribution == 1 || distribution == 2){
                                    cpdg.setLambda(distribution_parameter_1);
                                }
                                if (distribution == 3){
                                    cpdg.setP(distribution_parameter_1);
                                }
                                if (distribution == 4){
                                    cpdg.setA(distribution_parameter_1);
                                }
                                if (distribution == 5){
                                    cpdg.setConstant(distribution_parameter_1);
                                    cpdg.setConstant((int)distribution_parameter_1);
                                }

                                if (distribution == 7){
                                    cpdg.setGaussianMeanandStDev(distribution_parameter_1, distribution_parameter_2);
                                }

                                cpdg.processData();
                            }
                        }

                    reader.setPrompt("cpa> ");
                }
                else if (line.trim().contains("playback")){
                    String[] playback = line.split(" ");

                    if (playback.length != 2){
                        System.out.println("Expected: valid filename (filepath) after playback");
                    }
                    else {
                        File f = new File(playback[1]);
                        if (!f.exists()){
                            System.out.println("File does not exist - make sure you have the correct path.");
                        }
                        reader.setPrompt("Playback Speed, a positive float, for example 0.5 is half speed, 1 is real-time, 4 is four times faster than real time >");
                        String playbackSpeed = reader.readLine();
                        if (playbackSpeed.isEmpty()){
                            playbackSpeed = "1";
                            System.out.println("Setting default value 1");
                        }
                        float pbspeed = 0;
                        try{
                            pbspeed = Float.parseFloat(playbackSpeed.trim());
                        }catch (Exception ex){
                            System.out.println("Could not recognize "+playbackSpeed+". A valid choice is a number greater than zero");
                        }
                        if (pbspeed <= 0){
                            System.out.println("Could not recognize "+playbackSpeed+". A valid choice is a number greater than zero");
                        }
                        else {

                            reader.setPrompt("Playback Mode [JSON/HTTP=1, UDP=2, Console=3] >");
                            String playbackMode = reader.readLine();

                            if (playbackMode.isEmpty()) {
                                playbackMode = "1";
                                System.out.println("Setting default value 1 [JSON/HTTP]");
                            }
                            int pbmode = 0;
                            try {
                                pbmode = Integer.parseInt(playbackMode.trim());
                            } catch (Exception ex) {
                                System.out.println("Could not recognize " + playbackMode + ". A valid choice is 1, 2 or 3.");
                            }

                            if (pbmode >= 1 && pbmode <= 3) {
                                reader.setPrompt("Target URI [Default='http://localhost'] >");
                                String host = reader.readLine();
                                if (host.isEmpty()) {
                                    host = "http://localhost";
                                    System.out.println("Setting default value 'localhost'");
                                }

                                if (pbmode == 1) playbackMode = "JSON/HTTP";
                                if (pbmode == 2) playbackMode = "UDP";
                                if (pbmode == 3) playbackMode = "Console Out (no remote host needed)";
                                System.out.println("\n\nPlayback Configuration:\nTarget URI: "+
                                        host+"\nPlayback Speed: "+
                                        pbspeed+"\nPlayback Mode: "+
                                        playbackMode+"\nPlayback File: "+
                                        f.getAbsolutePath());

                                CityPulseDataPlayback cpdp = new CityPulseDataPlayback(new CityPulseDataPlaybackModel(f.getAbsolutePath(), pbspeed, pbmode, host));
                                cpdp.playBackData();
                            }
                            else {
                                System.out.println("Could not recognize choice \"" + pbmode + "\". A valid choice is 1, 2 or 3.");
                            }
                        }
                        reader.setPrompt("cpa> ");
                    }
                }
                else {
                    System.out.println("Could not recognize input: "+line+" - press TAB for valid inputs");
                }
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }


    }


    private static boolean checkForValueGreaterThan(String value,int number){
        int val = 0;

        try {
            val = Integer.parseInt(value);
        }
        catch (Exception ex){
            System.out.println("Given value is not a valid integer");
            return false;
        }

        if (val > number)
            return true;
        else {
            System.out.println("Expected an integer value greater than "+number+". Given value: "+val);
            return false;
        }
    }

    private static boolean checkForValueBetween(String value,int low, int high){
        int val = 0;

        try {
            val = Integer.parseInt(value);
        }
        catch (Exception ex){
            System.out.println("Given value is not a valid integer");
            return false;
        }

        if (val >= low && val <= high)
            return true;
        else {
            System.out.println("Expected an integer value greater than "+low+" and less than "+high+". Given value: "+val);
            return false;
        }
    }
}

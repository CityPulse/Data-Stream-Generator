package com.ericsson.research.iot.citypulse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class CityPulseDataPlayback {

    private List<CityPulseDataSetObservation> dp;
    private CityPulseDataPlaybackModel dm;


    public CityPulseDataPlayback(CityPulseDataPlaybackModel cp){
        dm = cp;
        dp = new ArrayList<CityPulseDataSetObservation>();
    }

    public void playBackData() throws ParseException, InterruptedException, IOException{
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String value;
        Date vdate;

        Model model = ModelFactory.createDefaultModel() ;
        try {
            model.read(new File(dm.getFile()).toURI().toString());
        }
        catch (Exception ex){
            System.out.println("Exception, file seems there seems to be an issue with the information model instance in the file - playback will now stop.");
            ex.printStackTrace();
            return;
        }
        StmtIterator iter = model.listStatements();

        System.out.print("Validating and reading dataset ...");
        int counter = 0;
        while (iter.hasNext()) {

            Statement stmt      = iter.nextStatement();
            Property  predicate = stmt.getPredicate();
            RDFNode   object    = stmt.getObject();

            if (
                    (predicate.toString().compareTo(CityPulseConstants.predicateSAOTime) == 0) ||
                            (predicate.toString().compareTo(CityPulseConstants.predicateSAOValue) == 0)
                    ) {
                counter++;

                if(dp.isEmpty()){
                    dp.add(new CityPulseDataSetObservation());
                }
                else if (!dp.get(dp.size() - 1).isEmptyDate() && !dp.get(dp.size() - 1).isEmptyValue()){
                    dp.add(new CityPulseDataSetObservation());
                }

                if (object instanceof Resource) {

                    Resource res = (Resource) object;
                    StmtIterator iter2 = res.listProperties();
                    while(iter2.hasNext()){
                        Statement stmt2 = iter2.nextStatement();
                        if (stmt2.getPredicate().toString().compareTo(CityPulseConstants.predicateGetTimeInstant) == 0){
                            String date = stmt2.getObject().toString();
                            date = date.substring(0, date.indexOf("^"));
                            vdate = df.parse(date);
                            dp.get(dp.size() - 1).addDate(vdate);
                        }
                    }
                } else {
                    if ((predicate.toString().compareTo(CityPulseConstants.predicateSAOTime) == 0))
                        value = "";
                    value = object.toString();
                    dp.get(dp.size() - 1).addValue(value);
                }


            }

        }

        System.out.print("[Read "+dp.size()+" points, starting playback ...");
        float timeMultiplier = dm.getPlayBackSpeed();

        if (timeMultiplier < 0){
            System.out.println("Error: negative time multiplier: "+timeMultiplier);
            System.exit(-1);
        }

        Date oldestDate = dp.get(oldestPointIndex()).getDate();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
        System.out.println("from "+f.format(oldestDate) + "]");

        long refEpoch = 0;

        CityPulseDataSetObservation currentPoint = dp.get(oldestPointIndex());
        if (dm.getPlayBackMode() == CityPulseConstants.PLAYBACK_MODE_NETWORK){
            sendPOST(
                    dm.getSink(),
                    "{'timestamp': '"+f.format(currentPoint.getDate())+"',"
                            + "'value: '"+currentPoint.getValue()+"'}"
                    , currentPoint.getDate(), currentPoint.getValue(), f);
        }
        else if (dm.getPlayBackMode() == CityPulseConstants.PLAYBACK_MODE_STDOUT){
            System.out.println("["+f.format(currentPoint.getDate()) + "]" + " New Point, Value: "+currentPoint.getValue());
        }
        else if (dm.getPlayBackMode() == CityPulseConstants.PLAYBACK_MODE_UDP){
            sendUDP(dm.getSink(), "{'timestamp': '"+f.format(currentPoint.getDate())+"',"
                    + "'value: '"+currentPoint.getValue()+"'}", currentPoint.getDate(), currentPoint.getValue(), f);
        }
        refEpoch = currentPoint.getDate().getTime();
        dp.remove(oldestPointIndex());


        while (dp.size() > 0){

            currentPoint = dp.get(oldestPointIndex());

            Thread.sleep((currentPoint.getDate().getTime() - refEpoch)/(int)timeMultiplier);
            refEpoch = currentPoint.getDate().getTime();
            if (dm.getPlayBackMode() == CityPulseConstants.PLAYBACK_MODE_NETWORK){
                sendPOST(
                        dm.getSink(),
                        "{'timestamp': '"+f.format(currentPoint.getDate())+"',"
                                + "'value: '"+currentPoint.getValue()+"'}"
                        , currentPoint.getDate(), currentPoint.getValue(), f);
            }
            else if (dm.getPlayBackMode() == CityPulseConstants.PLAYBACK_MODE_STDOUT){
                System.out.println("["+f.format(currentPoint.getDate()) + "]" + " New Point, Value: "+currentPoint.getValue());
            }
            else if (dm.getPlayBackMode() == CityPulseConstants.PLAYBACK_MODE_UDP){
                sendUDP(dm.getSink(), "{'timestamp': '"+f.format(currentPoint.getDate())+"',"
                        + "'value: '"+currentPoint.getValue()+"'}", currentPoint.getDate(), currentPoint.getValue(), f);
            }
            dp.remove(oldestPointIndex());
        }


    }

    private void sendUDP(String IP, String data, Date date, String value, SimpleDateFormat f) throws IOException{
        InetAddress address = InetAddress.getByName(IP);
        byte[] buffer = data.getBytes();
        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, address, 57
        );
        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.send(packet);
        datagramSocket.close();
        System.out.println("["+f.format(date) + ", destination: "+IP+"]" + " New Point, Value: "+value);
    }

    private void sendPOST(String request, String data, Date date, String value, SimpleDateFormat f) throws IOException{
        if (!request.contains("http")){
            request = "http://" + request;
        }
        URL url = new URL(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", "" + Integer.toString(data.getBytes("UTF-8").length));
        byte[] datab = data.getBytes("UTF-8");
        OutputStream os = connection.getOutputStream();
        os.write( datab );
        os.close();
        connection.setUseCaches (false);
        int respCode = connection.getResponseCode();
        if (!(respCode == HttpURLConnection.HTTP_OK)){
            System.out.println("Simulation stopped, expected 200 OK back, got "+respCode);
            System.exit(-1);
        }
        System.out.println("["+f.format(date) + ", destination: "+request+"]" + " New Point, Value: "+value);
        connection.disconnect();
    }

    private int oldestPointIndex(){
        int index = 0;
        int oldestIndex = 0;
        Date oldDate = new Date();
        Iterator<CityPulseDataSetObservation> iterator = dp.iterator();

        while (iterator.hasNext()){
            CityPulseDataSetObservation currentPoint = iterator.next();
            if (index == 0){
                oldDate = currentPoint.getDate();
                oldestIndex = index;
            }
            else {

                Date currentDate = currentPoint.getDate();

                if (currentDate.compareTo(oldDate) < 0){
                    oldestIndex = index;
                    oldDate = currentDate;
                }
            }
            index++;
        }
        return oldestIndex;
    }
}

class CityPulseDataSetObservation{
    private String value;
    private Date date;

    public CityPulseDataSetObservation(){
        value = "";
        date = null;
    }

    public boolean isEmptyValue(){
        return value.isEmpty();
    }

    public boolean isEmptyDate(){
        if (date==null)
            return true;
        else
            return false;
    }

    public void addDate(Date d){
        date = d;
    }

    public void addValue(String v){
        value = v;
    }

    public Date getDate(){
        return date;
    }

    public String getValue(){
        return value;
    }
}

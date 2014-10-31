package com.ericsson.research.iot.citypulse;

/**
 * Created by eathkar on 27/10/14.
 */
public class CityPulseDataPlaybackModel{
    private String targetFile;
    private float playbackSpeed;
    private int playbackMode;
    private String target;

    public CityPulseDataPlaybackModel(
            String tf,
            float pb,
            int pm,
            String targ){
        targetFile = tf;
        playbackSpeed = pb;
        playbackMode = pm;
        target = targ;
    }

    public String getFile(){
        return targetFile;
    }

    public float getPlayBackSpeed(){
        return playbackSpeed;
    }

    public int getPlayBackMode(){
        return playbackMode;
    }

    public String getSink(){
        return target;
    }
}

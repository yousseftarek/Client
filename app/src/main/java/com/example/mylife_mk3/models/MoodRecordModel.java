package com.example.mylife_mk3.models;

import org.json.JSONObject;

/**
 * Created by Tarek on 08-Jun-2017.
 */

public class MoodRecordModel {

    public String recordID;
    public String time;
    public String date;
    public JSONObject mood;

    public MoodRecordModel(String recordID, String time, String date, JSONObject mood){
        this.recordID = recordID;
        this.time = time;
        this.date = date;
        this.mood = mood;
    }

}

package com.example.mylife_mk3.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class DayRecordModel {

    public String type;
    public String id;
    public String duration = "TEMP";
    public JSONObject startTime;
    public JSONObject endTime;
    public JSONObject location;
    public int durationSeconds;
    public String appName;
    public String mealType  = "sdaasda";
    public String taskTitle;
    public String taskDescription;

    public DayRecordModel(JSONObject record){
        try {
            type = record.getString("recordType");
            id = record.getString("recordID");
            startTime = record.getJSONObject("start_time_object");
            switch (record.getString("recordType")){
                case "LOCATION":
                    location = record.getJSONObject("location");
                    durationSeconds = 0;
                    break;
                case "SLEEP":
                    endTime = record.getJSONObject("end_time_object");
                    break;
                case "MEAL":
                    mealType = record.getString("type");
                    break;
                case "TASK":
                    taskTitle = record.getString("title");
                    duration = record.getString("duration_verbose");
                    break;
                default:    location = null;
                            endTime = null;
                            taskDescription = null;
                            taskTitle = null;
                            durationSeconds = record.getInt("duration");
                            duration = record.getString("duration_verbose");
                            appName = record.getString("source");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
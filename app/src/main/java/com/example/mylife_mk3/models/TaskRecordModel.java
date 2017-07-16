package com.example.mylife_mk3.models;

/**
 * Created by Tarek on 08-Jun-2017.
 */

public class TaskRecordModel {

    public String recordID;
    public String tiitle;
    public String description;
    public String time;
    public String date;
    public String duration;

    public TaskRecordModel(String recordID, String tiitle, String description, String time, String date, String duration){
        this.recordID = recordID;
        this.tiitle = tiitle;
        this.description = description;
        this.time = time;
        this.date = date;
        this.description = description;
    }
}

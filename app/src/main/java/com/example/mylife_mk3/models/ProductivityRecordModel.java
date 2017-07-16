package com.example.mylife_mk3.models;

/**
 * Created by Tarek on 16-Mar-2017.
 */

public class ProductivityRecordModel {
    public String appName;
    public String duration;
    public boolean productive;
    public String dayOfTheWeek;
    public String activity;
    public String startTime;
    public String id;

    public ProductivityRecordModel(String id, String name, String duration, boolean productive, String dayOfTheWeek, String activity, String startTime){
        this.appName = name;
        this.duration = duration;
        this.productive = productive;
        this.dayOfTheWeek = dayOfTheWeek;
        this.activity = activity;
        this.startTime = startTime;
        this.id = id;
    }
}

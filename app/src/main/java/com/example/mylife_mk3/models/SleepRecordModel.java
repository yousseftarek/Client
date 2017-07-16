package com.example.mylife_mk3.models;



public class SleepRecordModel {

    public String recordID;
    public String startTime;
    public String endTime;
    public String duration;
    public String startDate;
    public String endDate;

    public SleepRecordModel(String id, String startTime, String endTime, String startDate, String endDate, String duration){
        this.recordID = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

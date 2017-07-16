package com.example.mylife_mk3.models;



public class LocationRecordModel {

    public String id;
    public String latitude;
    public String longitude;
    public String provider;
    public String date;
    public String time;
    public String address;

    public LocationRecordModel(String id, String latitude, String longitude, String provider, String date, String time, String address){
        this.id         = id;
        this.latitude   = latitude;
        this.longitude  = longitude;
        this.provider   = provider;
        this.date       = date;
        this.time       = time;
        this.address = address;
    }
}

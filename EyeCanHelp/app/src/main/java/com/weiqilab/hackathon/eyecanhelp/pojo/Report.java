package com.weiqilab.hackathon.eyecanhelp.pojo;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Weiqi Zhao on 3/15/16.
 * Copyright (c) 2016 Weiqi Zhao. All rights reserved.
 */

public class Report implements Parcelable {
    private String reportId;
    private String currentPhotoUrl;
    private String time;
    private String location_Lat;
    private String location_Lon;
    private Kid kidData;
    private String kidId;

    public Report() {
    }

    protected Report(Parcel input) {
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    public String getCurrentPhotoUrl() {
        return currentPhotoUrl;
    }

    public void setCurrentPhotoUrl(String currentPhotoUrl) {
        this.currentPhotoUrl = currentPhotoUrl;
    }

    public Kid getKidData() {
        return kidData;
    }

    public void setKidData(Kid kidData) {
        this.kidData = kidData;
    }

    public String getKidId() {
        return kidId;
    }

    public void setKidId(String kidId) {
        this.kidId = kidId;
    }
    public String getLocation_Lon() {
        return location_Lon;
    }

    public void setLocation_Lat(String location_lat) {
        this.location_Lat = location_lat;
    }
    public String getLocation_Lat() {
        return location_Lat;
    }

    public void setLocation_Lon(String location_lon) {
        this.location_Lon = location_lon;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

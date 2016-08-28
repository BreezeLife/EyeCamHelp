package com.weiqilab.hackathon.eyecanhelp.pojo;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Weiqi Zhao on 8/28/16.
 * Copyright (c) 2016 Weiqi Zhao. All rights reserved.
 */
public class Kid implements Parcelable {

    private String kidName;
    private int age;
    private Date missingDate;
    private Location location;
    private String contactCallNubmer;
    private String contactEmail;
    private String contactFB;
    private String photoUrl;

    protected Kid(Parcel in) {
    }

    public static final Creator<Kid> CREATOR = new Creator<Kid>() {
        @Override
        public Kid createFromParcel(Parcel in) {
            return new Kid(in);
        }

        @Override
        public Kid[] newArray(int size) {
            return new Kid[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContactCallNubmer() {
        return contactCallNubmer;
    }

    public void setContactCallNubmer(String contactCallNubmer) {
        this.contactCallNubmer = contactCallNubmer;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactFB() {
        return contactFB;
    }

    public void setContactFB(String contactFB) {
        this.contactFB = contactFB;
    }

    public String getKidName() {
        return kidName;
    }

    public void setKidName(String kidName) {
        this.kidName = kidName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getMissingDate() {
        return missingDate;
    }

    public void setMissingDate(Date missingDate) {
        this.missingDate = missingDate;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

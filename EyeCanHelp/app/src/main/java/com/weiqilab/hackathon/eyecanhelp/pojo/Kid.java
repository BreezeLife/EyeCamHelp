package com.weiqilab.hackathon.eyecanhelp.pojo;

import android.accessibilityservice.GestureDescription;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Weiqi Zhao on 8/28/16.
 * Copyright (c) 2016 Weiqi Zhao. All rights reserved.
 */
public class Kid implements Parcelable {
    private String kidUUId;
    private String kidName;
    private String age;
    private String missingDate;
    private String location;
    private String contactCallNubmer;
    private String contactEmail;
    private String contactFB;
    private String photoUrl;

    protected Kid(Parcel in) {
    }

    public JSONObject toJSON()  {
        try {

            JSONObject result = new JSONObject();
            result.put("kidUUId", kidUUId);
            result.put("name", kidName);
            result.put("age", age);
            result.put("missingDate", missingDate.toString());
            result.put("location", location.toString());
            result.put("contactCallNubmer", contactCallNubmer);
            result.put("contactEmail", contactEmail);
            result.put("contactFB", contactFB);
            result.put("photoUrl", photoUrl);

            return result;

        } catch (JSONException e) {
            throw new IllegalStateException("JSON parsing is broken on this device?!");
        }
    }

    public Kid fromJSON(String kidJson) {
        JSONObject json = null;
        try {
            json = new JSONObject(kidJson);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Expect JSON string", e);
        }
        return fromJSON(json);
    }

    public Kid fromJSON(JSONObject json) {
        this.kidUUId = json.optString("kidUUId");
        this.kidName = json.optString("name");
        this.age= json.optString("age");
        this.missingDate=json.optString("missingDate");
        this.location = json.optString("location");
        this.contactCallNubmer = json.optString("contactCallNubmer");
        this.contactEmail = json.optString("contactEmail");
        this.contactFB = json.optString("contactFB");
        this.photoUrl = json.optString("photoUrl");


        return this;
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
    public String getKidUUId() {
        return kidUUId;
    }

    public void setKidUUId(String kidUUId) {
        this.kidUUId =kidUUId ;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMissingDate() {
        return missingDate;
    }

    public void setMissingDate(String missingDate) {
        this.missingDate = missingDate;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

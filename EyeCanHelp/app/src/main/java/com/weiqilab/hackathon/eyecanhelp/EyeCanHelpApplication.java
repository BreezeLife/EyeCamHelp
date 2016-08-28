package com.weiqilab.hackathon.eyecanhelp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Weiqi Zhao on 3/1/16.
 * Copyright (c) 2016 Weiqi Zhao. All rights reserved.
 */


public class EyeCanHelpApplication extends Application {

    public static Activity appactivity;


    private static Application sInstance;



    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }



    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }


    public static Application getInstance() {
        return sInstance;
    }

}

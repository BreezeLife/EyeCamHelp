package com.weiqilab.hackathon.eyecanhelp.task;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.weiqilab.hackathon.eyecanhelp.callback.PhotoCheckMatchListener;
import com.weiqilab.hackathon.eyecanhelp.network.VolleySingleton;

/**
 * Created by Weiqi Zhao on 8/28/16.
 * Copyright (c) 2016 Weiqi Zhao. All rights reserved.
 */
public class TaskCheckMatch extends AsyncTask<String, Void, String> {
    private PhotoCheckMatchListener myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String userId;
    private String userToken;
    private String resultId;

    public TaskCheckMatch(PhotoCheckMatchListener myComponent) {
        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    @Override
    protected String doInBackground(String... params) {
        userId = params[0];
        userToken = params[1];
        String user = "";
        //String user = Utils.loadMatchResult(requestQueue, userId, userToken);
        return user;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String resultId) {
        if (myComponent != null) {
            myComponent.onPhotoCheckMatch(resultId);
        }
    }
}

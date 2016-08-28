package com.weiqilab.hackathon.eyecanhelp.network;


import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.weiqilab.hackathon.eyecanhelp.EyeCanHelpApplication;


/**
 * Created by Weiqi Zhao on 3/1/16.
 * Copyright (c) 2016 Weiqi Zhao. All rights reserved.
 */

public class VolleySingleton {
    public static VolleySingleton sInstance = null;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;

    private VolleySingleton() {
        mRequestQueue = Volley.newRequestQueue(EyeCanHelpApplication.getAppContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private LruCache<String, Bitmap> cache = new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 1024) / 8);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

    }

    public static VolleySingleton getInstance() {
        if (sInstance == null) {
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}

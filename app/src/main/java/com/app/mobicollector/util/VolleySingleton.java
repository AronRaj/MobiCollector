package com.app.mobicollector.util;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Aron on 05-06-2016.
 */
public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;

    private VolleySingleton(){
        mRequestQueue = Volley.newRequestQueue(MobiCollectorApplication.getAppContext());
    }

    public static VolleySingleton getInstance(){
        if(mInstance == null){
            mInstance = new VolleySingleton();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        return this.mRequestQueue;
    }

}


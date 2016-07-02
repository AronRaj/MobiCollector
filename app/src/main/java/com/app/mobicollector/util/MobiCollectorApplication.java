package com.app.mobicollector.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Aron on 05-06-2016.
 */
public class MobiCollectorApplication extends Application {
    private static MobiCollectorApplication mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        this.setAppContext(getApplicationContext());
    }

    public static MobiCollectorApplication getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }
}

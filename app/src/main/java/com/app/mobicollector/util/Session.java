package com.app.mobicollector.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Aron on 24-12-2015.
 */
public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void clearAgentId(){
        prefs.edit().clear().commit();
    }

    public void setAgentId(String agentId) {
        prefs.edit().putString("agentid", agentId).commit();
    }

    public String getAgentId() {
        String agentId = prefs.getString("agentid","");
        return agentId;
    }
}

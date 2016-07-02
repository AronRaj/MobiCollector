package com.app.mobicollector.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.*;


import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.mobicollector.R;
import com.app.mobicollector.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;


public class VolleyActivity extends AppCompatActivity {
String url="http://paperyheftyserver-coeus.rhcloud.com/staffByType/Agent";
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);

// Start the queue
        queue.start();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(getApplicationContext(),"Response: " + response.toString(),Toast.LENGTH_LONG).show();
                        getJsonData(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Response: " + error.toString(),Toast.LENGTH_LONG).show();

                    }
                });
        queue.add(jsObjRequest);
    }

    private void getJsonData(final JSONObject jsonResponse) {
        if (jsonResponse != null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        JSONObject response = jsonResponse;
                        int statusCode = response.getInt(Constants.TAG_RESPONSE);
                        if (statusCode == Constants.TAG_RESPONSE_OK) {
                            Toast.makeText(getApplicationContext(), "Sync Collection Success", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sync Failed", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

    }



// Access the RequestQueue through your singleton class.
        //MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

}

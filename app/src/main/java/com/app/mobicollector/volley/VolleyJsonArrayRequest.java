package com.app.mobicollector.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aron on 02-07-2016.
 */
public class VolleyJsonArrayRequest extends JsonArrayRequest {

    public VolleyJsonArrayRequest(int method, String url, JSONArray jsonRequest,
                                   Response.Listener<JSONArray> listener,
                                   Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        // here you can write a custom retry policy
        return super.getRetryPolicy();
    }
}

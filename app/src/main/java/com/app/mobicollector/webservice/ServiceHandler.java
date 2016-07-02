package com.app.mobicollector.webservice;

/**
 * Created by Aron on 23-12-2015.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    public final static int PUT = 3;

    public ServiceHandler() {

    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, int method,
                                  String params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new StringEntity(params));
                    httpPost.setEntity(new StringEntity(params, "UTF8"));
                }
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                /*if (params != null) {
                    String paramString    = new StringEntity(params, "UTF8").toString();
                    url += "/" + paramString;
                }*/
                HttpGet httpGet = new HttpGet(url);
                /*httpGet.setHeader("Accept", "application/json");
                httpGet.setHeader("Content-type", "application/json");*/
                httpResponse = httpClient.execute(httpGet);

            }else if(method == PUT){

                HttpPut putRequest = new HttpPut(url);
                if (params != null) {
                    putRequest.setEntity(new StringEntity(params, "UTF8"));
                }
                putRequest.setHeader("Accept", "application/json");
                putRequest.setHeader("Content-type", "application/json");
                //input.setContentType(CONTENT_TYPE);
                httpResponse = httpClient.execute(putRequest);
            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }
}

package com.app.mobicollector.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.mobicollector.R;
import com.app.mobicollector.database.AndroidDatabaseManager;
import com.app.mobicollector.database.DBHelper;
import com.app.mobicollector.util.Constants;
import com.app.mobicollector.util.Session;
import com.app.mobicollector.volley.VolleyJsonArrayRequest;
import com.app.mobicollector.volley.VolleyJsonObjectRequest;
import com.app.mobicollector.volley.VolleyRequestQueue;
import com.app.mobicollector.webservice.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SyncFragment extends Fragment implements Response.Listener, Response.ErrorListener {

    //Volley Example
    // http://www.truiton.com/2015/02/android-volley-example/

    private String TAG = "SyncFragment";

    Button syncCollectionButton;
    Button syncCustomerDataButton;
    Button b;
    private ProgressDialog dialog;
    private DBHelper mydb;
    private Context mContext;
    View rootview;
    String agentId;
    private RequestQueue mQueue;
    VolleyJsonObjectRequest customerSyncRequest;
    VolleyJsonArrayRequest collectionUploadRequest;

    private String customerSyncUrl = "http://paperyheftyserver-coeus.rhcloud.com/customerByAgent/";

    public SyncFragment() {

    }

    public static final SyncFragment newInstance(String message) {
        SyncFragment f = new SyncFragment();
        /*Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);*/
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        Session session = new Session(getActivity());
        agentId = session.getAgentId();
        Log.d(TAG, "AgentName:: " + agentId);
        mydb = new DBHelper(mContext);
        mQueue = VolleyRequestQueue.getInstance(getActivity()).getRequestQueue();
        customerSyncRequest = new VolleyJsonObjectRequest(Request.Method.GET, Constants.CUSTOMER_BY_AGENT_URL + agentId,
                new JSONObject(), this, this);
        customerSyncRequest.setTag(TAG);
        dialog = new ProgressDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_sync, container, false);
        syncCollectionButton = (Button) rootview.findViewById(R.id.syncCollectionButton);
        syncCustomerDataButton = (Button) rootview.findViewById(R.id.syncCustomerDataButton);
        b = (Button) rootview.findViewById(R.id.databaseButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, AndroidDatabaseManager.class);
                startActivity(i);
            }
        });
        syncCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getInternetConnectivity()) {
                    dialog.setMessage("Uploading Collection Data");
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    JSONArray collectionData = mydb.getCollectionData();
                    if (collectionData.length() > 0) {
                        collectionUploadRequest = new VolleyJsonArrayRequest
                                (Request.Method.PUT, Constants.COLLECTION_SYNC_URL, null, new Response.Listener<JSONArray>() {

                                    @Override
                                    public void onResponse(JSONArray response) {
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                        Toast.makeText(getActivity(), "Response: " + response.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                        Log.e(TAG, "Volley Error::", error);
                                    }
                                });
                        mQueue.add(collectionUploadRequest);
                        /*CollectionUploadAsyncTask task = new CollectionUploadAsyncTask();
                        task.execute(collectionData);*/
                    } else {
                        Toast.makeText(getActivity(), "No Data To Upload", Toast.LENGTH_LONG).show();
                    }

                } else {
                    showSettingsAlert();
                }
            }
        });

        syncCustomerDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getInternetConnectivity()) {
                    if (null != customerSyncRequest) {
                        dialog.setMessage("Sync in progress");
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        mQueue.add(customerSyncRequest);
                    }
                    /*Session session = new Session(getActivity());
                    agentName = session.getAgentId();
                    Log.d(TAG, "AgentName:: " + agentName);
                    CustomerSyncAsyncTask task = new CustomerSyncAsyncTask();
                    task.execute("Arun");*/
                } else {
                    showSettingsAlert();
                }

            }
        });
        return rootview;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Log.e(TAG, "Volley Error::", error);
    }

    @Override
    public void onResponse(Object response) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        getVolleyResponse(response);
    }


    private class CollectionUploadAsyncTask extends AsyncTask<JSONArray, Void, Void> {

        CollectionUploadAsyncTask() {
            dialog = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Uploading Collection Data");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(JSONArray... collectionData) {
            try {


                if (collectionData.length > 0) {
                    final JSONArray data = collectionData[0];

                    ServiceHandler service = new ServiceHandler();
                    String collectionResponse = service.makeServiceCall(Constants.COLLECTION_SYNC_URL, ServiceHandler.PUT, data.toString());
                    getJsonData(collectionResponse);
                }

            } catch (Exception e) {

                Log.e("ERROR IN SEVER UPLOAD", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void getJsonData(final String jsonResponse) {
        if (jsonResponse != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        JSONObject response = new JSONObject(jsonResponse);
                        int statusCode = response.getInt(Constants.TAG_RESPONSE);
                        if (statusCode == Constants.TAG_RESPONSE_OK) {
                            Toast.makeText(mContext, "Sync Collection Success", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "Sync Failed", Toast.LENGTH_LONG).show();
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

/*    private class CustomerSyncAsyncTask extends AsyncTask<String, Void, Void> {

        CustomerSyncAsyncTask() {
            dialog = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Sync in progress");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... data) {
            try {
                ServiceHandler service = new ServiceHandler();
                String customerSyncResponse = service.makeServiceCall(customerSyncUrl + agentName, ServiceHandler.GET, data.toString());
                parseJsonData(customerSyncResponse);


            } catch (Exception e) {

                Log.e("ERROR IN SEVER UPLOAD", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void parseJsonData(String jsonResponse) {
        if (jsonResponse != null) {

            try {
                JSONObject response = new JSONObject(jsonResponse);
                int statusCode = response.getInt(Constants.TAG_RESPONSE);
                if (statusCode == Constants.TAG_RESPONSE_OK) {
                    JSONArray customerArray = response.getJSONArray(Constants.CUSTOMER_DATA);
                    for (int i = 0; i < customerArray.length(); i++) {
                        JSONObject customer = customerArray.getJSONObject(i);
                        int customerId;
                        String customerFirstName = null;
                        String customerAddress = null;
                        if (customer.has(Constants.CUSTOMER_ID_KEY)) {
                            customerId = customer.getInt(Constants.CUSTOMER_ID_KEY);
                            if (customer.has(Constants.CUSTOMER_NAME_KEY)) {
                                customerFirstName = customer.getString(Constants.CUSTOMER_NAME_KEY);
                            }
                            if (customer.has(Constants.CUSTOMER_ADDRESS_KEY)) {
                                customerAddress = customer.getString(Constants.CUSTOMER_ADDRESS_KEY) + " " +
                                        customer.getString(Constants.CUSTOMER_PINCODE_KEY);
                            }

                            mydb.insertCustomer(customerId, customerFirstName, customerAddress);
                        } else {
                            Log.e(TAG, "Customer id not present");
                        }
                    }
                } else {
                    Toast.makeText(mContext, "Sync Failed", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

    }*/

    private void getVolleyResponse(Object jsonResponse) {
        if (jsonResponse != null) {

            try {
                JSONObject response = (JSONObject) jsonResponse;
                int statusCode = response.getInt(Constants.TAG_RESPONSE);
                if (statusCode == Constants.TAG_RESPONSE_OK) {
                    JSONArray customerArray = response.getJSONArray(Constants.CUSTOMER_DATA);
                    for (int i = 0; i < customerArray.length(); i++) {
                        JSONObject customer = customerArray.getJSONObject(i);
                        int customerId;
                        String customerFirstName = null;
                        String customerAddress = null;
                        if (customer.has(Constants.CUSTOMER_ID_KEY)) {
                            customerId = customer.getInt(Constants.CUSTOMER_ID_KEY);
                            if (customer.has(Constants.CUSTOMER_NAME_KEY)) {
                                customerFirstName = customer.getString(Constants.CUSTOMER_NAME_KEY);
                            }
                            if (customer.has(Constants.CUSTOMER_ADDRESS_KEY)) {
                                if (customer.has(Constants.CUSTOMER_PINCODE_KEY)) {
                                    customerAddress = customer.getString(Constants.CUSTOMER_ADDRESS_KEY) + " " +
                                            customer.getString(Constants.CUSTOMER_PINCODE_KEY);
                                } else {
                                    customerAddress = customer.getString(Constants.CUSTOMER_ADDRESS_KEY);
                                }

                            }

                            mydb.insertCustomer(customerId, customerFirstName, customerAddress);
                        } else {
                            Log.e(TAG, "Customer id not present");
                        }
                    }
                } else {
                    Toast.makeText(mContext, "Sync Failed", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle("Internet Settings");

        alertDialog
                .setMessage("Internet is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private boolean getInternetConnectivity() {
        boolean isInternetOn = false;
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        WifiManager wifi = (WifiManager) getActivity().getSystemService(getActivity().WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            Log.d(TAG, "Wifi is On");
            WifiInfo data = wifi.getConnectionInfo();
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                isInternetOn = true;
                Log.d(TAG, "Connected to wifi " + data.getSSID());
            } else if (mMobile.isConnected()) {
                isInternetOn = true;
                Log.d(TAG, "Connected to data:: " + mMobile.getExtraInfo());
            } else {
                Log.d(TAG, "Not Connected to wifi ");
            }
        } else {
            if (mMobile.isConnected()) {
                isInternetOn = true;
                Log.d(TAG, "Connected to data:: " + mMobile.getExtraInfo());
            }
        }
        return isInternetOn;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(TAG);
        }
    }

}
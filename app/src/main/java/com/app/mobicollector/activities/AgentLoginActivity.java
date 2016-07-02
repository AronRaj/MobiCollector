package com.app.mobicollector.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.mobicollector.R;
import com.app.mobicollector.database.DBHelper;
import com.app.mobicollector.util.Constants;
import com.app.mobicollector.util.Session;
import com.app.mobicollector.volley.VolleyJsonObjectRequest;
import com.app.mobicollector.volley.VolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AgentLoginActivity extends Activity implements Response.Listener, Response.ErrorListener{

    private final String TAG="AgentLoginActivity";
    AutoCompleteTextView agentId;
    EditText agentPassword;
    Button agentLoginButton;
    private DBHelper mydb;
    ArrayList<String> agentsList;
    private RequestQueue mQueue;
    VolleyJsonObjectRequest agentSyncRequest;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_login);
        mydb=new DBHelper(getApplicationContext());
        /*mydb.insertPaymentHistory(29051,"29/05/2016",1000,"PAID");
        mydb.insertPaymentHistory(29051,"27/04/2016",1000,"UNPAID");
        mydb.insertPaymentHistory(29051,"20/06/2016",1000,"UNPAID");
        mydb.insertPaymentHistory(29051,"05/03/2016",1000,"PAID");
        mydb.insertPaymentHistory(29051,"20/02/2016",1000,"UNPAID");*/
        /*mydb.insertAgent("Arun","12345");
        mydb.insertAgent("Chinna","12345");
        mydb.insertAgent("Raj","12345");*/
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getAgentDetails();
            }
        });

        dialog = new ProgressDialog(getApplicationContext());
        agentId=(AutoCompleteTextView)findViewById(R.id.agentId);
        agentPassword=(EditText)findViewById(R.id.agentPassword);
        agentLoginButton=(Button)findViewById(R.id.agentLoginButton);
        agentLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String agentid=mydb.checkForAgent(agentId.getText().toString(),agentPassword.getText().toString());
                if(agentid!=null){
                    Session agentSuccess=new Session(getApplicationContext());
                    agentSuccess.setAgentId(agentid);
                    Intent userIntent=new Intent(getApplicationContext(),TestActivity.class);
                    startActivity(userIntent);
                    overridePendingTransition(R.animator.push_up_in, R.animator.push_up_out);
                    finishAffinity();
                }else{
                    Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_LONG).show();
                    agentId.setText("");
                    agentPassword.setText("");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session registeredUser=new Session(AgentLoginActivity.this);
        if(!registeredUser.getAgentId().equals("")) {
            Intent userIntent = new Intent(AgentLoginActivity.this, TestActivity.class);
            startActivity(userIntent);
            finish();
        }
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

    private void getVolleyResponse(Object jsonResponse) {
        if (jsonResponse != null) {

            try {
                JSONObject response = (JSONObject) jsonResponse;
                int statusCode = response.getInt(Constants.TAG_RESPONSE);
                if (statusCode == Constants.TAG_RESPONSE_OK) {
                    JSONArray customerArray = response.getJSONArray(Constants.CUSTOMER_DATA);
                    for (int i = 0; i < customerArray.length(); i++) {
                        JSONObject agent = customerArray.getJSONObject(i);
                        int agentId;
                        String agentFirstName = null;
                        String agentPassword = null;
                        if (agent.has(Constants.AGENT_ID_KEY)) {
                            agentId = agent.getInt(Constants.AGENT_ID_KEY);
                            if (agent.has(Constants.AGENT_NAME_KEY)) {
                                agentFirstName = agent.getString(Constants.AGENT_NAME_KEY);
                            }
                            if (agent.has(Constants.AGENT_PASSWORD_KEY)) {
                                agentPassword = agent.getString(Constants.AGENT_PASSWORD_KEY);
                            }

                            mydb.insertAgent(agentId,agentFirstName,"12345");
                        } else {
                            Log.e(TAG, "Customer id not present");
                        }
                    }
                    agentsList=mydb.getAgentsList();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,agentsList);
                    agentId.setAdapter(adapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Sync Failed", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

    }

    private void getAgentDetails(){
        mQueue = VolleyRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        agentSyncRequest = new VolleyJsonObjectRequest(Request.Method.GET, Constants.AGENT_DETAILS_URL,new JSONObject(), this, this);
        agentSyncRequest.setTag(TAG);
        mQueue.add(agentSyncRequest);
    }
}

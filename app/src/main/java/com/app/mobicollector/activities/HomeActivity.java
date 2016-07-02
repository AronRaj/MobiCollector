package com.app.mobicollector.activities;

/**
 * Created by ARON on 28/2/16.
 */

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.mobicollector.R;
import com.app.mobicollector.database.AndroidDatabaseManager;
import com.app.mobicollector.database.DBHelper;
import com.app.mobicollector.fragments.AgentSelectFragment;
import com.app.mobicollector.fragments.CollectionFragment;
import com.app.mobicollector.util.Constants;
import com.app.mobicollector.webservice.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends Activity {

    private String TAG="HomeActivity";


    Button agentSelectButton;
    Button collectionButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        agentSelectButton = (Button) findViewById(R.id.selectAgentButton);
        collectionButton = (Button) findViewById(R.id.collectionButton);

        agentSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out);
                AgentSelectFragment agentfragment = new AgentSelectFragment();
                fragmentTransaction.add(R.id.mainFrame, agentfragment, "First");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });

        collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out);
                CollectionFragment collectionFragment = new CollectionFragment();
                fragmentTransaction.add(R.id.mainFrame, collectionFragment, "collection");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/


            }
        });


    }

}

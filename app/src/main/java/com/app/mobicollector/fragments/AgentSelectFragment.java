package com.app.mobicollector.fragments;

/**
 * Created by ARON on 28/2/16.
 */

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mobicollector.R;
import com.app.mobicollector.database.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AgentSelectFragment extends Fragment {

    private static String TAG="AgentSelectFragment";

    AutoCompleteTextView agentName;
    EditText agentId;
    View rootView;
    private DBHelper mydb;
    ArrayList<HashMap<Integer,String>> agentsList;
    ArrayList<HashMap<Integer,String>> autoCompleteList;


    public AgentSelectFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydb = new DBHelper(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_agent_select, container, false);
        agentsList=mydb.getAllAgents();
        agentName = (AutoCompleteTextView) rootView.findViewById(R.id.agentNameAutoComplete);
        agentId = (EditText) rootView.findViewById(R.id.agentId);
        agentName = (AutoCompleteTextView) rootView.findViewById(R.id.agentNameAutoComplete);
        agentName.setAdapter(new AutoCompleteAdapter(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1, agentsList));
        agentName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser=parent.getItemAtPosition(position).toString();
                Log.d(TAG, "User selected::" + selectedUser);
                String[] userSplit=selectedUser.split("-");
                String userId=userSplit[1];
                Log.d(TAG, "User ID::" + userId);
            }
        });
        return rootView;
    }

    private class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {


        public AutoCompleteAdapter(Context context, int textViewResourceId,ArrayList<HashMap<Integer,String>> data) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return autoCompleteList.size();
        }

        @Override
        public String getItem(int position) {
            return autoCompleteList.get(position).get("agent_name")+"-"+autoCompleteList.get(position).get("agent_id");
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected Filter.FilterResults performFiltering(final CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {

                        autoCompleteList = new ArrayList<HashMap<Integer, String>>();
                        for (int i = 0; i < agentsList.size(); i++) {
                            if (agentsList.get(i).get("agent_name").toLowerCase().startsWith(constraint.toString().toLowerCase())
                                    || agentsList.get(i).get("agent_id").startsWith(constraint.toString()))
                            {
                                autoCompleteList.add(agentsList.get(i));
                            }
                        }

                        // Now assign the values and count to the FilterResults
                        // object
                        filterResults.values = autoCompleteList;

                        filterResults.count = autoCompleteList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

}

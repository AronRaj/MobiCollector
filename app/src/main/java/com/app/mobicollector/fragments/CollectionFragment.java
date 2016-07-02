package com.app.mobicollector.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.app.mobicollector.R;
import com.app.mobicollector.database.DBHelper;
import com.app.mobicollector.util.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class CollectionFragment extends Fragment {

    private static String TAG = "CollectionFragment";

    AutoCompleteTextView aCustomerList;
    EditText customer;
    EditText customerAddress;
    EditText collectionAmount;
    Button submitButton;
    Button clearButton;
    Button historyButton;
    int customerId;
    String customerName;
    View rootView;
    private DBHelper mydb;
    private String agentName;
    ArrayList<HashMap<String, String>> customersList;
    ArrayList<HashMap<String, String>> autoCompleteList;

    public CollectionFragment() {
        // Required empty public constructor
    }

    public static final CollectionFragment newInstance(String message) {
        CollectionFragment f = new CollectionFragment();
        /*Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);*/
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mydb = new DBHelper(getActivity().getApplicationContext());
        Session session = new Session(getActivity());
        agentName = session.getAgentId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_collection, container, false);
        customerAddress = (EditText) rootView.findViewById(R.id.customer_address);
        collectionAmount = (EditText) rootView.findViewById(R.id.collectionAmount);
        historyButton = (Button) rootView.findViewById(R.id.history_button);
        historyButton.setVisibility(View.GONE);
        customersList = mydb.getAllCustomers();
        aCustomerList = (AutoCompleteTextView) rootView.findViewById(R.id.customerNameAutoComplete);
        customer = (EditText) rootView.findViewById(R.id.customerId);
        aCustomerList = (AutoCompleteTextView) rootView.findViewById(R.id.customerNameAutoComplete);
        aCustomerList.setDropDownBackgroundResource(R.color.colorPrimary);
        aCustomerList.setAdapter(new AutoCompleteAdapter(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, customersList));
        aCustomerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "User selected::" + selectedUser);
                String[] userSplit = selectedUser.split("-");
                customerName = userSplit[0];
                customerId = Integer.parseInt(userSplit[1]);
                String lCustomerAddress = mydb.getCustomerAddress(customerId);
                aCustomerList.setText(customerName);
                customer.setText(String.valueOf(customerId));
                customerAddress.setText(lCustomerAddress);
                historyButton.setVisibility(View.VISIBLE);
                try {
                    InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    input.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }catch(Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Customer ID::" + customerId + " Name:: " + customerName);
            }
        });
        submitButton = (Button) rootView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*int customerId=Integer.parseInt(customer.getText().toString());
                String customerName=aCustomerList.getText().toString();*/
                int collectedAmount = 0;
                Date date = new Date();
                String collectedDate = date.toString();
                //String collectedDate=collectiondate.getText().toString();
                if (!collectionAmount.getText().toString().equals("")) {
                    collectedAmount = Integer.parseInt(collectionAmount.getText().toString());
                }
                if (customerId != 0 && customerName != null && collectedAmount != 0) {
                    boolean insertStatus = mydb.insertCollectionData(customerId, customerName, collectedDate, collectedAmount, agentName);
                    if (insertStatus) {
                        Toast.makeText(getActivity().getApplicationContext(), "Collection Success", Toast.LENGTH_LONG).show();
                        historyButton.setVisibility(View.GONE);
                        aCustomerList.setText("");
                        customer.setText("");
                        customerAddress.setText("");
                        collectionAmount.setText("");
                    } else
                        Toast.makeText(getActivity().getApplicationContext(), "Collection Failed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter all details", Toast.LENGTH_LONG).show();
                }
            }
        });
        clearButton = (Button) rootView.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyButton.setVisibility(View.GONE);
                aCustomerList.setText("");
                customer.setText("");
                customerAddress.setText("");
                collectionAmount.setText("");
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //http://stackoverflow.com/questions/19466757/hashmap-to-listview
                PaymentHistoryDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {


        public AutoCompleteAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, String>> data) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return autoCompleteList.size();
        }

        @Override
        public String getItem(int position) {
            return autoCompleteList.get(position).get("customer_name") + "-" + autoCompleteList.get(position).get("customer_id");
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected Filter.FilterResults performFiltering(final CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {

                        autoCompleteList = new ArrayList<HashMap<String, String>>();
                        for (int i = 0; i < customersList.size(); i++) {
                            Log.d(TAG, "customersList :" + customersList.get(i));
                            Log.d(TAG, "customer :" + customersList.get(i).get("customer_name"));
                            if (customersList.get(i).get("customer_name").toLowerCase().startsWith(constraint.toString().toLowerCase())
                                    || customersList.get(i).get("customer_id").startsWith(constraint.toString())) {
                                autoCompleteList.add(customersList.get(i));
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

    public void PaymentHistoryDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ArrayList<HashMap<String, String>> paymentData=mydb.checkCustomerPaymentHistory(Integer.parseInt(customer.getText().toString()));
        String[] locales = Locale.getISOCountries();
        //countries = new ArrayList<HashMap<String, String>>();

        /*for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);

            HashMap<String, String> country = new HashMap<String, String>();

            String country_name = obj.getDisplayCountry();
            String country_code = obj.getCountry();

            country.put("name", country_name);
            country.put("code", country_code);

            countries.add(country);
        }*/
        /*final Dialog myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.payment_layout);
        myDialog.setTitle("Payment History");
        Window window = myDialog.getWindow();
        window.setLayout(850,1000);
        window.setGravity(Gravity.CENTER);
        myDialog.show();*/
            //ad.setIcon(R.drawable.icon);
            ad.setTitle("Payment History");
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.payment_layout, null);
            String[] from = {"payment_date", "payment_amount"};

            // view id's to which data to be binded
            int[] to = {R.id.name, R.id.code};
            ListView listView = (ListView) v.findViewById(R.id.listview);
            ListAdapter adapter = new SimpleAdapter(getActivity(), paymentData, R.layout.list_items, from, to);
            listView.setAdapter(adapter);

            ad.setView(v);

            ad.setPositiveButton("OK",
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            // OK, go back to Main menu
                        }
                    }
            );

            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                       public void onCancel(DialogInterface dialog) {
                                           // OK, go back to Main menu
                                       }
                                   }
            );

            ad.show();


    }
}

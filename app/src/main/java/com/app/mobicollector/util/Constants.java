package com.app.mobicollector.util;

/**
 * Created by Aron on 22-05-2016.
 */
public class Constants {
    public static String CUSTOMER_ID_KEY = "customerID";
    public static String CUSTOMER_NAME_KEY = "first_name";
    public static final String CUSTOMER_ADDRESS_KEY = "address";
    public static final String CUSTOMER_PINCODE_KEY = "pincode";
    public static String COLLECTED_AMOUNT_KEY = "payment";
    public static String COLLECTED_DATE_KEY = "collected_time";
    public static final String CUSTOMER_DATA = "data";
    public static String AGENT_NAME_KEY = "first_name";
    public static final String AGENT_ID_KEY = "staffID";
    public static final String AGENT_PASSWORD_KEY = "password";

    public static final int TAG_RESPONSE_OK = 200;
    public static final String TAG_RESPONSE = "code";

    public static final String CUSTOMER_BY_AGENT_URL = "http://paperyheftyserver-coeus.rhcloud.com/customerByAgent/";
    public static final String AGENT_DETAILS_URL ="http://paperyheftyserver-coeus.rhcloud.com/staffByType/Agent";
    public static final String COLLECTION_SYNC_URL = "http://paperyheftyserver-coeus.rhcloud.com/payment";
}

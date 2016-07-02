package com.app.mobicollector.database;

/**
 * Created by ARON on 28/2/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;
import android.util.Log;

import com.app.mobicollector.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    public static final String DATABASE_NAME = "mobicollector.db";
    public static final int DATABASE_VERSION = 2;
    public static final String CUSTOMER_TABLE_NAME = "customer";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String CUSTOMER_FIRST_NAME = "first_name";
    public static final String CUSTOMER_LAST_NAME = "last_name";
    public static final String CUSTOMER_MOBILE = "mobile";
    public static final String CUSTOMER_AREA_CODE = "area_code";
    public static final String CUSTOMER_ADDRESS = "address";
    public static final String CUSTOMER_PINCODE = "pincode";
    public static final String CUSTOMER_STATUS = "status";
    public static final String CUSTOMER_EMAIL_ID = "email_id";

    public static final String AGENT_TABLE_NAME = "agent";
    public static final String AGENT_ID = "agent_id";
    public static final String AGENT_NAME = "agent_name";
    public static final String AGENT_PASSWORD = "agent_password";

    public static final String AREA_TABLE_NAME = "area";
    public static final String AREA_CODE = "area_code";
    public static final String AREA_NAME = "area_name";

    public static final String COLLECTION_TABLE_NAME = "collection";
    public static final String COLLECTION_DATE = "collection_date";
    public static final String COLLECTION_AMOUNT = "collection_amount";

    public static final String PAYMENT_TABLE_NAME = "payment";
    public static final String PAYMENT_DATE = "payment_date";
    public static final String PAYMENT_AMOUNT = "payment_amount";
    public static final String PAYMENT_STATUS = "payment_status";

    // Database creation sql statements
    /*private static final String CUSTOMER_DATABASE_CREATE = "create table "
            +CUSTOMER_TABLE_NAME+"(" + CUSTOMER_ID
            +" integer primary key autoincrement, " + CUSTOMER_FIRST_NAME
            +" text not null,"+CUSTOMER_LAST_NAME+""+" text not null,"+CUSTOMER_MOBILE+" text not null,"+CUSTOMER_EMAIL_ID
            +" text,"+CUSTOMER_AREA_CODE+ " text not null,"+CUSTOMER_ADDRESS+" text not null,"+CUSTOMER_PINCODE+ " text not null,"+
            CUSTOMER_STATUS+ " text not null)";*/

    private static final String CUSTOMER_DATABASE_CREATE = "create table "
            + CUSTOMER_TABLE_NAME + "(" + CUSTOMER_ID
            + " integer primary key, " + CUSTOMER_FIRST_NAME
            + " text not null," + CUSTOMER_ADDRESS + "" + " text not null)";

    private static final String COLLECTION_DATABASE_CREATE = "create table "
            + COLLECTION_TABLE_NAME + "(" + CUSTOMER_ID
            + " integer , " + CUSTOMER_FIRST_NAME + " text not null," + COLLECTION_DATE + " text not null," + COLLECTION_AMOUNT + "" + " int not null," + AGENT_NAME + " text not null)";

    private static final String AGENT_DATABASE_CREATE = "create table "
            + AGENT_TABLE_NAME + "(" + AGENT_ID + " integer primary key, " + AGENT_NAME + " text not null, " + AGENT_PASSWORD + " text not null)";

    private static final String PAYMENT_DATABASE_CREATE = "create table "
            + PAYMENT_TABLE_NAME + "(" + CUSTOMER_ID + " integer, " + PAYMENT_DATE + " text not null, " + PAYMENT_AMOUNT + " integer not null," + PAYMENT_STATUS + " text not null)";

    private static final String AREA_DATABASE_CREATE = "create table "
            + AREA_TABLE_NAME + "(" + AREA_CODE + " integer primary key autoincrement, " + AREA_NAME + " text not null)";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CUSTOMER_DATABASE_CREATE);
        db.execSQL(COLLECTION_DATABASE_CREATE);
        db.execSQL(AGENT_DATABASE_CREATE);
        db.execSQL(PAYMENT_DATABASE_CREATE);
        //db.execSQL(AREA_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS customer");
        db.execSQL("DROP TABLE IF EXISTS collection");
        db.execSQL("DROP TABLE IF EXISTS agent");
        db.execSQL("DROP TABLE IF EXISTS payment");
        //db.execSQL("DROP TABLE IF EXISTS area");
        onCreate(db);
    }

    /*public boolean insertCustomer(String firstName, String lastName,long mobile,String email,int area,String address,int pincode,String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_FIRST_NAME, firstName);
        contentValues.put(CUSTOMER_LAST_NAME, lastName);
        contentValues.put(CUSTOMER_MOBILE, mobile);
        contentValues.put(CUSTOMER_EMAIL_ID, email);
        contentValues.put(CUSTOMER_AREA_CODE, area);
        contentValues.put(CUSTOMER_ADDRESS, address);
        contentValues.put(CUSTOMER_PINCODE, pincode);
        contentValues.put(CUSTOMER_STATUS, status);

        long record=db.insert(CUSTOMER_TABLE_NAME, null, contentValues);
        db.close();
        if(record!=-1)
            return true;
        else
            return false;
    }*/
//Cursor findEntry = db.query(AGENT_TABLE_NAME,null, "agent_id=? and agent_password=?", new String[] { agentId, agentPassword }, null, null, null);
    public String checkForAgent(String agentName, String agentPassword) {
        String agentID = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "select agent_id from agent where agent_name ='" + agentName + "' and agent_password ='" + agentPassword + "'";
            Cursor findEntry = db.rawQuery(sql, null);
            if (findEntry != null&&findEntry.getCount()>0) {
                findEntry.moveToFirst();
                agentID = findEntry.getString(0);
                Log.e(TAG, "Found Agent");
            } else
                Log.e(TAG, "No Matching Agent");
        } catch (Exception e) {
            Log.e(TAG, "Exception Happend inserting Customer::" + e.getMessage());
        }
        return agentID;
    }

    public void insertCustomer(int customerID, String firstName, String address) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CUSTOMER_ID, customerID);
            contentValues.put(CUSTOMER_FIRST_NAME, firstName);
            contentValues.put(CUSTOMER_ADDRESS, address);

            long record = db.insertWithOnConflict(CUSTOMER_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
            if (record != -1)
                Log.e(TAG, "Customer insert success");
            else
                Log.e(TAG, "Customer insert failed");
        } catch (Exception e) {
            Log.e(TAG, "Exception Happend inserting Customer::" + e.getMessage());
        }

    }

    public void deleteAllCustomers() {
        try {
            Log.d(TAG, "deleteAllCustomers()");
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(CUSTOMER_TABLE_NAME, null, null);
        } catch (Exception e) {

        }
    }

    public void insertAgent(int agentID, String agentName, String agentPassword) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(AGENT_ID, agentID);
            contentValues.put(AGENT_NAME, agentName);
            contentValues.put(AGENT_PASSWORD, agentPassword);

            long record = db.insert(AGENT_TABLE_NAME, null, contentValues);
            db.close();
            if (record != -1)
                Log.d(TAG, "Agent insert success");
            else
                Log.e(TAG, "Agent insert failed");
        } catch (Exception e) {
            Log.e(TAG, "Exception Happend inserting Agent::" + e.getMessage());
        }

    }

    public void deleteAllAgents() {
        Log.d(TAG, "deleteAllAgents()");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(AGENT_TABLE_NAME, null, null);
    }

    public boolean insertCollectionData(int customerID, String firstName, String date, int amount, String agentName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_ID, customerID);
        contentValues.put(CUSTOMER_FIRST_NAME, firstName);
        contentValues.put(COLLECTION_DATE, date);
        contentValues.put(COLLECTION_AMOUNT, amount);
        contentValues.put(AGENT_NAME, agentName);


        long record = db.insert(COLLECTION_TABLE_NAME, null, contentValues);
        db.close();
        if (record != -1)
            return true;
        else
            return false;
    }

    public boolean insertPaymentHistory(int customerID, String paymentDate, int amount, String paymentStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_ID, customerID);
        contentValues.put(PAYMENT_DATE, paymentDate);
        contentValues.put(PAYMENT_AMOUNT, amount);
        contentValues.put(PAYMENT_STATUS, paymentStatus);


        long record = db.insert(PAYMENT_TABLE_NAME, null, contentValues);
        db.close();
        if (record != -1)
            return true;
        else
            return false;
    }

    public ArrayList<HashMap<String, String>> checkCustomerPaymentHistory(int customerId) {
        ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "select payment_date,payment_amount from payment where customer_id ='" + customerId + "' and payment_status ='UNPAID'";
            Cursor res = db.rawQuery(sql, null);
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                HashMap<String, String> customer_data = new HashMap<String, String>();
                customer_data.put("payment_date", res.getString(0));
                customer_data.put("payment_amount", res.getString(1));
                Log.d(TAG, "Customer Payment History::" + customer_data);
                resultList.add(customer_data);
                res.moveToNext();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in getting Payment history::" + e.getMessage());
        }
        return resultList;
    }

    public JSONArray getCollectionData() {
        JSONArray collectiondata = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from collection", null);
        res.moveToFirst();
        JSONObject jsondata = null;
        while (res.isAfterLast() == false) {
            try {
                jsondata = new JSONObject();
                jsondata.accumulate(Constants.CUSTOMER_ID_KEY, res.getInt(0));
                jsondata.accumulate(Constants.COLLECTED_DATE_KEY, res.getString(2));
                jsondata.accumulate(Constants.COLLECTED_AMOUNT_KEY, res.getInt(3));
                collectiondata.put(jsondata);
                res.moveToNext();
            } catch (Exception e) {
            }
        }
        Log.d(TAG, "Json DATA::" + Arrays.asList(collectiondata));
        return collectiondata;
    }

    public ArrayList<HashMap<String, String>> getAllCustomers() {
        ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select customer_id,first_name from customer", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            HashMap<String, String> customer_data = new HashMap<String, String>();
            customer_data.put("customer_id", res.getString(0));
            customer_data.put("customer_name", res.getString(1));
            Log.d(TAG, "Customer ID::" + res.getInt(res.getColumnIndex(CUSTOMER_ID)) + "Customer Name::" + res.getString(res.getColumnIndex(CUSTOMER_FIRST_NAME)));
            resultList.add(customer_data);
            res.moveToNext();
        }

        Log.d(TAG, "ArrayLIST DATA::" + Arrays.asList(resultList));
        return resultList;
    }

    public boolean insertAgent(String agentName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AGENT_NAME, agentName);

        long record = db.insert(AGENT_TABLE_NAME, null, contentValues);
        db.close();
        if (record != -1)
            return true;
        else
            return false;
    }

    public boolean insertArea(String areaName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AREA_NAME, areaName);

        long record = db.insert(AREA_TABLE_NAME, null, contentValues);
        db.close();
        if (record != -1)
            return true;
        else
            return false;
    }

    public boolean updateCustomer(Integer customerId, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CUSTOMER_ADDRESS, address);

        db.update("customer", contentValues, "customer_id = ? ", new String[]{Integer.toString(customerId)});
        return true;
    }

    public Cursor getCustomerData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from customer", null);
        return res;
    }

    public String getCustomerAddress(int customerid) {
        Log.d(TAG, "Getting Customer Address for :: " + customerid);
        String address = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select address from customer where customer_id ='" + customerid + "'", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            address = res.getString(res.getColumnIndex(CUSTOMER_ADDRESS));
            Log.d(TAG, "Customer Address::" + res.getString(res.getColumnIndex(CUSTOMER_ADDRESS)));
            res.moveToNext();
        }
        return address;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CUSTOMER_TABLE_NAME);
        return numRows;
    }

    public boolean deleteCustomerRecord() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CUSTOMER_TABLE_NAME, null, null);
        return true;
    }

    public ArrayList<HashMap<Integer, String>> getAllAgents() {
        HashMap<Integer, String> agent_data = new HashMap<Integer, String>();
        ArrayList<HashMap<Integer, String>> resultList = new ArrayList<HashMap<Integer, String>>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from agent", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            agent_data.put(res.getInt(0), res.getString(1));
            Log.d(TAG, "AGENT ID::" + res.getInt(res.getColumnIndex(AGENT_ID)) + "AGENT Name::" + res.getString(res.getColumnIndex(AGENT_NAME)));
            res.moveToNext();
        }
        resultList.add(agent_data);
        Log.d(TAG, "ArrayLIST DATA::" + Arrays.asList(resultList));
        return resultList;
    }

    public ArrayList<String> getAgentsList() {
        ArrayList<String> resultList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select agent_name from agent", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            resultList.add(res.getString(res.getColumnIndex(AGENT_NAME)));
            Log.d(TAG, "AGENT Name::" + res.getString(res.getColumnIndex(AGENT_NAME)));
            res.moveToNext();
        }
        Log.d(TAG, "ArrayLIST DATA::" + Arrays.asList(resultList));
        return resultList;
    }


    public ArrayList<String> getAllCustomerId() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from customer", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(CUSTOMER_ID)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }


    }
}

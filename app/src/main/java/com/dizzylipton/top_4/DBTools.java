package com.dizzylipton.top_4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Patrick on 12/15/2014.
 */
public class DBTools extends SQLiteOpenHelper {

    public DBTools(Context appContext) {
        super(appContext, "top4_book.db", null, 1);
    }

    public void onCreate(SQLiteDatabase database) {

        String query = "CREATE TABLE top4 (btnId TEXT, contactName TEXT, contactNumber TEXT)";
        database.execSQL(query);

        for (int i = 0; i < 4; i++) {
            HashMap<String, String> initMap = new HashMap<>();
            initMap.put("btnId", Integer.toString(i));
            initMap.put("contactName", "Not Yet Assigned");
            initMap.put("contactNumber", "0");
            insertInitTop4(initMap, database);
        }

    }

    public void onUpgrade(SQLiteDatabase database, int version_old, int version_new) {
        String query = "DROP TABLE IF EXISTS top4";
        database.execSQL(query);
        onCreate(database);
    }

    public ArrayList<HashMap<String, String>> getAllContacts() {


        SQLiteDatabase database = this.getWritableDatabase();
        //String delQuery = "DROP TABLE IF EXISTS top4";
        //database.execSQL(delQuery);
        //onCreate(database);
        //database = this.getWritableDatabase();

        ArrayList<HashMap<String, String>> top4ArrayList;
        top4ArrayList = new ArrayList<>();
        String selectQuery = "SELECT * FROM top4";


        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                HashMap<String, String> top4Map = new HashMap<>();
                top4Map.put("btnId", cursor.getString(0));
                top4Map.put("contactName", cursor.getString(1));
                top4Map.put("contactNumber", cursor.getString(2));

                top4ArrayList.add(top4Map);
            } while(cursor.moveToNext());
        }
        return top4ArrayList;
    }
    public void insertInitTop4(HashMap<String, String> queryValues, SQLiteDatabase database) {

        // This method should only be called once to initialize the database

        ContentValues values = new ContentValues();
        values.put("btnId", queryValues.get("btnId"));
        values.put("contactName", queryValues.get("contactName"));
        values.put("contactNumber", queryValues.get("contactNumber"));

        database.insert("top4", null, values);
    }

    public void dropDatabase(SQLiteDatabase database) {
        String query = "DROP TABLE IF EXISTS top4";
        database.execSQL(query);
        onCreate(database);
    }

    public int updateTop4(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("contactName", queryValues.get("contactName"));
        values.put("contactNumber", queryValues.get("contactNumber"));

        return database.update("top4", values, "btnId" + " = ?", new String[]{ queryValues.get("btnId")});
    }

    public void deleteTop4(String id) {
        // This will never be used, places in here for reference
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM top4 WHERE btnId ='" + id + "'";
        database.execSQL(deleteQuery);
    }

    public String getContactNumber(String id) {

        String contactNumber = "";
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT contactNumber from top4 where btnId='" + id + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                contactNumber = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        return contactNumber;
    }
}






package com.dizzylipton.top_4;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Patrick on 12/15/2014.
 */
public class DBTools extends SQLiteOpenHelper {

    Context mContext = null;

    public DBTools(Context appContext) {

    super(appContext, "top4_book.db", null, 1);
        mContext = appContext;
    }

    public void onCreate(SQLiteDatabase database) {

        String query = "CREATE TABLE top4 (btnId INTEGER, " +
                "contactName TEXT, " +
                "contactNumber TEST, " +
                "contactAvatar BLOB)";

        database.execSQL(query);

        for (int i = 0; i < 4; i++) {
            Top4Contact t4c = new Top4Contact();
            t4c.setContactId(i);
            t4c.setContactName("Not Yet Assigned");
            t4c.setContactNumber("0");
            Resources res = mContext.getResources();
            t4c.setContactAvatar( BitmapFactory.decodeResource(res, R.drawable.default_avatar ));
            insertInitTop4(t4c, database);
        }
    }

    public void onUpgrade(SQLiteDatabase database, int version_old, int version_new) {
        String query = "DROP TABLE IF EXISTS top4";
        database.execSQL(query);
        onCreate(database);
    }

    public ArrayList<Top4Contact> getAllContacts() {

        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<Top4Contact> top4ArrayList;
        top4ArrayList = new ArrayList<>();
        String selectQuery = "SELECT * FROM top4";

        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                Top4Contact t4c = new Top4Contact();
                t4c.setContactId(cursor.getInt(0));
                t4c.setContactName(cursor.getString(1));
                t4c.setContactNumber(cursor.getString(2));
                t4c.setContactAvatar( BitmapFactory.decodeByteArray(cursor.getBlob(3), 0, cursor.getBlob(3).length));

                top4ArrayList.add(t4c);
            } while(cursor.moveToNext());
        }
        return top4ArrayList;
    }

    public void insertInitTop4(Top4Contact t4c, SQLiteDatabase database) {

        // This method should only be called once to initialize the database

        ContentValues values = new ContentValues();
        values.put("btnId", t4c.getContactId());
        values.put("contactName", t4c.getContactName());
        values.put("contactNumber", t4c.getContactNumber());
        byte[] avatar = bitmap2byte(t4c.getContactAvatar());
        values.put("contactAvatar", avatar);

        database.insert("top4", null, values);
    }

    public void dropDatabase(SQLiteDatabase database) {
        String query = "DROP TABLE IF EXISTS top4";
        database.execSQL(query);
        onCreate(database);
    }

    public int updateTop4(Top4Contact t4c) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("contactName", t4c.getContactName());
        values.put("contactNumber", t4c.getContactNumber());
        byte[] avatar = bitmap2byte(t4c.getContactAvatar());
        values.put("contactAvatar", avatar);

        return database.update("top4", values, "btnId" + " = ?", new String[]{ Integer.toString(t4c.getContactId()) });
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

    private byte[] bitmap2byte(Bitmap b) {
        ByteArrayOutputStream byteMapOutStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, byteMapOutStream);
        byte[] byteArray = byteMapOutStream.toByteArray();
        return byteArray;
    }
}






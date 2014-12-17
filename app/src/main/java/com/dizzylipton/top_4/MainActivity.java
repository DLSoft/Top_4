package com.dizzylipton.top_4;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {


    private final static int PICK_CONTACT = 10;
    private final static String DEBUG_TAG = "PAT";

    Button[] callButton = new Button[4];
    int callButtonId;

    final int[] btnR = new int[]{
            R.id.callButton1,
            R.id.callButton2,
            R.id.callButton3,
            R.id.callButton4,
    };

    DBTools dbTools = new DBTools(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<HashMap<String, String>> top4List = dbTools.getAllContacts();

        for (int i = 0; i < top4List.size(); i++) {

            callButton[i] = (Button) findViewById(btnR[i]);
            callButton[i].setText(top4List.get(i).get("contactName"));

            callButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = "";
                    for (int i = 0; i < 4; i++) {
                        if (v.getId() == btnR[i]) {
                            number = dbTools.getContactNumber(Integer.toString(i));
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + number));
                            startActivity(callIntent);
                            break;

                        }
                    }

                    Log.e(DEBUG_TAG, number);
                }
            });

            callButton[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(DEBUG_TAG, "Long CLick");
                    doLaunchContactPicker(v);
                    return false;
                }
            });
        }
    }

    public void doLaunchContactPicker(View sender) {
        Log.d(DEBUG_TAG, "Launch Contact");

        for (int i = 0; i < 4; i++) {
            if (btnR[i] == sender.getId()) {
                callButtonId = i;
                break;
            }
        }
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, PICK_CONTACT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String number = "";
        String name = "";


        if (resultCode == RESULT_OK && requestCode == PICK_CONTACT) {

            Cursor cursor = null;

            try {
                Uri result = data.getData();
                String id = result.getLastPathSegment();

                String[] projections = {Phone._ID,
                        Phone.NUMBER,
                        ContactsContract.Contacts.DISPLAY_NAME,
                };

                cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                        projections,
                        Phone._ID + "=?" + " AND " + ContactsContract.Contacts.Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'",
                        new String[]{String.valueOf(id)},
                        null);

                int numberIdx = cursor.getColumnIndex(Phone.DATA);


                if (cursor.moveToFirst()) {
                    number = cursor.getString(numberIdx);
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        try {
            Button callButton = (Button) findViewById(btnR[callButtonId]);
            callButton.setText(name);
            HashMap<String, String> updateMap = new HashMap<>();
            updateMap.put("btnId", Integer.toString(callButtonId));
            updateMap.put("contactNumber", number);
            updateMap.put("contactName", name);

            dbTools.updateTop4(updateMap);
            //updateButtons();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

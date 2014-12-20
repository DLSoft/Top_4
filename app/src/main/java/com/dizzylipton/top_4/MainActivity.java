package com.dizzylipton.top_4;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Patrick on 12/15/2014.
 */
public class MainActivity extends ActionBarActivity {


    private final static int PICK_CONTACT = 10;
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

        final AlertDialog.Builder fragDlg = new AlertDialog.Builder(this);

        ArrayList<Top4Contact> top4List = dbTools.getAllContacts();

        for (int i = 0; i < top4List.size(); i++) {

            callButton[i] = (Button) findViewById(btnR[i]);
            callButton[i].setText(top4List.get(i).getContactName());
            Drawable drawable = new BitmapDrawable(getResources(), top4List.get(i).getContactAvatar());
            drawable.setBounds(0, 0, 60, 60);
            callButton[i].setCompoundDrawables(drawable, null, null, null);

            callButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = "";
                    for (int i = 0; i < 4; i++) {
                        if (v.getId() == btnR[i]) {
                            number = dbTools.getContactNumber(Integer.toString(i));
                            if (number.equals("0")) {
                                fragDlg.setTitle(getString(R.string.error_invalid_number));
                                fragDlg.setMessage(getString(R.string.error_invalid_msg));
                                fragDlg.setCancelable(true);
                                fragDlg.setNeutralButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alertDlg = fragDlg.create();
                                alertDlg.show();

                            } else {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + number));
                                startActivity(callIntent);
                            }
                            break;
                        }
                    }
                }
            });

            callButton[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    doLaunchContactPicker(v);
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                final AlertDialog.Builder fragDlg = new AlertDialog.Builder(this);
                fragDlg.setTitle(getString(R.string.help_button_option));
                String msg = getString(R.string.help_long_msg) + "\n" + getString(R.string.help_short_msg);
                fragDlg.setMessage(msg);
                fragDlg.setCancelable(true);
                fragDlg.setNeutralButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDlg = fragDlg.create();
                alertDlg.show();
                return true;
            case R.id.action_exit:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void doLaunchContactPicker(View sender) {

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

        String number = null;
        String name = "";
        Bitmap avatar = null;
        Integer avatarId = null;

        if (resultCode == RESULT_OK && requestCode == PICK_CONTACT) {

            Cursor cursor = null;

            try {
                final Uri result = data.getData();
                String id = result.getLastPathSegment();

                String[] projections = {Phone._ID,
                        Phone.NUMBER,
                        ContactsContract.Contacts.PHOTO_ID,
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
                    avatarId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
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
            if (number.equals("")) {
                name = "Not Assigned";
                number = "0";
            }
            Button callButton = (Button) findViewById(btnR[callButtonId]);
            callButton.setText(name);
            avatar = getAvatar(avatarId);

            if (avatar == null) {
                avatar = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
            }
            Drawable drawable = new BitmapDrawable(getResources(), avatar);
            drawable.setBounds(0, 0, 60, 60);
            callButton.setCompoundDrawables(drawable, null, null, null);
            Top4Contact t4c = new Top4Contact(callButtonId, name, number, avatar);
            dbTools.updateTop4(t4c);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getAvatar(int avatarId) {

        String[] PHOTO_BITMAP_PROJECTION = new String[]{
                ContactsContract.CommonDataKinds.Photo.PHOTO
        };

        Bitmap avatar = null;
        Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, avatarId);
        Cursor cursor = getContentResolver().query(uri, PHOTO_BITMAP_PROJECTION, null, null, null);

        try {
            if (cursor.moveToFirst()) {
                byte[] avatarBytes = cursor.getBlob(0);
                if (avatarBytes != null) {
                    avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                }
            }
        } finally {
            cursor.close();
        }
        return avatar;
    }
}

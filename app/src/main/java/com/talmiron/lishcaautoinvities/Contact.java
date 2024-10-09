package com.talmiron.lishcaautoinvities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Contact implements Serializable {

    public String number;
    public String name = "";
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Contact(String number, Context context){
        this.isChecked = true;
        this.number = number;
        this.name = getContactName(context);
    }

    public String GetDisplayText(){
        if (!name.isEmpty()){
            return name;
        }
        return formatNumber(number);
    }

    private String formatNumber(String phoneNumber) {
        return "0" + phoneNumber.substring(0, 2) + "-" + phoneNumber.substring(2);
    }

    private String getContactName(Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }
}

package com.talmiron.lishcaautoinvities;

import static java.security.AccessController.getContext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqlManager {

    private static FeedReaderDbHelper dbHelper;

    // Initialize the database helper
    public static void initialize(Context context) {
        dbHelper = new FeedReaderDbHelper(context);
    }

    // Static insert method
    public static void insert(String title, String name, String date, String hour, String rawData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_TITLE, title);
        values.put(FeedReaderContract.FeedEntry.COLUMN_MEETING_NAME, name);
        values.put(FeedReaderContract.FeedEntry.COLUMN_DATE, date);
        values.put(FeedReaderContract.FeedEntry.COLUMN_HOUR, hour);
        values.put(FeedReaderContract.FeedEntry.COLUMN_RAW_DATA, rawData);

        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
        if (newRowId == -1) {
            // Handle the error
            Log.e("SqlManager", "Error inserting data");
        } else {
            Log.d("SqlManager", "Data inserted with row ID: " + newRowId);
        }
    }

    public static int getRowCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String countQuery = "SELECT COUNT(*) FROM " + FeedReaderContract.FeedEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int rowCount = cursor.getInt(0);
        cursor.close();
        return rowCount;
    }

    public static void DeleteRows(int num) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + FeedReaderContract.FeedEntry.TABLE_NAME +
                " WHERE " + FeedReaderContract.FeedEntry._ID + " IN (SELECT " +
                FeedReaderContract.FeedEntry._ID + " FROM " +
                FeedReaderContract.FeedEntry.TABLE_NAME + " ORDER BY " +
                FeedReaderContract.FeedEntry._ID + " LIMIT " + num + ")");
        db.close();
    }

    public static Meeting[] GetHistoryActivityData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(FeedReaderContract.FeedEntry.TABLE_NAME, null, null, null, null, null, null);

        Meeting[] meetings = new Meeting[cursor.getCount()];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                int Id = (cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID)));
                String Title = (cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_TITLE)));
                String Name = (cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_MEETING_NAME)));
                String Date = (cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_DATE)));
                String Hour = (cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_HOUR)));
                Meeting meeting = new Meeting(Id, Name, Title, Date, Hour);
                meetings[i++] = meeting;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return meetings;
    }

    public static String GetRawData(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(FeedReaderContract.FeedEntry.TABLE_NAME,
                new String[]{FeedReaderContract.FeedEntry.COLUMN_RAW_DATA},
                FeedReaderContract.FeedEntry._ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String rawData = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_RAW_DATA));
            cursor.close();
            db.close();
            return rawData;
        } else {
            db.close();
            return null;
        }
    }

}

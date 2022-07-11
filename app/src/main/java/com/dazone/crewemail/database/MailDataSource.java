package com.dazone.crewemail.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dazone.crewemail.data.MailBoxData;
import com.google.gson.Gson;

import java.util.ArrayList;


public class MailDataSource extends DataSourceBase {
    static final String TABLE_NAME = "Mail";
    private static final String DATA = "Data";

    public static final String ID = "MailNo";
    private static final String USER_NO = "UserNo";
    private static final String DATE = "RegDateToString";
    private static final String BOX_NO = "BoxNo";
    private static final String DELETED = "Deleted";

    static final String SQL_EXECUTE_MAIL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " INTEGER primary key not null, "
            + USER_NO + " INTEGER, "
            + BOX_NO + " INTEGER, "
            + DATA + " TEXT, "
            + DATE + " TEXT, "
            + DELETED + " INTEGER default 0"
            + ");";

    MailDataSource(Context context) {
        super(context);
    }

    public MailBoxData getMail(long mailNo) {
        MailBoxData result = null;

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = " + mailNo;
        Cursor cursor = database.rawQuery(selectQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                String data = cursor.getString(cursor.getColumnIndex(DATA));
                result = new Gson().fromJson(data, MailBoxData.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }


        return result;
    }

    public ArrayList<Long> getMailDetailIds() {
        ArrayList<Long> result = new ArrayList<>();

        String selectQuery = "SELECT " + ID + " FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(selectQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long data = cursor.getLong(cursor.getColumnIndex(ID));
                    result.add(data);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }


        return result;
    }

    public boolean addMailDetail(MailBoxData data) {
        try {
            ContentValues values = new ContentValues();
            values.put(ID, data.getMailNo());
            values.put(USER_NO, data.getUserNo());
            values.put(BOX_NO, data.getBoxNo());
            values.put(DATA, new Gson().toJson(data));
            values.put(DATE, data.getDateCreate());
            values.put(DELETED, 0);

            long val = database.insert(TABLE_NAME, null, values);

            Log.d(">>>sss", "addMailDetail " + data.getMailNo());
            return val >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    void clearData() {
        try {
            String deleteQuery = "DELETE FROM " + TABLE_NAME;
            database.execSQL(deleteQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

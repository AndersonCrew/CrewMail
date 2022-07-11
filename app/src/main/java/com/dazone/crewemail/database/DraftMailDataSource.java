package com.dazone.crewemail.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dazone.crewemail.data.DraftMailData;
import com.dazone.crewemail.data.MailBoxData;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DraftMailDataSource extends DataSourceBase {
    static final String TABLE_NAME = "DraftMail";
    private static final String DATA = "Data";

    public static final String ID = "MailNo";
    private static final String USER_NO = "UserNo";
    private static final String DRAFT_TYPE = "DraftType";
    private static final String DATE = "RegDateToString";
    private static final String BOX_NO = "BoxNo";
    private static final String DELETED = "Deleted";
    public static final int COMPOSE_TYPE_NEW = 1;
    public static final int COMPOSE_TYPE_FORWARRD = 2;
    public static final int COMPOSE_TYPE_REPLY = 3;
    public static final int COMPOSE_TYPE_NEW_DRAFT = 4;
    public static final int COMPOSE_TYPE_FORWARRD_DRAFT = 5;
    public static final int COMPOSE_TYPE_REPLY_DRAFT = 6;

    static final String SQL_EXECUTE_MAIL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " INTEGER primary key autoincrement not null, "
            + USER_NO + " INTEGER, "
            + DRAFT_TYPE + " INTEGER, "
            + BOX_NO + " INTEGER, "
            + DATA + " TEXT, "
            + DATE + " TEXT, "
            + DELETED + " INTEGER default 0"
            + ");";

    DraftMailDataSource(Context context) {
        super(context);
    }

    ArrayList<DraftMailData> getMails() {
        ArrayList<DraftMailData> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(selectQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String data = cursor.getString(cursor.getColumnIndex(DATA));
                    long id = cursor.getLong(cursor.getColumnIndex(ID));
                    int type = cursor.getInt(cursor.getColumnIndex(DRAFT_TYPE));
                    MailBoxData mailBoxData = new Gson().fromJson(data, MailBoxData.class);
                    DraftMailData mailData = new DraftMailData();
                    mailData.setId(id);
                    mailData.setDraftType(type);
                    mailData.setData(mailBoxData);
                    result.add(mailData);
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

    boolean addDraftMail(MailBoxData data, int draftType) {
        try {
            ContentValues values = new ContentValues();
            values.put(DRAFT_TYPE, draftType);
            values.put(USER_NO, data.getUserNo());
            values.put(BOX_NO, data.getBoxNo());
            values.put(DATA, new Gson().toJson(data));
            values.put(DATE, data.getDateCreate());
            values.put(DELETED, 0);

            long val = database.insert(TABLE_NAME, null, values);

            return val >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean deleteDraftMail(long mailNo) {
        try {
            String deleteQuery = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = " + mailNo;
            database.execSQL(deleteQuery);
            return true;
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

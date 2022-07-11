package com.dazone.crewemail.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.data.UserData;

import java.util.ArrayList;

/**
 * Created by maidinh on 8/13/2015.
 */
public class UserDBHelper {
    public static final String TABLE_NAME = "UserTbl";
    public static final String ID = "Id";
    public static final String USER_ID = "user_id";
    public static final String USER_SESSION = "user_session";
    public static final String USER_NAME = "user_name";
    public static final String USER_COMPANY = "user_company";
    public static final String USER_FULLNAME = "user_fullname";
    public static final String USER_AVATAR = "user_avartar";
    public static final String USER_EMAIL = "user_email";

    public static final String SQL_EXCUTE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " integer primary key autoincrement not null," + USER_ID + " integer," + USER_SESSION
            + " text, " + USER_NAME + " text, " + USER_COMPANY + " text, "
            + USER_FULLNAME + " text, " + USER_EMAIL + " text, " + USER_AVATAR + " text );";

    public static UserData getUser() {
        String[] columns = new String[]{"*"};
        ContentResolver resolver = DaZoneApplication.getInstance()
                .getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_USER_CONTENT_URI, columns, null,
                null, null);
        UserData userData = new UserData();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {
                    if (cursor.moveToFirst()) {
                        userData.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(USER_ID))));
                        userData.setSession(cursor.getString(cursor.getColumnIndex(USER_SESSION)));
                        userData.setUserId(cursor.getString(cursor.getColumnIndex(USER_NAME)));
                        userData.setCompanyName(cursor.getString(cursor.getColumnIndex(USER_COMPANY)));
                        userData.setFullName(cursor.getString(cursor.getColumnIndex(USER_FULLNAME)));
                        userData.setAvatar(cursor.getString(cursor.getColumnIndex(USER_AVATAR)));
                        userData.setmEmail(cursor.getString(cursor.getColumnIndex(USER_EMAIL)));
                    }

                } finally {
                    cursor.close();
                }

            }
        }
        return userData;
    }



    public static boolean addUser(UserData userData) {
        clearUser();
        try {
            ContentValues values = new ContentValues();
            values.put(USER_ID, userData.getId());
            values.put(USER_SESSION, userData.getSession());
            values.put(USER_NAME, userData.getUserId());
            values.put(USER_COMPANY, userData.getCompanyName());
            values.put(USER_FULLNAME, userData.getFullName());
            values.put(USER_AVATAR, userData.getAvatar());
            values.put(USER_EMAIL, userData.getmEmail());

            ContentResolver resolver = DaZoneApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.insert(AppContentProvider.GET_USER_CONTENT_URI, values);
            return true;
        } catch (Exception e) {
           e.printStackTrace();
        }
        return false;
    }

    public static boolean clearUser() {
        try {
            ContentResolver resolver = DaZoneApplication.getInstance()
                    .getApplicationContext().getContentResolver();
            resolver.delete(AppContentProvider.GET_USER_CONTENT_URI, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

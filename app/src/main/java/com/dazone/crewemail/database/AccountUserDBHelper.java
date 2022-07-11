package com.dazone.crewemail.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.data.AccountData;

import java.util.ArrayList;

public class AccountUserDBHelper {
    public static final String TABLE_NAME = "account_tbl";
    public static final String ID = "Id";
    public static final String ACCOUNT_NO = "account_no";
    public static final String ACCOUNT_USER_NO = "account_user_no";
    public static final String ACCOUNT_SERVER = "account_server";
    public static final String ACCOUNT_POPUSER = "account_popuser";
    public static final String ACCOUNT_NAME = "account_name";
    public static final String ACCOUNT_MAIL = "account_mail";

    public static final String SQL_EXCUTE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " integer primary key autoincrement not null," +ACCOUNT_NO+" integer default 0,"+ ACCOUNT_USER_NO
            + " integer default 0, "+ ACCOUNT_SERVER + " text, "
            + ACCOUNT_POPUSER + " text, "+ ACCOUNT_NAME + " text, "
            + ACCOUNT_MAIL + " text );" ;

    public static ArrayList<AccountData> getList() {
        String[] columns = new String[]{"*"};
        ContentResolver resolver = DaZoneApplication.getInstance()
                .getApplicationContext().getContentResolver();
        ArrayList<AccountData> arrayList = new ArrayList<>();
        Cursor cursor = resolver.query(
                AppContentProvider.GET_ACCOUNT_CONTENT_URI, columns, null,
                null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                try {

                    while (!cursor.isLast()) {
                        AccountData userData=new AccountData();
                        cursor.moveToNext();
                        userData.setAccountNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ACCOUNT_NO))));
                        userData.setUserNo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ACCOUNT_USER_NO))));
                        userData.setServer(cursor.getString(cursor.getColumnIndex(ACCOUNT_SERVER)));
                        userData.setPopUser(cursor.getString(cursor.getColumnIndex(ACCOUNT_POPUSER)));
                        userData.setMailAddress(cursor.getString(cursor.getColumnIndex(ACCOUNT_MAIL)));
                        userData.setName(cursor.getString(cursor.getColumnIndex(ACCOUNT_NAME)));
                        arrayList.add(userData);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return arrayList;
    }

    public static boolean addUser(ArrayList<AccountData> list) {
        clearUser();
        try {
            if (list!=null&&list.size()>0) {
                for (AccountData userData:list ) {
                    ContentValues values = new ContentValues();
                    values.put(ACCOUNT_NO, userData.getAccountNo());
                    values.put(ACCOUNT_USER_NO, userData.getUserNo());
                    values.put(ACCOUNT_MAIL, userData.getMailAddress());
                    values.put(ACCOUNT_NAME, userData.getName());
                    values.put(ACCOUNT_POPUSER, userData.getPopUser());
                    values.put(ACCOUNT_SERVER, userData.getServer());
                    ContentResolver resolver = DaZoneApplication.getInstance()
                            .getApplicationContext().getContentResolver();
                    resolver.insert(AppContentProvider.GET_ACCOUNT_CONTENT_URI, values);
                }
            }
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
            resolver.delete(AppContentProvider.GET_ACCOUNT_CONTENT_URI, null,null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

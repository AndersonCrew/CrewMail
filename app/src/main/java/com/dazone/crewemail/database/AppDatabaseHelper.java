package com.dazone.crewemail.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Util;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = Statics.DATABASE_NAME;
    public static final int DB_VERSION = Statics.DATABASE_VERSION;
    public static final String[] TABLE_NAMES = new String[] {
            ServerSiteDBHelper.TABLE_NAME, UserDBHelper.TABLE_NAME,
            OrganizationUserDBHelper.TABLE_NAME, AccountUserDBHelper.SQL_EXCUTE,
            MailListDataSource.TABLE_NAME, MailDataSource.TABLE_NAME, DraftMailDataSource.TABLE_NAME
    };
    public static final String[] SQL_EXCUTE = new String[] {
            ServerSiteDBHelper.SQL_EXCUTE,UserDBHelper.SQL_EXCUTE,
            OrganizationUserDBHelper.SQL_EXCUTE, AccountUserDBHelper.SQL_EXCUTE,
            MailListDataSource.SQL_EXECUTE_MAIL, MailDataSource.SQL_EXECUTE_MAIL,
            DraftMailDataSource.SQL_EXECUTE_MAIL
    };

    public AppDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public AppDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            execMultipleSQL(db, SQL_EXCUTE);
            db.setTransactionSuccessful();
        } catch (android.database.SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){

            try {
                db.execSQL(UserDBHelper.SQL_EXCUTE);
            } catch (SQLException e) {
                Util.printLogs("Table " + UserDBHelper.TABLE_NAME + " already exists");
            }
            try {
                db.execSQL("ALTER TABLE "+ UserDBHelper.TABLE_NAME + " ADD COLUMN "+UserDBHelper.USER_EMAIL+ " text");
            } catch (SQLException e) {
                Util.printLogs("ADD COLUMN "+UserDBHelper.USER_EMAIL+ " already exists");
            }

            try {
                db.execSQL(OrganizationUserDBHelper.SQL_EXCUTE);
            } catch (SQLException e) {
                Util.printLogs("Table "+ OrganizationUserDBHelper.TABLE_NAME+ " already exists");
            }

            try {
                db.execSQL("ALTER TABLE "+ OrganizationUserDBHelper.TABLE_NAME + " ADD COLUMN "+OrganizationUserDBHelper.ORGANIZATION_DEPART_PARENT_NO + " text");
            } catch (SQLException e) {
                Util.printLogs("ADD COLUMN "+OrganizationUserDBHelper.ORGANIZATION_DEPART_PARENT_NO+ " already exists");
            }

            try {
                db.execSQL(AccountUserDBHelper.SQL_EXCUTE);
            } catch (SQLException e) {
                Util.printLogs("Table "+ AccountUserDBHelper.TABLE_NAME+ " already exists");
            }

        }
        else {
            try {
                dropMultipleSQL(db, TABLE_NAMES);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        this.onCreate(db);
    }
    private void execMultipleSQL(SQLiteDatabase db, String[] sql) throws android.database.SQLException {
        for (String s : sql) {
            if (s.trim().length() > 0) {
                try {
                    db.execSQL(s);
                } catch (android.database.SQLException e) {
                    throw new android.database.SQLException();
                }
            }
        }
    }

    private void execSQL(SQLiteDatabase db, String s) throws android.database.SQLException {
            if (s.trim().length() > 0) {
                try {
                    db.execSQL(s);
                } catch (android.database.SQLException e) {
                    throw new android.database.SQLException();
                }
            }
    }

    private void dropMultipleSQL(SQLiteDatabase db, String[] tablename) throws android.database.SQLException {
        for (String s : tablename) {
            if (s.trim().length() > 0) {
                try {
                    db.execSQL("DROP TABLE IF EXISTS " + s + ";");
                } catch (android.database.SQLException e) {
                    throw new android.database.SQLException();
                }
            }
        }
    }
}

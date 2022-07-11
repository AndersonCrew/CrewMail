package com.dazone.crewemail.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.dazone.crewemail.BuildConfig;


/**
 * Created by maidinh on 8/13/2015.
 */
public class AppContentProvider extends ContentProvider {

    /* database helper */
    public static AppDatabaseHelper mDatabaseHelper;
    private static AppContentProvider mContentProvider;


    public static AppContentProvider getInstance() {
        if (mContentProvider == null) {
            mContentProvider = new AppContentProvider();
        }
        return mContentProvider;
    }

    /* key for uri matches */
    private static final int GET_USER_KEY = 1;
    private static final int GET_USER_ROW_KEY = 2;

    private static final int GET_SERVER_SITE_KEY = 5;
    private static final int GET_SERVER_SITE_ROW_KEY = 6;

    private static final int GET_ORGANIZATION_USER_KEY = 13;
    private static final int GET_ORGANIZATION_USER_ROW_KEY = 14;

    private static final int GET_ACCOUNT_USER_KEY = 15;
    private static final int GET_ACCOUNT_USER_ROW_KEY = 16;

    /* authority */
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider_2";

    /* Uri Matches */
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /* path */
    private static final String GET_USER_PATH = "request";
    private static final String GET_SERVER_SITE_PATH = "request_server_site";
    private static final String GET_ORGANIZATION_USER_PATH = "request_organization_user";
    private static final String GET_ACCOUNT_PATH = "request_acount";

    /* content uri */
    public static final Uri GET_USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_USER_PATH);
    public static final Uri GET_SERVER_SITE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_SERVER_SITE_PATH);
    public static final Uri GET_SERVER_ORGANIZATION_USER_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_ORGANIZATION_USER_PATH);
    public static final Uri GET_ACCOUNT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_ACCOUNT_PATH);

    static {
        sUriMatcher.addURI(AUTHORITY, GET_USER_PATH, GET_USER_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_USER_PATH + "/#", GET_USER_ROW_KEY);

        sUriMatcher.addURI(AUTHORITY, GET_ORGANIZATION_USER_PATH, GET_ORGANIZATION_USER_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_ORGANIZATION_USER_PATH + "/#", GET_ORGANIZATION_USER_ROW_KEY);

        sUriMatcher.addURI(AUTHORITY, GET_ACCOUNT_PATH, GET_ACCOUNT_USER_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_ACCOUNT_PATH + "/#", GET_ACCOUNT_USER_ROW_KEY);

        sUriMatcher.addURI(AUTHORITY, GET_SERVER_SITE_PATH, GET_SERVER_SITE_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_SERVER_SITE_PATH + "/#", GET_SERVER_SITE_ROW_KEY);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        int row_deleted;
        SQLiteDatabase db;
        db = mDatabaseHelper.getWritableDatabase();
        int uriKey = sUriMatcher.match(uri);
        switch (uriKey) {
            case GET_USER_ROW_KEY:
                row_deleted = db.delete(UserDBHelper.TABLE_NAME, UserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_USER_KEY:
                row_deleted = db.delete(UserDBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case GET_ORGANIZATION_USER_ROW_KEY:
                row_deleted = db.delete(OrganizationUserDBHelper.TABLE_NAME, OrganizationUserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_ORGANIZATION_USER_KEY:
                row_deleted = db.delete(OrganizationUserDBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case GET_ACCOUNT_USER_ROW_KEY:
                row_deleted = db.delete(AccountUserDBHelper.TABLE_NAME, AccountUserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;

            case GET_SERVER_SITE_ROW_KEY:
                row_deleted = db.delete(ServerSiteDBHelper.TABLE_NAME, ServerSiteDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_SERVER_SITE_KEY:
                row_deleted = db.delete(ServerSiteDBHelper.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return row_deleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int uriType;
        int insertCount = 0;

        try {

            uriType = sUriMatcher.match(uri);
            SQLiteDatabase sqlDB = mDatabaseHelper.getWritableDatabase();

            switch (uriType) {
                case GET_USER_KEY:
                    try {
                        sqlDB.beginTransaction();
                        for (ContentValues value : values) {
                            long id = sqlDB.insert(UserDBHelper.TABLE_NAME, null, value);
                            if (id > 0)
                                insertCount++;
                        }
                        sqlDB.setTransactionSuccessful();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        sqlDB.endTransaction();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return insertCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        Uri result_uri;
        SQLiteDatabase db;
        db = mDatabaseHelper.getWritableDatabase();
        int uriKey = sUriMatcher.match(uri);
        long id = 0;
        switch (uriKey) {
            case GET_USER_KEY:
                id = db.insertWithOnConflict(UserDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_USER_CONTENT_URI + "/" + id);
                break;
            case GET_ORGANIZATION_USER_KEY:
                id = db.insertWithOnConflict(OrganizationUserDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_USER_CONTENT_URI + "/" + id);
                break;
            case GET_ACCOUNT_USER_KEY:
                id = db.insertWithOnConflict(AccountUserDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_ACCOUNT_CONTENT_URI + "/" + id);
                break;
            case GET_SERVER_SITE_KEY:
                id = db.insertWithOnConflict(ServerSiteDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_SERVER_SITE_CONTENT_URI + "/" + id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return result_uri;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        mDatabaseHelper = new AppDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        Cursor cursor;
        SQLiteDatabase db;
        db = mDatabaseHelper.getWritableDatabase();
        SQLiteQueryBuilder querybuilder = new SQLiteQueryBuilder();
        int uriKey = sUriMatcher.match(uri);
        switch (uriKey) {
            case GET_USER_ROW_KEY:
                querybuilder.appendWhere(UserDBHelper.ID + " = " + uri.getLastPathSegment());
            case GET_USER_KEY:
                querybuilder.setTables(UserDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_ORGANIZATION_USER_ROW_KEY:
                querybuilder.appendWhere(OrganizationUserDBHelper.ID + " = " + uri.getLastPathSegment());
            case GET_ORGANIZATION_USER_KEY:
                querybuilder.setTables(OrganizationUserDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_ACCOUNT_USER_ROW_KEY:
                querybuilder.appendWhere(AccountUserDBHelper.ID + " = " + uri.getLastPathSegment());
            case GET_ACCOUNT_USER_KEY:
                querybuilder.setTables(AccountUserDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_SERVER_SITE_ROW_KEY:
                querybuilder.appendWhere(ServerSiteDBHelper.ID + " = " + uri.getLastPathSegment());
            case GET_SERVER_SITE_KEY:
                querybuilder.setTables(ServerSiteDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        int row_update = 0;
        SQLiteDatabase db;
        db = mDatabaseHelper.getWritableDatabase();
        int uriKey = sUriMatcher.match(uri);
        switch (uriKey) {
            case GET_USER_ROW_KEY:
                row_update = db.update(UserDBHelper.TABLE_NAME, values, UserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_USER_KEY:
                row_update = db.update(UserDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_ORGANIZATION_USER_ROW_KEY:
                row_update = db.update(OrganizationUserDBHelper.TABLE_NAME, values, OrganizationUserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_ORGANIZATION_USER_KEY:
                row_update = db.update(OrganizationUserDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_ACCOUNT_USER_ROW_KEY:
                row_update = db.update(AccountUserDBHelper.TABLE_NAME, values, AccountUserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_ACCOUNT_USER_KEY:
                row_update = db.update(AccountUserDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;

            case GET_SERVER_SITE_ROW_KEY:
                row_update = db.update(ServerSiteDBHelper.TABLE_NAME, values, ServerSiteDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_SERVER_SITE_KEY:
                row_update = db.update(ServerSiteDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return row_update;
    }


}

package com.dazone.crewemail.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class DataSourceBase {
    protected Context _context;
    protected SQLiteDatabase database;
    protected AppDatabaseHelper dbHelper;

    public DataSourceBase(Context context)
    {
        _context = context;
        dbHelper = new AppDatabaseHelper(_context);
    }



    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }
}

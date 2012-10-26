/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.stats;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.Date;

public class StatsLogger extends SQLiteOpenHelper {
    private static final String
        DATABASE_NAME = "Stats",
        REQUEST_INFO_TABLE_CREATE
            = "CREATE TABLE RequestInfo ("
            + "    urlSubdir TEXT,"
            + "    lengthInMilliseconds INTEGER,"
            + "    timestamp DATETIME,"
            + "    fullUrl TEXT"
            + ");",
        LOG_REQUEST_INFO_SQL
            = "INSERT INTO RequestInfo ("
            + "urlSubdir, lengthInMilliseconds, timestamp, fullUrl"
            + ") VALUES ("
            + "?, ?, ?, ?"
            + ");";

    public StatsLogger(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(REQUEST_INFO_TABLE_CREATE);
    }



    public void logRequestInfo(String urlSubdir, String fullUrl, long lengthInMilliseconds) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement(LOG_REQUEST_INFO_SQL);
        stmt.bindString(0, urlSubdir);
        stmt.bindLong(1, lengthInMilliseconds);
        stmt.bindLong(2, new Date().getTime());
        stmt.bindString(3, fullUrl);
        stmt.execute();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}
}
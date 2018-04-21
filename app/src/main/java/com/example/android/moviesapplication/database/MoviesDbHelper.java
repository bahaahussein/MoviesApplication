package com.example.android.moviesapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Professor on 2/7/2018.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movies.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            MoviesContract.MoviesEntry.TABLE_NAME + " (" +
            MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY, "+
            MoviesContract.MoviesEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, "+
            MoviesContract.MoviesEntry.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL, " +
            MoviesContract.MoviesEntry.COLUMN_NAME_DATE + " TEXT NOT NULL, " +
            MoviesContract.MoviesEntry.COLUMN_NAME_POSTER + " TEXT NOT NULL, " +
            MoviesContract.MoviesEntry.COLUMN_NAME_VOTE + " REAL NOT NULL, " +
            MoviesContract.MoviesEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
            MoviesContract.MoviesEntry.COLUMN_NAME_FAVORITE + " INTEGER DEFAULT 0)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}

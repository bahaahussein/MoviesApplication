package com.example.android.moviesapplication.database;

import android.provider.BaseColumns;

/**
 * Created by Professor on 2/7/2018.
 */

public final class MoviesContract {

    private MoviesContract() {}

    public static class MoviesEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_VOTE = "vote";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_POSTER = "poster";
        public static final String COLUMN_NAME_FAVORITE = "favorite";
        public static final String COLUMN_TYPE = "type";
    }
}

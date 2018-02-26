package com.example.android.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.popmovies.data.MovieContract.MovieEntry;

/**
 * {@link MovieDbHelper} is a {@link SQLiteOpenHelper} to build and provide the database for
 * further use.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE = "CREATE TABLE ";
    private static final String START = " (";
    private static final String CLOSE = ");";
    private static final String COMMA = " ,";
    private static final String INT_REQ = " INTEGER NOT NULL";
    private static final String TEXT_REQ = " TEXT NOT NULL";
    private static final String REAL_REQ = " REAL NOT NULL";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = CREATE + MovieEntry.TABLE_NAME + START
                + MovieEntry.MOVIE_ID + INT_REQ + COMMA
                + MovieEntry.MOVIE_ORG_TITLE + TEXT_REQ + COMMA
                + MovieEntry.MOVIE_POSTER + TEXT_REQ + COMMA
                + MovieEntry.MOVIE_RATING + REAL_REQ + COMMA
                + MovieEntry.MOVIE_RELEASE + TEXT_REQ + COMMA
                + MovieEntry.MOVIE_SYNOPSIS + TEXT_REQ + COMMA
                + MovieEntry.MOVIE_REVIEW + TEXT_REQ + COMMA
                + MovieEntry.MOVIE_TRAILER + TEXT_REQ + COMMA
                + MovieEntry.MOVIE_LIST_POPULAR + INT_REQ + COMMA
                + MovieEntry.MOVIE_LIST_RATED + INT_REQ + COMMA
                + MovieEntry.MOVIE_FAVOURITED + INT_REQ + CLOSE;

        Log.v(LOG_TAG, "Sqlite creator string : " + SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}

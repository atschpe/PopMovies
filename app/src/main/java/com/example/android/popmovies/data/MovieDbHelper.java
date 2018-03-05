package com.example.android.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private static final String INT_AUTO = " INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String INT_REQ = " INTEGER NOT NULL";
    private static final String TEXT_REQ = " TEXT NOT NULL";
    private static final String REAL_REQ = " REAL NOT NULL";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = CREATE + MovieEntry.TABLE_NAME + START
                + MovieEntry._ID + INT_AUTO + COMMA
                + MovieEntry.MOVIE_ID + INT_REQ + COMMA
                + MovieEntry.MOVIE_ORG_TITLE + TEXT_REQ + COMMA
                + MovieEntry.MOVIE_POSTER + TEXT_REQ + COMMA
                + MovieEntry.MOVIE_RATING + REAL_REQ + COMMA
                + MovieEntry.MOVIE_RELEASE + TEXT_REQ + COMMA
                + MovieEntry.MOVIE_SYNOPSIS + TEXT_REQ + CLOSE;

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
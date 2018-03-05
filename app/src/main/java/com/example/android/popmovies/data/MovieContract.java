package com.example.android.popmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * {@link MovieContract} holds all the data for the sqlite table which the ContentProvider accesses.
 */
public class MovieContract {

    public static final String CONTENT_AUTH = "com.example.android.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTH);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTH + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTH + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";
        public static final String _ID = BaseColumns._ID;

        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_ORG_TITLE = "title";
        public static final String MOVIE_POSTER = "poster";
        public static final String MOVIE_SYNOPSIS = "Synopsis";
        public static final String MOVIE_RATING = "rating";
        public static final String MOVIE_RELEASE = "release";

    }
}
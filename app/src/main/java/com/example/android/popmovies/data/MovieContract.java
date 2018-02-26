package com.example.android.popmovies.data;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * {@link MovieContract} holds all the data for the sqlite table which the ContentProvider accesses.
 */
public class MovieContract {

    public static final String CONTENT_AUTH = "com.example.android.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTH);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTH + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTH + PATH_MOVIE;

        public static String TABLE_NAME = "movies";

        public static String MOVIE_ID = "id";
        public static String MOVIE_ORG_TITLE = "title";
        public static String MOVIE_POSTER = "poster";
        public static String MOVIE_SYNOPSIS = "Synposis";
        public static String MOVIE_RATING = "rating";
        public static String MOVIE_RELEASE = "release";
        public static String MOVIE_TRAILER = "trailer";
        public static String MOVIE_REVIEW = "review";
        public static String MOVIE_LIST_POPULAR = "popular_list"; //boolean
        public static String MOVIE_LIST_RATED = "rated"; //boolean
        public static String MOVIE_FAVOURITED = "favourited"; //boolean
    }
}

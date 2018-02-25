package com.example.android.popmovies.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.android.popmovies.R;
import com.example.android.popmovies.data.Movie;
import com.example.android.popmovies.data.MovieContract.MovieEntry;

import java.util.ArrayList;

/**
 * {@link MovieUtils} holds all methods concerning writing and reading the Db.
 */
public class MovieUtils {

    private static final int POPULAR_SORT = 0;
    private static final int RATED_SORT = 1;

    public static boolean isPopularSort(Context ctxt) {
        SharedPreferences sharedPref = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(ctxt);
        String sortKey = ctxt.getString(R.string.sort_by_key);
        String sortDefault = ctxt.getString(R.string.popularity_sort_label_default);
        String sortPreference = sharedPref.getString(sortKey, sortDefault);
        if (sortDefault.equals(sortPreference)) {
            return true;
        } else
            return false;
    }

    public static void saveLoadedDataToDb(Uri uri, ArrayList<Movie> movieList, Context ctxt) {

        ContentResolver movieContentResolver = ctxt.getContentResolver();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctxt);

        //Check existing database for duplicates and remove these, so that the data is always
        // up to date and the correct & latest info is displayed.
        if (movieContentResolver != null) {
            String selection = MovieEntry.MOVIE_ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(MovieEntry.MOVIE_ID)};
            Cursor movieIds = movieContentResolver.query(uri, null, selection,
                    selectionArgs, null);

            if (movieIds != null) {
                movieIds.moveToFirst();
                while (movieIds.moveToNext()) {
                    for (int i = 0; i < movieList.size(); i++) {
                        Movie currentMovie = movieList.get(i);
                        int dbIdIndex = movieIds.getColumnIndex(MovieEntry.MOVIE_ID);
                        int dbId = movieIds.getInt(dbIdIndex);
                        if (dbId == currentMovie.getMvId()) {
                            long id = movieIds.getLong(movieIds.getColumnIndex(MovieEntry.MOVIE_ID));
                            Uri currentItem = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                            movieContentResolver.delete(currentItem, null, null);
                        }
                    }
                }
            }
        }

        // Write the data to the database.
        ContentValues[] movieContentValues = new ContentValues[movieList.size()];

        for (int i = 0; i < movieList.size(); i++) {
            Movie currentMovie = movieList.get(i);

            int sort;
            if (isPopularSort(ctxt)) sort = POPULAR_SORT;
            else sort = RATED_SORT;


            //collecting the data
            // Movie Constructor: String mvPoster, String mvTitle, String mvSynopsis,
            // double mvRating, String mvRelease, int movieId
            int movieId = currentMovie.getMvId();
            String movieTitle = currentMovie.getMvTitle();
            String moviePoster = currentMovie.getMvPoster();
            String movieSynopsis = currentMovie.getMvSynopsis();
            double movieRating = currentMovie.getMvRating();
            String movieRelease = currentMovie.getMvRelease();

            //putting data into ContentValues[]
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.MOVIE_ID, movieId);
            movieValues.put(MovieEntry.MOVIE_ORG_TITLE, movieTitle);
            movieValues.put(MovieEntry.MOVIE_POSTER, moviePoster);
            movieValues.put(MovieEntry.MOVIE_RATING, movieRating);
            movieValues.put(MovieEntry.MOVIE_RELEASE, movieRelease);
            movieValues.put(MovieEntry.MOVIE_SYNOPSIS, movieSynopsis);
            movieValues.put(MovieEntry.MOVIE_SORTER, sort);
            movieValues.put(MovieEntry.MOVIE_SORT_ID, i);

            movieContentValues[i] = movieValues;
        }

        movieContentResolver.bulkInsert(uri, movieContentValues);
    }

}


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
import com.example.android.popmovies.data.MovieContract;
import com.example.android.popmovies.data.MovieContract.MovieEntry;

import java.util.ArrayList;

/**
 * {@link MovieUtils} holds all methods concerning writing and reading the Db.
 */
public class MovieUtils {

    private static final int SELECTED_SORT_METHOD = 1;
    private static final int SORT_METHOD_NOT_SELECTED = 0;

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


        //Check existing database for duplicates and remove these, so that the data is always
        // up to date and the correct & latest info is displayed.
        String selection;
        String[] selectionArgs;
        if (movieContentResolver != null) {

            if (isPopularSort(ctxt)) {
                selection = MovieEntry.MOVIE_LIST_POPULAR + "LIKE ?";
                selectionArgs = new String[]{String.valueOf(SELECTED_SORT_METHOD)};
            } else {
                selection = MovieEntry.MOVIE_LIST_RATED + "LIKE ?";
                selectionArgs = new String[]{String.valueOf(SELECTED_SORT_METHOD)};
            }
            movieContentResolver.delete(uri, selection, selectionArgs);
        }

        // Write the data to the database.
        ContentValues[] movieContentValues = new ContentValues[movieList.size()];

        for (int i = 0; i < movieList.size(); i++) {
            Movie currentMovie = movieList.get(i);

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
            if (isPopularSort(ctxt)) {
                movieValues.put(MovieEntry.MOVIE_LIST_POPULAR, SELECTED_SORT_METHOD);
                movieValues.put(MovieEntry.MOVIE_LIST_RATED, SORT_METHOD_NOT_SELECTED);
            } else {
                movieValues.put(MovieEntry.MOVIE_LIST_POPULAR, SORT_METHOD_NOT_SELECTED);
                movieValues.put(MovieEntry.MOVIE_LIST_RATED, SELECTED_SORT_METHOD);
            }
            movieValues.put(MovieEntry.MOVIE_LIST_RATED, i);

            movieContentValues[i] = movieValues;
        }

        movieContentResolver.bulkInsert(uri, movieContentValues);
    }
}
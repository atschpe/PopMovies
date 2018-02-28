package com.example.android.popmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;


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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctxt);
        String sortKey = ctxt.getString(R.string.sort_by_key);
        String sortDefault = ctxt.getString(R.string.popularity_sort_label_default);
        String sortPreference = sharedPref.getString(sortKey, sortDefault);
        if (sortDefault.equals(sortPreference)) {
            return true;
        } else
            return false;
    }

}
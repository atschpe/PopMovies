package com.example.android.popmovies.utils;

import android.content.Context;
import android.net.Uri;

import com.example.android.popmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * {@link NetworkUtils} carries out and supports any internet calls.
 */
public class NetworkUtils {

    //create a /values/api.xml resource and store your api as "movieDB_api_v3" string
    private static String API = "api_key";
    private static String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";

    /**
     * Build the needed url to make the desired server call.
     *
     * @param ctxt The context within with the server call is made.
     * @return the built url.
     */
    private static URL buildUrl(Context ctxt) {
        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendQueryParameter(API, ctxt.getString(R.string.movieDB_api_v3))
                .build();
        try {
            URL url = new URL(buildUri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(Context ctxt) throws IOException {
        HttpURLConnection urlConnect = (HttpURLConnection) buildUrl(ctxt).openConnection();
        try {
            InputStream input = urlConnect.getInputStream();

            Scanner scan = new Scanner(input);
            scan.useDelimiter("\\A");

            boolean hasInput = scan.hasNext();
            String response = null;
            if (hasInput) {
                response = scan.next();
            }
            scan.close();
            return response;
        } finally {
            urlConnect.disconnect();
        }
    }

}
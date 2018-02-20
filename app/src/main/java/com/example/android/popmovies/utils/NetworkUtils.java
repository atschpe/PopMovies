package com.example.android.popmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;

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
    private static String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static String POPULAR_SEGMENT = "popular";
    private static String RATED_SEGMENT = "top_rated";
    final private static String SORT_BY_POPULAR = "Popularity";
    final private static String SORT_BY_RELATED = "Top Rated";

    static String getSortPreference(Context ctxt) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctxt);
        String sortKey = ctxt.getString(R.string.sort_by_key);
        String sortDefault = ctxt.getString(R.string.popularity_sort_default);
        return sharedPref.getString(sortKey, sortDefault);
    }

    /**
     * Build the needed url to make the desired server call.
     *
     * @param ctxt The context within with the server call is made.
     * @return the built url.
     */
    private static URL buildUrl(Context ctxt) {
        String userSelection = null;
        switch (getSortPreference(ctxt)) {
            case SORT_BY_POPULAR:
                userSelection = POPULAR_SEGMENT;
                break;
            case SORT_BY_RELATED:
                userSelection = RATED_SEGMENT;
                break;
        }

        Uri buildUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendEncodedPath(userSelection)
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

    // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
    // based on: https://stackoverflow.com/a/27312494 as pointed out in the project guidelines
//    public static boolean isOnline() {
//        final boolean[] online = new boolean[1];
//        new AsyncTask<Void, Void, Boolean>() {
//
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                try {
//                    int timeoutMs = 1500;
//                    Socket sock = new Socket();
//                    SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
//
//                    sock.connect(sockaddr, timeoutMs);
//                    sock.close();
//
//                    return online[0] = true;
//                } catch (IOException e) {
//                    return online[0] = false;
//                }
//            }
//        }; return online[0];
    //}
}
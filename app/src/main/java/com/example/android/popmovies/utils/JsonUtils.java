package com.example.android.popmovies.utils;

import com.example.android.popmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * {@link JsonUtils} enables and handles all JSON resquests to the server.
 */

public class JsonUtils {

    private static final String JSON_MESSAGE_CODE = "cod";

    //Access to jsonArray:
    private static final String JSON_RESULTS = "results"; //array

    //required for Stage 1 project:
    private static final String JSON_VOTE_AVERAGE = "vote_average"; //double
    private static final String JSON_POSTER_PATH = "poster_path"; //String
    private static final String JSON_ORIGINAL_TITLE = "original_title"; //String
    private static final String JSON_OVERVIEW = "overview"; // String
    private static final String JSON_RELEASE_DATE = "release_date"; // String


    //quick list of other keys – incase of future necessity in Stage 2.
    private static final String JSON_ID = "id"; //int
    private static final String JSON_POPULARITY = "popularity"; //double
    private static final String JSON_VOTE_COUNT = "vote_count"; //int
    private static final String JSON_VIDEO = "video";//boolean
    private static final String JSON_TITLE = "title"; //String
    private static final String JSON_ORIGINAL_LANGUAGE = "original_language"; // String
    private static final String JSON_GENRE_IDS = "genre_ids"; //array; int
    private static final String JSON_BACKDROP_PATH = "backdrop_path"; //String
    private static final String JSON_ADULT = "adult"; // boolean


    public static ArrayList<Movie> getMovieList(String JsonUrl) throws JSONException {

        ArrayList<Movie> movieList = new ArrayList<>();

        JSONObject movieJson = new JSONObject(JsonUrl);

        //check for errors
        if (movieJson.has(JSON_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(JSON_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK: // everything fine
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND: // invalid location
                default: //probably: server down
                    return null;
            }
        }

        JSONArray movieResults = movieJson.getJSONArray(JSON_RESULTS);

        for (int i = 0; i < movieResults.length(); i++) {

            JSONObject movieItem = movieResults.getJSONObject(i);

            double movieVote = movieItem.getDouble(JSON_VOTE_AVERAGE);
            String moviePoster = movieItem.getString(JSON_POSTER_PATH);
            String movieOrgTitle = movieItem.getString(JSON_ORIGINAL_TITLE);
            String movieOverview = movieItem.getString(JSON_OVERVIEW);
            String movieRelease = movieItem.getString(JSON_RELEASE_DATE);

            //Movie Constructor: String mvPoster, String mvTitle, String mvSynopsis,
            // long mvRating, String mvRelease
            movieList.add(new Movie(moviePoster, movieOrgTitle, movieOverview, movieVote,
                    movieRelease));
        }

        return movieList;
    }
}

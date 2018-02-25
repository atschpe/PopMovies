package com.example.android.popmovies.utils;

import com.example.android.popmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import com.example.android.popmovies.data.Review;
import com.example.android.popmovies.data.Trailer;

/**
 * {@link JsonUtils} enables and handles all JSON requests to the server.
 */
public class JsonUtils {

    private static final String JSON_MESSAGE_CODE = "cod";

    //Global json tags & variables:
    private static final String JSON_ID = "id"; //int
    private static final String JSON_RESULTS = "results"; //array
    private static int movieId;
    private static JSONArray jsonResults;

    //Tags for main request
    private static final String JSON_VOTE_AVERAGE = "vote_average"; //double
    private static final String JSON_POSTER_PATH = "poster_path"; //String
    private static final String JSON_ORIGINAL_TITLE = "original_title"; //String
    private static final String JSON_OVERVIEW = "overview"; // String
    private static final String JSON_RELEASE_DATE = "release_date"; // String

    //Tags for trailer request
    private static final String JSON_TRAILER_NAME = "name";
    private static final String JSON_TRAILER_KEY = "key";

    //Tags for review request
    private static final String JSON_REVIEW_AUTHOR = "author";
    private static final String JSON_REVIEW_CONTENT = "content";

    //quick list of other (main) keys – in case of future necessity in Stage 2.
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
        if (!isErrorFree(movieJson)) {
            return null;
        }

        jsonResults = movieJson.getJSONArray(JSON_RESULTS);

        for (int i = 0; i < jsonResults.length(); i++) {

            JSONObject movieItem = jsonResults.getJSONObject(i);

            movieId = movieItem.getInt(JSON_ID);
            double movieVote = movieItem.getDouble(JSON_VOTE_AVERAGE);
            String moviePoster = movieItem.getString(JSON_POSTER_PATH);
            String movieOrgTitle = movieItem.getString(JSON_ORIGINAL_TITLE);
            String movieOverview = movieItem.getString(JSON_OVERVIEW);
            String movieRelease = movieItem.getString(JSON_RELEASE_DATE);

            movieList.add(new Movie(moviePoster, movieOrgTitle, movieOverview, movieVote,
                    movieRelease, movieId));

        }
        return movieList;
    }

    public static ArrayList<Trailer> trailerList(String JsonUrl) throws JSONException {
        ArrayList<Trailer> trailerList = new ArrayList<>();

        JSONObject trailerJson = new JSONObject(JsonUrl);

        //check for errors
        if (!isErrorFree(trailerJson)) {
            return null;
        }

        movieId = trailerJson.getInt(JSON_ID);
        jsonResults = trailerJson.getJSONArray(JSON_RESULTS);

        for (int i = 0; i < jsonResults.length(); i++) {
            JSONObject trailerItem = jsonResults.getJSONObject(i);
            String trailerName = trailerItem.getString(JSON_TRAILER_NAME);
            String trailerKey = trailerItem.getString(JSON_TRAILER_KEY);

            trailerList.add(new Trailer(movieId, trailerName, trailerKey));
        }

        return trailerList;
    }

    public static ArrayList<Review> reviewList(String JsonUrl) throws JSONException {
        ArrayList<Review> reviewList = new ArrayList<>();

        JSONObject reviewJson = new JSONObject(JsonUrl);

        //check for errors
        if (!isErrorFree(reviewJson)) {
            return null;
        }

        movieId = reviewJson.getInt(JSON_ID);
        jsonResults = reviewJson.getJSONArray(JSON_RESULTS);
        for (int i = 0; i < jsonResults.length(); i++) {
            JSONObject reviewItem = jsonResults.getJSONObject(i);

            String reviewAuthor = reviewItem.getString(JSON_REVIEW_AUTHOR);
            String reviewContent = reviewItem.getString(JSON_REVIEW_CONTENT);

            reviewList.add(new Review(movieId, reviewAuthor, reviewContent));
        }
        return reviewList;
    }

    private static boolean isErrorFree(JSONObject reviewJson) throws JSONException {
        boolean noErrors = true;
        if (reviewJson.has(JSON_MESSAGE_CODE)) {
            int errorCode = reviewJson.getInt(JSON_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK: // everything fine
                    noErrors = true;
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND: // invalid location
                default: //probably: server down
                    noErrors = false;
            }
        }
        return noErrors;
    }
}
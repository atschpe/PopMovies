package com.example.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popmovies.data.Movie;
import com.example.android.popmovies.data.MovieContract.MovieEntry;
import com.example.android.popmovies.data.PosterAdapter;
import com.example.android.popmovies.databinding.ActivityMainBinding;
import com.example.android.popmovies.utils.JsonUtils;
import com.example.android.popmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import static android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<ArrayList<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener, PosterAdapter.PosterClickHandler {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private PosterAdapter posterAdapter;
    private ActivityMainBinding mainBinding;
    private ArrayList<Movie> movieList;
    private final int LOADER_ID = 1;
    private final int FAV_LOADER_ID = 2;
    private static final String MOVIELIST_KEY = "movies";
    private static boolean preferencesUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIELIST_KEY)) {
            mainBinding.alertViewMain.alertTv.setVisibility(View.GONE);
            mainBinding.alertViewMain.progressPb.setVisibility(View.GONE);
            movieList = savedInstanceState.getParcelableArrayList(MOVIELIST_KEY);
            if (movieList != null && !movieList.isEmpty()) {
                setUpAdapter(this, movieList);
            }
        } else {
            checkNetworkToLoad(); // ensure there is internet.
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        mainBinding.alertViewMain.alertTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNetworkToLoad();
            }
        });
    }

    private void setUpAdapter(Context ctxt, ArrayList<Movie> movieList) {
        posterAdapter = new PosterAdapter(ctxt, movieList, this);
        mainBinding.rv.setAdapter(posterAdapter);
        mainBinding.rv.setLayoutManager(new GridLayoutManager(ctxt, 2));
        posterAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu); //display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_menu) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //If there is no internet display a message prompting the user to check it.
    //based on: https://stackoverflow.com/a/4009133 as pointed out in the project guidelines
    private void checkNetworkToLoad() {
        ConnectivityManager connectMan = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectMan != null;
        NetworkInfo netInfo = connectMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            getSupportLoaderManager().destroyLoader(LOADER_ID);
            mainBinding.alertViewMain.alertTv.setVisibility(View.GONE);
            if (displayFavourites()) {
                getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, favouriteCaller);
            } else {
                getSupportLoaderManager().initLoader(LOADER_ID, null, this);
            }
        } else {
            mainBinding.alertViewMain.alertTv.setVisibility(View.VISIBLE);
            mainBinding.alertViewMain.alertTv.setText(getString(R.string.no_internet));
            mainBinding.alertViewMain.progressPb.setVisibility(View.GONE);
        }
    }

    private boolean displayFavourites() { //does the user want to view their favourited movies?
        SharedPreferences sharedPref = getDefaultSharedPreferences(this);
        String sortKey = getString(R.string.display_key);
        String sortDefault = getString(R.string.popularity_display_label_default);
        String sortPreference = sharedPref.getString(sortKey, sortDefault);
        return sortPreference.equals(getString(R.string.favourite_display_label));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (preferencesUpdated) {
            movieList = null;
            if (displayFavourites()) {
                getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, favouriteCaller);
            } else {
                checkNetworkToLoad();
            }
            preferencesUpdated = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIELIST_KEY, movieList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        mainBinding.alertViewMain.alertTv.setVisibility(View.GONE);
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            private String url; //the url used to make the server call

            @Override
            protected void onStartLoading() {
                if (movieList != null) {
                    deliverResult(movieList);
                } else {
                    mainBinding.alertViewMain.progressPb.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {

                try {
                    url = NetworkUtils.getResponseFromHttpUrl(MainActivity.this,
                            NetworkUtils.SORTED_URL, 0);
                    movieList = JsonUtils.getMovieList(url);
                    return movieList;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mainBinding.alertViewMain.progressPb.setVisibility(View.GONE);

        if (data == null) { //highly unlikely, with the current options, but better safe than sorry.
            mainBinding.alertViewMain.alertTv.setText(R.string.no_data);
        } else {
            mainBinding.alertViewMain.alertTv.setVisibility(View.GONE);
            setUpAdapter(this, data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        // no action on reset required, as it is not used.
    }

    private final LoaderManager.LoaderCallbacks<Cursor> favouriteCaller = new LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            mainBinding.alertViewMain.alertTv.setVisibility(View.GONE);
            String[] projection = new String[]{
                    MovieEntry.MOVIE_POSTER,
                    MovieEntry.MOVIE_ID
            };
            return new CursorLoader(MainActivity.this, MovieEntry.CONTENT_URI, projection,
                    null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mainBinding.alertViewMain.progressPb.setVisibility(View.GONE);
            movieList = new ArrayList<>();

            if (data == null) { // no videos have been added as favourites
                mainBinding.alertViewMain.alertTv.setText(R.string.no_favourite);
            } else {
                mainBinding.alertViewMain.alertTv.setVisibility(View.GONE);
                data.moveToFirst();
                while (data.moveToNext()) {
                    String moviePoster = data.getString(data.getColumnIndex(MovieEntry.MOVIE_POSTER));
                    int movieId = data.getInt(data.getColumnIndex(MovieEntry.MOVIE_ID));
                    movieList.add(new Movie(moviePoster, movieId));
                }
                setUpAdapter(MainActivity.this, movieList);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // no action on reset required, as it is not used.
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        preferencesUpdated = true;
    }

    @Override
    public void onClick(Movie selectedMovie) {
        Intent openDetailActivity = new Intent(this, DetailActivity.class);
        openDetailActivity.putExtra(DetailActivity.SELECTED_KEY, selectedMovie);
        startActivity(openDetailActivity);
    }
}
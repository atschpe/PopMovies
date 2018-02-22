package com.example.android.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popmovies.data.Movie;
import com.example.android.popmovies.data.PosterAdapter;
import com.example.android.popmovies.databinding.ActivityMainBinding;
import com.example.android.popmovies.utils.JsonUtils;
import com.example.android.popmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<ArrayList<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener, PosterAdapter.PosterClickHandler {

    String LOG_TAG = MainActivity.class.getSimpleName();

    private PosterAdapter posterAdapter;
    private ActivityMainBinding mainBinding;
    public ArrayList<Movie> movieList;
    private int LOADER_ID = 1;
    static String INSTANTESTATE_KEY = "movies";
    static boolean preferencesUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        checkNetwork(); // ensure there is internet.

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANTESTATE_KEY)) {
            mainBinding.alertTv.setVisibility(View.GONE);
            mainBinding.progressPb.setVisibility(View.GONE);
            movieList = savedInstanceState.getParcelableArrayList(INSTANTESTATE_KEY);
            setUpAdapter(this, movieList);
        } else {
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
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
    private void checkNetwork() {
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        ConnectivityManager connectMan = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectMan != null;
        NetworkInfo netInfo = connectMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            mainBinding.alertTv.setVisibility(View.VISIBLE);
            mainBinding.alertTv.setText(getString(R.string.no_internet));
            mainBinding.progressPb.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkNetwork();
        if (preferencesUpdated) {
            movieList = null;
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
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
        outState.putParcelableArrayList(INSTANTESTATE_KEY, movieList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        mainBinding.alertTv.setVisibility(View.GONE);
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            private String url; //the url used to make the server call

            @Override
            protected void onStartLoading() {
                if (movieList != null) {
                    deliverResult(movieList);
                } else {
                    mainBinding.progressPb.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {

                try {
                    url = NetworkUtils.getResponseFromHttpUrl(MainActivity.this);
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
        mainBinding.progressPb.setVisibility(View.GONE);

        if (data == null) { //highly unlikely, with the current options, but better safe than sorry.
            mainBinding.alertTv.setText(R.string.no_data);
        } else {
            mainBinding.alertTv.setVisibility(View.GONE);
            setUpAdapter(this, data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        preferencesUpdated = true;
    }

    @Override
    public void onClick(Movie selectedMovie) {
        Intent openDetailActivity = new Intent(this, DetailActivity.class);
        openDetailActivity.putExtra(DetailActivity.INTENT_KEY, selectedMovie);
        startActivity(openDetailActivity);
    }
}
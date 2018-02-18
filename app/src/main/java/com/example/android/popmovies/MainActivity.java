package com.example.android.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.View;
import android.widget.AdapterView;

import com.example.android.popmovies.data.Movie;
import com.example.android.popmovies.data.MovieAdapter;
import com.example.android.popmovies.databinding.ActivityMainBinding;
import com.example.android.popmovies.utils.JsonUtils;
import com.example.android.popmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Movie>> {

    private MovieAdapter movieAdapter;
    private ActivityMainBinding mainBinding;
    public ArrayList<Movie> movieList;
    private int LOADER_ID = 1;
    static String INSTANTESTATE_KEY = "movies";
    static String INTENT_KEY = "selectedMovie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey(INSTANTESTATE_KEY)) {
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            movieList = savedInstanceState.getParcelableArrayList(INSTANTESTATE_KEY);
        }
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mainBinding.gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);
                Intent openDetailActivity = new Intent(MainActivity.this, DetailActivity.class);
                openDetailActivity.putExtra(INTENT_KEY, movie);
                startActivity(openDetailActivity);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(INSTANTESTATE_KEY, movieList);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
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

        if (data == null) {
            mainBinding.alertTv.setText(R.string.no_data);
        } else {
            mainBinding.alertTv.setVisibility(View.GONE);
            movieAdapter = new MovieAdapter(this, data);
            mainBinding.gv.setAdapter(movieAdapter);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<Movie>> loader) {

    }

}

package com.example.android.popmovies;

import android.databinding.DataBindingUtil;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.View;

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
    private ArrayList<Movie> movieList;
    private int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

//        movieList = new ArrayList<Movie>();
//        movieList.add(new Movie("/q0R4crx2SehcEEQEkYObktdeFy.jpg", 211672));
//        movieList.add(new Movie("/qey0tdcOp9kCDdEZuJ87yE3crSe.jpg", 254128));
//        movieList.add(new Movie("/tWqifoYuwLETmmasnGHO7xBjEtt.jpg", 321612));
//        movieList.add(new Movie("/47pLZ1gr63WaciDfHCpmoiXJlVr.jpg", 460793));
//        movieList.add(new Movie("/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg", 335984));
//        movieList.add(new Movie("/xOfdQHNF9TlrdujyAjiKfUhxSXy.jpg", 335777));

        getSupportLoaderManager().initLoader(LOADER_ID,null, this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
    public void onLoadFinished (Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
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

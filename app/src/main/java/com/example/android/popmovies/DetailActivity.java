package com.example.android.popmovies;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.popmovies.data.Movie;
import com.example.android.popmovies.data.MovieContract.MovieEntry;
import com.example.android.popmovies.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String INTENT_KEY = "selectedMovie";
    static String LOG_TAG = DetailActivity.class.getSimpleName();

    private ActivityDetailBinding detailBinding;
    private Movie movie;
    private boolean isFavourited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent movieIntent = getIntent();
        if (movieIntent != null && movieIntent.hasExtra(INTENT_KEY)) {
            movie = movieIntent.getParcelableExtra(INTENT_KEY);
        }

        String poster_url = getString(R.string.image_base_url) + movie.getMvPoster();
        Picasso.with(this).load(poster_url).into(detailBinding.posterThumbnailIv);

        //Populate the text views.
        detailBinding.releaseDate.setText(movie.getMvRelease());
        detailBinding.synopsisTv.setText(movie.getMvSynopsis());
        detailBinding.userRating.setText(String.valueOf(movie.getMvRating()));

        setTitle(movie.getMvTitle());

        detailBinding.favouriteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setSelected(!v.isSelected());

                if (v.isSelected()) {
                    detailBinding.favouriteBt.setColorFilter(ContextCompat
                            .getColor(DetailActivity.this, R.color.colorAccent));
                    Uri addedFav = getContentResolver().insert(MovieEntry.CONTENT_URI, getMovieValues());
                    if (addedFav == null) { //error occurred during saving
                        Toast.makeText(DetailActivity.this, R.string.error_adding_fav,
                                Toast.LENGTH_SHORT).show();
                    } else { //success
                        Toast.makeText(DetailActivity.this, R.string.fav_added,
                                Toast.LENGTH_SHORT).show();
                        isFavourited = true;
                    }
                } else {
                    detailBinding.favouriteBt.setColorFilter(ContextCompat
                            .getColor(DetailActivity.this, android.R.color.darker_gray));
                    String selection = MovieEntry.MOVIE_ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(movie.getMvId())};
                    int favDeleted = getContentResolver().delete(MovieEntry.CONTENT_URI, selection, selectionArgs);
                    if (favDeleted == 0) { // error occurred during deletion
                        Toast.makeText(DetailActivity.this, R.string.error_removing_fav,
                                Toast.LENGTH_SHORT).show();
                    } else { // success
                        Toast.makeText(DetailActivity.this, R.string.fav_removed,
                                Toast.LENGTH_SHORT).show();
                        isFavourited = false;
                    }
                }
            }
        });
    }

    ContentValues getMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.MOVIE_ID, movie.getMvId());
        movieValues.put(MovieEntry.MOVIE_ORG_TITLE, movie.getMvTitle());
        movieValues.put(MovieEntry.MOVIE_POSTER, movie.getMvPoster());
        movieValues.put(MovieEntry.MOVIE_RATING, movie.getMvRating());
        movieValues.put(MovieEntry.MOVIE_RELEASE, movie.getMvRelease());
        movieValues.put(MovieEntry.MOVIE_SYNOPSIS, movie.getMvSynopsis());

        return movieValues;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {MovieEntry.MOVIE_ID};

        return new CursorLoader(this, MovieEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 1) {
            isFavourited = true;
        } else {
            isFavourited = false;
        }
        data.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

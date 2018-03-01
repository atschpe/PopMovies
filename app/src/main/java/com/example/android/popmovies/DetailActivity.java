package com.example.android.popmovies;

import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.popmovies.data.Movie;
import com.example.android.popmovies.data.MovieContract.MovieEntry;
import com.example.android.popmovies.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
        //implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String INTENT_KEY = "selectedMovie";
    static String LOG_TAG = DetailActivity.class.getSimpleName();

    private ActivityDetailBinding detailBinding;
    private Movie selectedMovie;
    private boolean isFavourited;
    private Menu menu;

    private final int FAV_LOADER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent movieIntent = getIntent();
        if (movieIntent != null && movieIntent.hasExtra(INTENT_KEY)) {
            selectedMovie = movieIntent.getParcelableExtra(INTENT_KEY);
        }

        String poster_url = getString(R.string.image_base_url) + selectedMovie.getMvPoster();
        Picasso.with(this).load(poster_url).into(detailBinding.posterThumbnailIv);

        //Populate the text views.
        detailBinding.releaseDate.setText(selectedMovie.getMvRelease());
        detailBinding.synopsisTv.setText(selectedMovie.getMvSynopsis());
        detailBinding.userRating.setText(String.valueOf(selectedMovie.getMvRating()));

        setTitle(selectedMovie.getMvTitle());

       // getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, this);

        getFavourited();
    }

    private void getFavourited() {
        String selection = MovieEntry.MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(selectedMovie.getMvId())};
        Cursor csr = getContentResolver().query(MovieEntry.CONTENT_URI, null, selection,
                selectionArgs, null);
        if (csr != null && csr.getCount() > 1) {
            isFavourited = true;
            csr.close();
        } else {
            isFavourited = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.detail_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.detail_fav_bt:
                if (isFavourited) {
                    isFavourited = false;
                    tintMenuIcon(this, item, android.R.color.black);
                    String selection = MovieEntry.MOVIE_ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(selectedMovie.getMvId())};
                    int favDeleted = getContentResolver()
                            .delete(MovieEntry.CONTENT_URI, selection, selectionArgs);
                    if (favDeleted == 0) { // error occurred during deletion
                        Toast.makeText(this, R.string.error_removing_fav,
                                Toast.LENGTH_SHORT).show();
                    } else { // success
                        Toast.makeText(this, R.string.fav_removed,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isFavourited = true;
                    tintMenuIcon(this, item, R.color.colorAccent);
                    Uri addedFav = getContentResolver()
                            .insert(MovieEntry.CONTENT_URI, getMovieValues());
                    if (addedFav == null) { //error occurred during saving
                        Toast.makeText(this, R.string.error_adding_fav,
                                Toast.LENGTH_SHORT).show();
                    } else { //success
                        Toast.makeText(this, R.string.fav_added,
                                Toast.LENGTH_SHORT).show();
                        isFavourited = true;
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }

    // https://futurestud.io/tutorials/android-quick-tips-8-how-to-dynamically-tint-actionbar-menu-icons
    public static void tintMenuIcon(Context ctxt, MenuItem item, @ColorRes int color) {
        Drawable icon = item.getIcon();
        Drawable wrapper = DrawableCompat.wrap(icon);
        DrawableCompat.setTint(wrapper, ctxt.getResources().getColor(color));

        item.setIcon(wrapper);
    }

    ContentValues getMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.MOVIE_ID, selectedMovie.getMvId());
        movieValues.put(MovieEntry.MOVIE_ORG_TITLE, selectedMovie.getMvTitle());
        movieValues.put(MovieEntry.MOVIE_POSTER, selectedMovie.getMvPoster());
        movieValues.put(MovieEntry.MOVIE_RATING, selectedMovie.getMvRating());
        movieValues.put(MovieEntry.MOVIE_RELEASE, selectedMovie.getMvRelease());
        movieValues.put(MovieEntry.MOVIE_SYNOPSIS, selectedMovie.getMvSynopsis());

        return movieValues;
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String selection = MovieEntry.MOVIE_ID + "=?";
//        String[] selectionArgs = new String[]{String.valueOf(selectedMovie.getMvId())};
//        return new CursorLoader(this, MovieEntry.CONTENT_URI, null,
//                selection, selectionArgs, null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        if (data != null && data.getCount() > 1) {
//            isFavourited = true;
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//    }
}

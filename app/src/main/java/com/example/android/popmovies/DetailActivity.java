package com.example.android.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.popmovies.data.Movie;
import com.example.android.popmovies.data.MovieContract.MovieEntry;
import com.example.android.popmovies.data.Review;
import com.example.android.popmovies.data.ReviewAdapter;
import com.example.android.popmovies.data.Trailer;
import com.example.android.popmovies.data.TrailerAdapter;
import com.example.android.popmovies.databinding.ActivityDetailBinding;
import com.example.android.popmovies.utils.JsonUtils;
import com.example.android.popmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    //Keys for parcels
    public static final String SELECTED_KEY = "selectedMovie";
    private static final String REVIEWLIST_KEY = "review";
    private static final String TRAILERLIST_KEY = "trailer";

    private ActivityDetailBinding detailBinding;
    private Movie selectedMovie;
    private boolean isFavourited;

    //adapters and data for the reviews and trailers.
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private final static int REVIEW_LOADER_ID = 2;
    private final static int TRAILER_LOADER_ID = 3;
    private ArrayList<Review> reviewList;
    private ArrayList<Trailer> trailerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(REVIEWLIST_KEY)) {
                detailBinding.alertViewReview.alertTv.setVisibility(View.GONE);
                detailBinding.alertViewReview.progressPb.setVisibility(View.GONE);
                reviewList = savedInstanceState.getParcelableArrayList(REVIEWLIST_KEY);
                if (reviewList != null && !reviewList.isEmpty()) {
                    setupReviewAdapter(this, reviewList);
                }
            }
            if (savedInstanceState.containsKey(TRAILERLIST_KEY)) {
                detailBinding.alertViewTrailer.alertTv.setVisibility(View.GONE);
                detailBinding.alertViewTrailer.progressPb.setVisibility(View.GONE);
                trailerList = savedInstanceState.getParcelableArrayList(TRAILERLIST_KEY);
                if (trailerList != null && !trailerList.isEmpty()) {
                    setupTrailerAdapter(this, trailerList);
                    Log.v(LOG_TAG, "trailerList: " + trailerList.size());
                }
            }
        } else {
            checkNetworkToLoad();
        }

        Intent movieIntent = getIntent();
        if (movieIntent != null && movieIntent.hasExtra(SELECTED_KEY)) {
            selectedMovie = movieIntent.getParcelableExtra(SELECTED_KEY);
        }

        getFavourited();

        String poster_url = getString(R.string.image_base_url) + selectedMovie.getMvPoster();
        Picasso.with(this).load(poster_url).into(detailBinding.posterThumbnailIv);

        //Populate the text views.
        detailBinding.releaseDate.setText(selectedMovie.getMvRelease());
        detailBinding.synopsisTv.setText(selectedMovie.getMvSynopsis());
        detailBinding.userRating.setText(String.valueOf(selectedMovie.getMvRating()));

        setTitle(selectedMovie.getMvTitle());

        detailBinding.alertViewReview.alertTv.setOnClickListener(new Reclicker());
        detailBinding.alertViewTrailer.alertTv.setOnClickListener(new Reclicker());
    }

    private class Reclicker implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            checkNetworkToLoad();
        }
    }


    private void checkNetworkToLoad() {
        ConnectivityManager connectMan = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectMan != null;
        NetworkInfo netInfo = connectMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            getSupportLoaderManager().destroyLoader(REVIEW_LOADER_ID);
            getSupportLoaderManager().destroyLoader(TRAILER_LOADER_ID);
            detailBinding.alertViewReview.alertTv.setVisibility(View.GONE);
            detailBinding.alertViewTrailer.alertTv.setVisibility(View.GONE);
            getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, reviewCaller);
            getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, trailerCaller);
        } else {
            detailBinding.alertViewReview.alertTv.setVisibility(View.VISIBLE);
            detailBinding.alertViewTrailer.alertTv.setVisibility(View.VISIBLE);
            detailBinding.alertViewReview.alertTv.setText(getString(R.string.no_internet));
            detailBinding.alertViewTrailer.alertTv.setText(getString(R.string.no_internet));
            detailBinding.alertViewTrailer.progressPb.setVisibility(View.GONE);
            detailBinding.alertViewReview.progressPb.setVisibility(View.GONE);
        }
    }

    private void getFavourited() {
        String[] projection = new String[]{MovieEntry.MOVIE_ID};
        String selection = MovieEntry.MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(selectedMovie.getMvId())};
        Cursor csr = getContentResolver().query(MovieEntry.CONTENT_URI, projection, selection,
                selectionArgs, null);
        if (csr != null && csr.getCount() > 0) {
            isFavourited = true;
            csr.close();
        } else {
            isFavourited = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getFavourited();
        getMenuInflater().inflate(R.menu.detail_menu, menu);

        MenuItem favouriteIcon = menu.findItem(R.id.detail_fav_bt);
        if (isFavourited) {
            tintFavButton(this, favouriteIcon, R.color.colorAccent);
        } else {
            tintFavButton(this, favouriteIcon, android.R.color.black);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.detail_fav_bt:
                if (!isFavourited) {
                    tintFavButton(this, item, R.color.colorAccent);
                    Uri addedFav = getContentResolver()
                            .insert(MovieEntry.CONTENT_URI, getMovieValues());
                    if (addedFav == null) { //error occurred during saving
                        Toast.makeText(this, R.string.error_adding_fav,
                                Toast.LENGTH_SHORT).show();
                    } else { //success
                        Toast.makeText(this, R.string.fav_added,
                                Toast.LENGTH_SHORT).show();
                        isFavourited = false;
                    }
                } else {
                    tintFavButton(this, item, android.R.color.black);
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
                    isFavourited = true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    // Tint the favourited star to show whether selected or not. Based on stackoverflow:
    // https://futurestud.io/tutorials/android-quick-tips-8-how-to-dynamically-tint-actionbar-menu-icons
    private static void tintFavButton(Context ctxt, MenuItem item, @ColorRes int color) {
        Drawable icon = item.getIcon();
        Drawable wrapper = DrawableCompat.wrap(icon);
        DrawableCompat.setTint(wrapper, ctxt.getResources().getColor(color));

        item.setIcon(wrapper);
    }

    private ContentValues getMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.MOVIE_ID, selectedMovie.getMvId());
        movieValues.put(MovieEntry.MOVIE_ORG_TITLE, selectedMovie.getMvTitle());
        movieValues.put(MovieEntry.MOVIE_POSTER, selectedMovie.getMvPoster());
        movieValues.put(MovieEntry.MOVIE_RATING, selectedMovie.getMvRating());
        movieValues.put(MovieEntry.MOVIE_RELEASE, selectedMovie.getMvRelease());
        movieValues.put(MovieEntry.MOVIE_SYNOPSIS, selectedMovie.getMvSynopsis());

        return movieValues;
    }

    private void setupReviewAdapter(Context ctxt, ArrayList<Review> reviewList) {
        reviewAdapter = new ReviewAdapter(this, reviewList);
        detailBinding.reviewRv.setAdapter(reviewAdapter);
        detailBinding.reviewRv.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter.notifyDataSetChanged();
    }

    private void setupTrailerAdapter(Context ctxt, ArrayList<Trailer> trailerList) {
        trailerAdapter = new TrailerAdapter(this, trailerList);
        detailBinding.trailerRv.setAdapter(trailerAdapter);
        detailBinding.trailerRv.setLayoutManager(new GridLayoutManager(ctxt, 4));
        trailerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(REVIEWLIST_KEY, reviewList);
        outState.putParcelableArrayList(TRAILERLIST_KEY, trailerList);
        super.onSaveInstanceState(outState);
    }

    private final LoaderManager.LoaderCallbacks<ArrayList<Review>> reviewCaller =
            new LoaderManager.LoaderCallbacks<ArrayList<Review>>() {
                @Override
                public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
                    detailBinding.alertViewReview.alertTv.setVisibility(View.GONE);
                    return new AsyncTaskLoader<ArrayList<Review>>(DetailActivity.this) {

                        private String url; //the url used to make the server call

                        @Override
                        protected void onStartLoading() {
                            if (reviewList != null) {
                                deliverResult(reviewList);
                            } else {
                                detailBinding.alertViewReview.progressPb.setVisibility(View.VISIBLE);
                                forceLoad();
                            }
                        }

                        @Override
                        public ArrayList<Review> loadInBackground() {
                            try {
                                url = NetworkUtils.getResponseFromHttpUrl(DetailActivity.this,
                                        NetworkUtils.REVIEW_URL, selectedMovie.getMvId());
                                reviewList = JsonUtils.getReviewList(url);
                                return reviewList;
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
                public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
                    detailBinding.alertViewReview.progressPb.setVisibility(View.GONE);
                    if (data == null) { //in case there are no reviews to display
                        detailBinding.alertViewReview.alertTv.setText(R.string.alert_no_review);
                    } else {
                        detailBinding.alertViewReview.alertTv.setVisibility(View.GONE);
                        setupReviewAdapter(DetailActivity.this, data);
                    }
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<Review>> loader) {
                    // no action on reset required, as it is not used.
                }
            };

    private final LoaderManager.LoaderCallbacks<ArrayList<Trailer>> trailerCaller =
            new LoaderManager.LoaderCallbacks<ArrayList<Trailer>>() {

                @Override
                public Loader<ArrayList<Trailer>> onCreateLoader(int id, Bundle args) {
                    detailBinding.alertViewTrailer.alertTv.setVisibility(View.GONE);

                    return new AsyncTaskLoader<ArrayList<Trailer>>(DetailActivity.this) {

                        private String url; //the url used to make the server call

                        @Override
                        protected void onStartLoading() {
                            if (trailerList != null) {
                                deliverResult(trailerList);
                            } else {
                                detailBinding.alertViewTrailer.progressPb.setVisibility(View.VISIBLE);
                                forceLoad();
                            }
                        }

                        @Override
                        public ArrayList<Trailer> loadInBackground() {
                            try {
                                url = NetworkUtils.getResponseFromHttpUrl(DetailActivity.this,
                                        NetworkUtils.TRAILER_URL, selectedMovie.getMvId());
                                trailerList = JsonUtils.getTrailerList(url);
                                return trailerList;
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<Trailer>> loader, ArrayList<Trailer> data) {
                    detailBinding.alertViewTrailer.progressPb.setVisibility(View.GONE);
                    if (data == null) { //in case there are not trailers to display
                        detailBinding.alertViewTrailer.alertTv.setText(R.string.alert_no_trailer);
                    } else {
                        detailBinding.alertViewTrailer.alertTv.setVisibility(View.GONE);
                        setupTrailerAdapter(DetailActivity.this, data);
                    }
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<Trailer>> loader) {
                    // no action on reset required, as it is not used.
                }
            };
}
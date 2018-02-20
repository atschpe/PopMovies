package com.example.android.popmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popmovies.data.Movie;
import com.example.android.popmovies.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    static String LOG_TAG = DetailActivity.class.getSimpleName();

    ActivityDetailBinding detailBinding;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent movieIntent = getIntent();
        if (movieIntent != null && movieIntent.hasExtra(MainActivity.INTENT_KEY)) {
            movie = movieIntent.getParcelableExtra(MainActivity.INTENT_KEY);
        }

        String poster_url = getString(R.string.image_base_url) + movie.getMvPoster();
        Picasso.with(this).load(poster_url).into(detailBinding.posterThumbnailIv);

        //Populate the text views.
        detailBinding.releaseDate.setText(movie.getMvRelease());
        detailBinding.synopsisTv.setText(movie.getMvSynopsis());
        detailBinding.userRating.setText(String.valueOf(movie.getMvRating()));

        setTitle(movie.getMvTitle());
    }
}

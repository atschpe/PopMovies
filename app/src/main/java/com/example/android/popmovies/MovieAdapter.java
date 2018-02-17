package com.example.android.popmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link MovieAdapter}
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

   /** Constructor for the {@link MovieAdapter} to display the data
     *
     * @param ctxt wherein the adapter operates.
     * @param movieList is the list of objects to be displayed.
     */
    public MovieAdapter(Context ctxt, ArrayList<Movie> movieList) {
        super(ctxt, 0, movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie currentMovie = getItem(position);
        Context ctxt = getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(ctxt).inflate(R.layout.activity_poster, parent, false);
        }

        ImageView poster = convertView.findViewById(R.id.movie_poster);
        String poster_url = null;
        if (currentMovie != null) {
            poster_url = ctxt.getString(R.string.image_base_url) + currentMovie.getMvPoster();
        }
        Picasso.with(ctxt).load(poster_url).into(poster);

        return convertView;
    }
}
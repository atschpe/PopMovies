package com.example.android.popmovies.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link PosterAdapter} is a {@link RecyclerView} to populate a Gridlayout of movieList-posters in the
 * MainActivity.
 */
public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {

    private static final String LOG_TAG = PosterAdapter.class.getSimpleName();

    private final Context ctxt;
    private final ArrayList<Movie> movieList;


    /**
     * Constructor
     *
     * @param ctxt          is the context within which the adapter is called.
     * @param movieList     is the collection of movie posters.
     * @param posterClicker is the clickHandler.
     */
    public PosterAdapter(Context ctxt, ArrayList<Movie> movieList, PosterClickHandler posterClicker) {
        this.ctxt = ctxt;
        this.movieList = movieList;
        this.posterClicker = posterClicker;
    }

    final private PosterClickHandler posterClicker;

    public interface PosterClickHandler {
        void onClick(Movie movie);
    }

    /**
     * create view for data.
     *
     * @param parent   of the view.
     * @param viewType the type of view to be shown (not needed here)
     * @return the new constructed view.
     */
    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_poster,
                parent, false);
        root.setFocusable(true);
        return new PosterViewHolder(root);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        Movie currentMovie = this.movieList.get(position);

        String poster_url;
        if (currentMovie != null) {
            poster_url = ctxt.getString(R.string.image_base_url) + currentMovie.getMvPoster();
            Picasso.with(ctxt).load(poster_url).into(holder.posterView);
        }
    }

    @Override
    public int getItemCount() {
        if (movieList == null) {
            return 0;
        } else {
            return movieList.size();
        }

    }

    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView posterView;

        public PosterViewHolder(View itemView) {
            super(itemView);
            posterView = itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie currentMovie = movieList.get(adapterPosition);
            posterClicker.onClick(currentMovie);
        }
    }
}
package com.example.android.popmovies.data;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.popmovies.R;

import java.util.ArrayList;

/**
 * {@link TrailerAdapter} is a {@link RecyclerView} to populate a list of trailers in the
 * DetailActivity.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private final Context ctxt;
    private final ArrayList<Trailer> trailerList;

    public TrailerAdapter(Context ctxt, ArrayList<Trailer> trailerList) {
        this.ctxt = ctxt;
        this.trailerList = trailerList;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_trailer,
                parent, false);
        root.setFocusable(true);
        return new TrailerViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final TrailerViewHolder holder, int position) {
        final Trailer currentTrailer = this.trailerList.get(position);

        holder.trailerPlayBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_url = ctxt.getString(R.string.youtube_base_url)
                        + currentTrailer.getTrailKey();
                watchYoutubeTrailer(ctxt, video_url);
            }
        });

        holder.trailerTitleTv.setText(currentTrailer.getTrailName());
    }

    // Method to open video – if there is no youtube app on the mobile, then in the browser.
    // See: https://stackoverflow.com/a/12439378
    private void watchYoutubeTrailer(Context ctxt, String video_url) {
        Intent openInApp = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video_url));
        Intent openInWeb = new Intent(Intent.ACTION_VIEW,
                Uri.parse(ctxt.getString(R.string.youtube_base_url) + video_url));
        try {
            ctxt.startActivity(openInApp);
        } catch (ActivityNotFoundException ex) {
            ctxt.startActivity(openInWeb);
        }
    }

    @Override
    public int getItemCount() {
        if (trailerList == null) {
            return 0;
        } else {
            return trailerList.size();
        }

    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        final ImageButton trailerPlayBt;
        final TextView trailerTitleTv;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            trailerPlayBt = itemView.findViewById(R.id.trailer_play_bt);
            trailerTitleTv = itemView.findViewById(R.id.trailer_title_tv);
        }
    }
}

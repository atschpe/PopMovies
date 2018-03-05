package com.example.android.popmovies.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies.R;

import java.util.ArrayList;

/**
 * {@link ReviewAdapter} is a {@link RecyclerView} to populate a list of of movie reviews in the
 * DetailActivity.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private final Context ctxt;
    private final ArrayList<Review> reviewList;


    /**
     * Constructor
     *
     * @param ctxt       is the context within which the adapter is called.
     * @param reviewList is the collection of movie posters.
     */
    public ReviewAdapter(Context ctxt, ArrayList<Review> reviewList) {
        this.ctxt = ctxt;
        this.reviewList = reviewList;
    }

    /**
     * create view for data.
     *
     * @param parent   of the view.
     * @param viewType the type of view to be shown (not needed here)
     * @return the new constructed view.
     */
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_review,
                parent, false);
        root.setFocusable(true);
        return new ReviewViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review currentReview = this.reviewList.get(position);

        holder.authorView.setText(currentReview.getReviewAuthor());
        holder.reviewContentView.setText(currentReview.getReviewContent());
    }

    @Override
    public int getItemCount() {
        if (reviewList == null) {
            return 0;
        } else {
            return reviewList.size();
        }
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        final TextView authorView;
        final TextView reviewContentView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            authorView = itemView.findViewById(R.id.review_author_tv);
            reviewContentView = itemView.findViewById(R.id.review_content_tv);
        }
    }
}
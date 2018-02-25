package com.example.android.popmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HildePols on 24.02.18.
 */

public class Review implements Parcelable {

    private int mvId;
    private String reviewAuthor;
    private String reviewContent;

    public Review(int    mvId, String reviewAuthor, String reviewContent) {
        this.mvId = mvId;
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    protected Review(Parcel in) {
        mvId = in.readInt();
        reviewAuthor = in.readString();
        reviewContent = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mvId);
        dest.writeString(reviewAuthor);
        dest.writeString(reviewContent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public int getMvId() {
        return mvId;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }
}
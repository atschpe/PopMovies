package com.example.android.popmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@ink Review} is an object containing all information needed for the reviews to be displayed.
 */

public class Review implements Parcelable {

    private int mvId;
    private String reviewAuthor;
    private String reviewContent;

    /** Constructor
     *
     * @param mvId is the id of the movie, so as to keep track of the movie across the objects.
     * @param reviewAuthor is the writer of the review
     * @param reviewContent is the review itself.
     */
    public Review(int mvId, String reviewAuthor, String reviewContent) {
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
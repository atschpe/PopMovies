package com.example.android.popmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@link Movie} contains all elements needed to display information provided by the api.
 */
public class Movie implements Parcelable {

    private String mvPoster; //  e.g. "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"
    private String mvTitle; // e.g. "Minions"
    private long mvId; // e.g. 123456
    private String mvSynopsis; //e.g. "A long(er) running text describing the storyline of the film."
    private double mvRating; // e.g. 6.4
    private String mvRelease; // e.g. 2010-01-01

    //used by MainActivity
    public Movie(String mvPoster) {
        this.mvPoster = mvPoster;
    }

    //used by DetailActivity
    public Movie(String mvPoster, String mvTitle, long mvId, String mvSynopsis, double mvRating,
                 String mvRelease) {
        this.mvPoster = mvPoster;
        this.mvTitle = mvTitle;
        this.mvId = mvId;
        this.mvSynopsis = mvSynopsis;
        this.mvRating = mvRating;
        this.mvRelease = mvRelease;
    }

    private Movie(Parcel in) {
        mvPoster = in.readString();
        mvTitle = in.readString();
        mvId = in.readLong();
        mvSynopsis = in.readString();
        mvRating = in.readDouble();
        mvRelease = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mvPoster);
        dest.writeString(mvTitle);
        dest.writeLong(mvId);
        dest.writeString(mvSynopsis);
        dest.writeDouble(mvRating);
        dest.writeString(mvRelease);
    }

    public String getMvPoster() {
        return mvPoster;
    }

    public void setMvPoster(String mvPoster) {
        this.mvPoster = mvPoster;
    }

    public String getMvTitle() {
        return mvTitle;
    }

    public void setMvTitle(String mvTitle) {
        this.mvTitle = mvTitle;
    }

    public long getMvId() {
        return mvId;
    }

    public void setMvId(long mvId) {
        this.mvId = mvId;
    }

    public String getMvSynopsis() {
        return mvSynopsis;
    }

    public void setMvSynopsis(String mvSynopsis) {
        this.mvSynopsis = mvSynopsis;
    }

    public double getMvRating() {
        return mvRating;
    }

    public void setMvRating(long mvRating) {
        this.mvRating = mvRating;
    }

    public String getMvRelease() {
        return mvRelease;
    }

    public void setMvRelease(String mvRelease) {
        this.mvRelease = mvRelease;
    }
}

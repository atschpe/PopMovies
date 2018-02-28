package com.example.android.popmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@link Movie} contains all elements needed to display information provided by the api.
 */
public class Movie implements Parcelable {

    private String mvPoster; //  e.g. "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"
    private String mvTitle; // e.g. "Minions"
    private String mvSynopsis; //e.g. "A long(er) running text describing the storyline of the film."
    private double mvRating; // e.g. 6.4
    private String mvRelease; // e.g. 2010-01-01
    private int mvId; // e.g. 123456

    //used by DetailActivity
    public Movie(String mvPoster, String mvTitle, String mvSynopsis, double mvRating,
                 String mvRelease, int mvId) {
        this.mvPoster = mvPoster;
        this.mvTitle = mvTitle;
        this.mvSynopsis = mvSynopsis;
        this.mvRating = mvRating;
        this.mvRelease = mvRelease;
        this.mvId = mvId;
    }

    private Movie(Parcel in) {
        mvPoster = in.readString();
        mvTitle = in.readString();
        mvSynopsis = in.readString();
        mvRating = in.readDouble();
        mvRelease = in.readString();
        mvId = in.readInt();
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
        dest.writeString(mvSynopsis);
        dest.writeDouble(mvRating);
        dest.writeString(mvRelease);
        dest.writeInt(mvId);
    }

    public String getMvPoster() {
        return mvPoster;
    }

    public String getMvTitle() {
        return mvTitle;
    }

    public String getMvSynopsis() {
        return mvSynopsis;
    }

    public double getMvRating() {
        return mvRating;
    }

    public String getMvRelease() {
        return mvRelease;
    }

    public int getMvId() {
        return mvId;
    }
}

package com.example.android.popmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@link Trailer} holds all information needed for the trailer videos.
 */
public class Trailer implements Parcelable {

    private int mvId;
    private String trailName;
    private String trailKey;

    /** Constructor
     *
     * @param mvId is the id of the movie, so as to keep track of the movie across the objects.
     * @param trailName is the title of the trailer.
     * @param trailKey us the youtube key to access the trailer.
     */
    public Trailer(int mvId, String trailName, String trailKey) {
        this.mvId = mvId;
        this.trailName = trailName;
        this.trailKey = trailKey;
    }

    protected Trailer(Parcel in) {
        mvId = in.readInt();
        trailName = in.readString();
        trailKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mvId);
        dest.writeString(trailName);
        dest.writeString(trailKey);
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public int getMvId() {
        return mvId;
    }

    public String getTrailName() {
        return trailName;
    }

    public String getTrailKey() {
        return trailKey;
    }
}

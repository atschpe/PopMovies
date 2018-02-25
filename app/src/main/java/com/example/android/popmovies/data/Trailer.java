package com.example.android.popmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@link Trailer} holds all information needed for the trailer videos.
 */
public class Trailer implements Parcelable {

    //https://www.youtube.com/watch?v=64-iSYVmMVY
    private int mvId;
    private String trailName;
    private String trailKey;

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(mvId);
    dest.writeString(trailName);
    dest.writeString(trailKey);
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

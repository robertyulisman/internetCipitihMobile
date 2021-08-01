package com.gadihkaratau.lamejorescancionesreik.models.song;

import android.os.Parcel;
import android.os.Parcelable;

public class SongBuffPlay implements Parcelable {

    public static final Creator<SongBuffPlay> CREATOR = new Creator<SongBuffPlay>() {
        @Override
        public SongBuffPlay createFromParcel(Parcel in) {
            return new SongBuffPlay(in);
        }

        @Override
        public SongBuffPlay[] newArray(int size) {
            return new SongBuffPlay[size];
        }
    };
    private boolean isPlaying;
    private boolean isBuffering;

    public SongBuffPlay(boolean isPlaying, boolean isBuffering) {
        this.isPlaying = isPlaying;
        this.isBuffering = isBuffering;
    }

    protected SongBuffPlay(Parcel in) {
        isPlaying = in.readByte() != 0;
        isBuffering = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isPlaying ? 1 : 0));
        dest.writeByte((byte) (isBuffering ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isBuffering() {
        return isBuffering;
    }

    public void setBuffering(boolean buffering) {
        isBuffering = buffering;
    }
}

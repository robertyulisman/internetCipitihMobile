package com.gadihkaratau.lamejorescancionesreik.models.song;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SongPlayer implements Parcelable {

    public static final Creator<SongPlayer> CREATOR = new Creator<SongPlayer>() {
        @Override
        public SongPlayer createFromParcel(Parcel in) {
            return new SongPlayer(in);
        }

        @Override
        public SongPlayer[] newArray(int size) {
            return new SongPlayer[size];
        }
    };
    private int indexSong;
    private ArrayList<Song> songArrayList;

    protected SongPlayer(Parcel in) {
        indexSong = in.readInt();
        songArrayList = in.createTypedArrayList(Song.CREATOR);
    }

    public SongPlayer(int indexSong, ArrayList<Song> songArrayList) {
        this.indexSong = indexSong;
        this.songArrayList = songArrayList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(indexSong);
        dest.writeTypedList(songArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getIndexSong() {
        return indexSong;
    }

    public void setIndexSong(int indexSong) {
        this.indexSong = indexSong;
    }

    public ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }

    public void setSongArrayList(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
    }
}



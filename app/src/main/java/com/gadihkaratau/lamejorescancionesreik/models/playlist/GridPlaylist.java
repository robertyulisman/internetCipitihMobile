package com.gadihkaratau.lamejorescancionesreik.models.playlist;

import android.os.Parcel;
import android.os.Parcelable;

import com.gadihkaratau.lamejorescancionesreik.models.song.Song;

import java.util.ArrayList;

public class GridPlaylist implements Parcelable {

    public static final Creator<GridPlaylist> CREATOR = new Creator<GridPlaylist>() {
        @Override
        public GridPlaylist createFromParcel(Parcel in) {
            return new GridPlaylist(in);
        }

        @Override
        public GridPlaylist[] newArray(int size) {
            return new GridPlaylist[size];
        }
    };
    private NamePlaylist namePlaylist;
    private ArrayList<Song> songArrayList;

    public GridPlaylist(NamePlaylist namePlaylist, ArrayList<Song> songArrayList) {
        this.namePlaylist = namePlaylist;
        this.songArrayList = songArrayList;
    }

    protected GridPlaylist(Parcel in) {
        namePlaylist = in.readParcelable(NamePlaylist.class.getClassLoader());
        songArrayList = in.createTypedArrayList(Song.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(namePlaylist, flags);
        dest.writeTypedList(songArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public NamePlaylist getNamePlaylist() {
        return namePlaylist;
    }

    public void setNamePlaylist(NamePlaylist namePlaylist) {
        this.namePlaylist = namePlaylist;
    }

    public ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }

    public void setSongArrayList(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
    }
}

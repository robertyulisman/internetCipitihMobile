package com.gadihkaratau.lamejorescancionesreik.models.playlist;

import android.os.Parcel;
import android.os.Parcelable;

public class NamePlaylist implements Parcelable {

    public static final Creator<NamePlaylist> CREATOR = new Creator<NamePlaylist>() {
        @Override
        public NamePlaylist createFromParcel(Parcel in) {
            return new NamePlaylist(in);
        }

        @Override
        public NamePlaylist[] newArray(int size) {
            return new NamePlaylist[size];
        }
    };
    private String id;
    private String name;

    public NamePlaylist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public NamePlaylist() {
    }

    public NamePlaylist(String name) {
        this.name = name;
    }

    protected NamePlaylist(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.gadihkaratau.lamejorescancionesreik.models.song;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Song implements Parcelable {

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
    @SerializedName("_id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("artist")
    private String artist;
    @SerializedName("image")
    private String img;
    @SerializedName("url")
    private String url;
    @SerializedName("lyric")
    private String lyric;

    public Song() {
    }

    public Song(String id, String title, String artist, String img, String url, String lyric) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.img = img;
        this.url = url;
        this.lyric = lyric;
    }

    public Song(String id, String title, String artist, String img, String url) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.img = img;
        this.url = url;
    }

    protected Song(Parcel in) {
        id = in.readString();
        title = in.readString();
        artist = in.readString();
        img = in.readString();
        url = in.readString();
        lyric = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(img);
        dest.writeString(url);
        dest.writeString(lyric);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
}

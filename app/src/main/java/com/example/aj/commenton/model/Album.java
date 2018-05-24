package com.example.aj.commenton.model;

import com.google.gson.annotations.SerializedName;

public class Album {

    @SerializedName("name")
    public String name;

    @SerializedName("songs_count")
    public int songCount;

    @SerializedName("release_date")
    public String releaseDate;

    public boolean isLast;

    public String getName() {
        return name;
    }

    public Album setName(String name) {
        this.name = name;
        return this;
    }

    public int getSongCount() {
        return songCount;
    }

    public Album setSongCount(int songCount) {
        this.songCount = songCount;
        return this;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Album setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public boolean isLast() {
        return isLast;
    }

    public Album setLast(boolean last) {
        isLast = last;
        return this;
    }
}
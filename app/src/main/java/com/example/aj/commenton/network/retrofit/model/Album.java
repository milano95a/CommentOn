package com.example.aj.commenton.network.retrofit.model;

import com.google.gson.annotations.SerializedName;

public class Album {

    @SerializedName("name")
    public String name;

    @SerializedName("songs_count")
    public int songCount;

    @SerializedName("release_date")
    public String releaseDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}

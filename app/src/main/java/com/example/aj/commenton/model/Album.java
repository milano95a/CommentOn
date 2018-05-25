package com.example.aj.commenton.model;

import android.arch.persistence.room.Entity;

import com.example.aj.commenton.db.AlbumEntity;
import com.google.gson.annotations.SerializedName;

public class Album {

    @SerializedName("id")
    public int id;

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

    public Album() {
    }

    public Album(int id, String name, int songCount, String releaseDate) {
        this.id = id;
        this.name = name;
        this.songCount = songCount;
        this.releaseDate = releaseDate;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLast() {
        return isLast;
    }

    public Album setLast(boolean last) {
        isLast = last;
        return this;
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof Album)){
            return  false;
        }

        Album a = (Album)obj;

        return this.getId() == a.getId();
    }

    public AlbumEntity toAlbumEntity() {
        AlbumEntity albumEntity = new AlbumEntity();
        albumEntity.setId(this.getId());
        albumEntity.setName(this.getName());
        albumEntity.setReleaseDate(this.getReleaseDate());
        albumEntity.setSongCount(this.getSongCount());

        return albumEntity;
    }
}

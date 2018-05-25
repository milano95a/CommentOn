package com.example.aj.commenton.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.aj.commenton.model.Album;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "album_table")
public class AlbumEntity {

    @ColumnInfo(name = "id")
    public int id;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "songs_count")
    private int songCount;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AlbumEntity(int id, @NonNull String name, int songCount, String releaseDate) {
        this.id = id;
        this.name = name;
        this.songCount = songCount;
        this.releaseDate = releaseDate;
    }

    public AlbumEntity() { }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
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

    public Album toAlbumObject() {
        return new Album(this.id,this.name,this.songCount,this.releaseDate);
    }
}

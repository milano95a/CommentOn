package com.example.aj.commenton.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.aj.commenton.model.Album;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AlbumDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AlbumEntity word);

    @Query("DELETE FROM album_table")
    void deleteAll();

    @Query("SELECT * FROM album_table")
    List<AlbumEntity> getAllAlbums();

    @Query("SELECT Count(*) FROM album_table")
    int getNumberOfAlbums();
}

package com.example.aj.commenton.listener;

import com.example.aj.commenton.db.AlbumEntity;
import com.example.aj.commenton.model.Album;

import java.util.ArrayList;
import java.util.List;

public interface ReadAlbumListener {
    void onDataRetrievedFromCache(ArrayList<Album> albumList);
}

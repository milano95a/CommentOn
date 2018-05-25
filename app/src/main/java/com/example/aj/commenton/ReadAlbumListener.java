package com.example.aj.commenton;

import com.example.aj.commenton.db.AlbumEntity;

import java.util.List;

public interface ReadAlbumListener {
    void dataRetrievedFromCache(List<AlbumEntity> albumEntities);
}

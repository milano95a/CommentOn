package com.example.aj.commenton.db;

import android.app.Application;
import android.os.AsyncTask;

import com.example.aj.commenton.listener.ReadAlbumListener;
import com.example.aj.commenton.model.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumRepo {

    private AlbumDAO mAlbumDAO;
    private ReadAlbumListener mReadListener;

    public AlbumRepo(Application application, ReadAlbumListener listener){
        CommentOnDb db = CommentOnDb.databaseInstance(application);
        mAlbumDAO = db.albumDAO();
        mReadListener = listener;
    }

    public void insertAll(ArrayList<Album> arrayList){
        Album[] albums = new Album[arrayList.size()];
        albums = arrayList.toArray(albums);

        new insertAllAsyncTask(mAlbumDAO).execute(albums);
    }

    public void retrieveCache(){
        new retrieveCache(mAlbumDAO, mReadListener).execute();
    }

    private static class insertAllAsyncTask extends AsyncTask<Album[], Void, Void> {

        private AlbumDAO mAsyncTaskDao;

        insertAllAsyncTask(AlbumDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Album[]... params) {
            Album[] albums = params[0];

            for(int i = 0; i < albums.length; i++){
                mAsyncTaskDao.insert(albums[i].toAlbumEntity());
            }
            return null;
        }
    }

    private static class retrieveCache extends AsyncTask<Void, Void, ArrayList<Album>> {

        private AlbumDAO mAsyncTaskDao;
        private ReadAlbumListener mReadAlbumListener;

        retrieveCache(AlbumDAO dao, ReadAlbumListener readAlbumListener) {
            mAsyncTaskDao = dao;
            mReadAlbumListener = readAlbumListener;
        }

        @Override
        protected ArrayList<Album> doInBackground(final Void... params) {
            List<AlbumEntity> albumEntities = mAsyncTaskDao.getAllAlbums();
            ArrayList<Album> albumArrayList = new ArrayList<>();

            for(AlbumEntity a : albumEntities){
                albumArrayList.add(a.toAlbumObject());
            }
            return albumArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Album> albumList) {
            mReadAlbumListener.onDataRetrievedFromCache(albumList);
        }
    }
}

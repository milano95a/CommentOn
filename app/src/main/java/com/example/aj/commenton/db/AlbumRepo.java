package com.example.aj.commenton.db;

import android.app.Application;
import android.os.AsyncTask;

import com.example.aj.commenton.ReadAlbumListener;

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

    List<AlbumEntity> getAlbums(){
        return mAlbumDAO.getAllAlbums();
    }

    public void insert(AlbumEntity album){
        new insertAsyncTask(mAlbumDAO).execute(album);
    }

    public void insertAll(ArrayList<AlbumEntity> arrayList){
        AlbumEntity[] albumEntities = new AlbumEntity[arrayList.size()];
        albumEntities = arrayList.toArray(albumEntities);

        new insertAllAsyncTask(mAlbumDAO).execute(albumEntities);
    }

    public void retrieveCache(){
        new retrieveCache(mAlbumDAO, mReadListener).execute();
    }

    public int countNumberOfAlbums(){
        return mAlbumDAO.getNumberOfAlbums();
    }

    private static class insertAsyncTask extends AsyncTask<AlbumEntity, Void, Void> {

        private AlbumDAO mAsyncTaskDao;

        insertAsyncTask(AlbumDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AlbumEntity... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class insertAllAsyncTask extends AsyncTask<AlbumEntity[], Void, Void> {

        private AlbumDAO mAsyncTaskDao;

        insertAllAsyncTask(AlbumDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AlbumEntity[]... params) {
            AlbumEntity[] albumEntities = params[0];

            for(int i = 0; i < albumEntities.length; i++){
                mAsyncTaskDao.insert(albumEntities[i]);
            }
            return null;
        }
    }

    private static class retrieveCache extends AsyncTask<Void, Void, List<AlbumEntity>> {

        private AlbumDAO mAsyncTaskDao;
        private ReadAlbumListener mReadAlbumListener;

        retrieveCache(AlbumDAO dao, ReadAlbumListener readAlbumListener) {
            mAsyncTaskDao = dao;
            mReadAlbumListener = readAlbumListener;
        }

        @Override
        protected List<AlbumEntity> doInBackground(final Void... params) {
            return mAsyncTaskDao.getAllAlbums();
        }

        @Override
        protected void onPostExecute(List<AlbumEntity> albumEntities) {
            mReadAlbumListener.dataRetrievedFromCache(albumEntities);
        }
    }
}

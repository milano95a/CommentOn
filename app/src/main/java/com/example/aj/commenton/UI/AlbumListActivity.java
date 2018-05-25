package com.example.aj.commenton.UI;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.aj.commenton.R;
import com.example.aj.commenton.ReadAlbumListener;
import com.example.aj.commenton.adapter.AlbumAdapter;
import com.example.aj.commenton.db.AlbumDAO;
import com.example.aj.commenton.db.AlbumEntity;
import com.example.aj.commenton.db.AlbumRepo;
import com.example.aj.commenton.listener.EndlessRecyclerViewScrollListener;
import com.example.aj.commenton.model.Album;
import com.example.aj.commenton.model.Albums;
import com.example.aj.commenton.network.retrofit.RetrofitInstance;
import com.example.aj.commenton.network.retrofit.service.AndroidAcademyWebService;
import com.example.aj.commenton.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumListActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, ReadAlbumListener{

//    TODO: no internet connection case
//    todo: no data case
//    todo: fix up design of items

    private final String LOG_TAG = LoginFragment.class.getName();

    @BindView(R.id.recycler_view_album_list) RecyclerView mRecyclerView;
    @BindView(R.id.activity_home_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.txt_no_internet) TextView mTextViewNoInternet;

    private AlbumAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<Album> mAlbums;
//    private EndlessRecyclerViewScrollListener scrollListener;
    private int mLastPage = 1;
    private  int mFirstPage = 1;
    private boolean isRefreshing = true;
    private AlbumRepo mAlbumRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        init();

        loadAlbumsByPageNumber(mFirstPage);
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        loadAlbumsByPageNumber(mFirstPage);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, AuthActivity.class);
        finish();
        startActivity(intent);
    }

    private void init(){
        mAlbums = new ArrayList<>();
        mAlbumRepo = new AlbumRepo(getApplication(), this);

        mAdapter = new AlbumAdapter(this,mAlbums);
        mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnScrollListener(makeScrollListener());

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private EndlessRecyclerViewScrollListener makeScrollListener(){
        return new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mLastPage++;
                loadAlbumsByPageNumber(mLastPage);
            }
        };
    }

    private void loadAlbumsByPageNumber(int pageNumber){
        try{
            if(isRefreshing){
                showProgressBar(true);
            }

            if(!Utils.isNetworkConnected(this)){
                mTextViewNoInternet.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                populateListFromCache();
                return;

//                if(isDataCached()){
//
//                }else{
//                    mTextViewNoInternet.setVisibility(View.VISIBLE);
//                    mRecyclerView.setVisibility(View.GONE);
//                    showProgressBar(false);
//                    return;
//                }
            }else{
                mTextViewNoInternet.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            AndroidAcademyWebService webService = RetrofitInstance
                    .retrofitInstanceWithAndroidAcademyWithBasicAuth("qa@mail.com","12345678")
//                    .retrofitInstanceWithAuthenticatedClient()
                    .create(AndroidAcademyWebService.class);

            Call<Albums> albumsCall = webService.listOfAlbumsByPageNumber(pageNumber);
            AlbumCallback albumCallback = new AlbumCallback();
            albumsCall.enqueue(albumCallback);

        }
//        catch (AuthenticationClientNotFound e)
        catch (Exception e)
        {
            showProgressBar(false);
            navigateToLogin();
        }
    }

    private void populateListFromCache() {
        mAlbumRepo.retrieveCache();
    }

    private void updateList(ArrayList<Album> albumArrayList) {

        if(mLastPage > 1){
            if(mAlbums.get(mAlbums.size() -1).isLast){
                mAlbums.remove(mAlbums.size() -1);
                mAdapter.notifyItemRemoved(mAlbums.size());
            }
        }

        if(albumArrayList.size() == 0){
            return;
        }

        int currentSize = mAdapter.getItemCount();

        mAlbums.addAll(albumArrayList);
        cacheData(mAlbums);
        mAlbums.add(new Album().setLast(true));
        mAdapter.notifyItemRangeInserted(currentSize,mAlbums.size() -1);
    }

    private void displayFirstPage(ArrayList<Album> albumArrayList) {
        int numOfAlbums = mAlbums.size();
        mAlbums.clear();
        mAdapter.notifyItemRangeRemoved(0,numOfAlbums);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mLastPage++;
                loadAlbumsByPageNumber(mLastPage);
            }
        };

        mRecyclerView.addOnScrollListener(scrollListener);

        mLastPage = mFirstPage;

        int currentSize = mAdapter.getItemCount();

        mAlbums.addAll(albumArrayList);
        cacheData(mAlbums);
        mAlbums.add(new Album().setLast(true));
        mAdapter.notifyItemRangeInserted(currentSize,mAlbums.size() -1);
    }

    private void cacheData(ArrayList<Album> albumArrayList) {
        mAlbumRepo.insertAll(convertAlbumsToEntities(albumArrayList));
    }

    private ArrayList<AlbumEntity> convertAlbumsToEntities(ArrayList<Album> albumArrayList){
        ArrayList<AlbumEntity> albumEntities = new ArrayList<>();

        for(Album a : albumArrayList){
            AlbumEntity albumEntity = new AlbumEntity();
            albumEntity.setId(a.getId());
            albumEntity.setName(a.getName());
            albumEntity.setReleaseDate(a.getReleaseDate());
            albumEntity.setSongCount(a.getSongCount());

            albumEntities.add(albumEntity);
        }

        return albumEntities;
    }

    class AlbumCallback implements Callback<Albums>{

        @Override
        public void onResponse(Call<Albums> call, Response<Albums> response) {
            showProgressBar(false);

            if(response.isSuccessful()){
                if(isRefreshing){
                    isRefreshing = false;
                    displayFirstPage(response.body().getData());
                }else{
                    updateList(response.body().getData());
                }
            }else{
                Log.wtf(LOG_TAG, "Failed: " + response.code());
            }
        }
        @Override
        public void onFailure(Call<Albums> call, Throwable t) {
            showProgressBar(false);
            Log.wtf(LOG_TAG, t.getMessage());
        }
    }

    private void showProgressBar(boolean refresh) {
        mSwipeRefreshLayout.setRefreshing(refresh);
    }

    @Override
    public void dataRetrievedFromCache(List<AlbumEntity> albumEntities) {
        Log.wtf(LOG_TAG, "Albums: ");
        showProgressBar(false);

        if(albumEntities.size() > 0){
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextViewNoInternet.setVisibility(View.GONE);
            Utils.showMessage(mSwipeRefreshLayout,R.string.no_internet_connection);
        }else{
            mRecyclerView.setVisibility(View.GONE);
            mTextViewNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<Album> create(){
        ArrayList<Album> albums = new ArrayList<>();
        for(int i = mAlbums.size(), k = mAlbums.size() + 7; i < k; i++){
            albums.add(new Album().setName("Album " + i).setReleaseDate("1995").setSongCount(13));
        }

        return albums;
    }
}

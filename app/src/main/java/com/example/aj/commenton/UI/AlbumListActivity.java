package com.example.aj.commenton.UI;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.aj.commenton.R;
import com.example.aj.commenton.exception.AuthenticationClientNotFound;
import com.example.aj.commenton.listener.ReadAlbumListener;
import com.example.aj.commenton.adapter.AlbumAdapter;
import com.example.aj.commenton.db.AlbumRepo;
import com.example.aj.commenton.listener.EndlessRecyclerViewScrollListener;
import com.example.aj.commenton.model.Album;
import com.example.aj.commenton.model.Albums;
import com.example.aj.commenton.network.retrofit.RetrofitInstance;
import com.example.aj.commenton.network.service.AndroidAcademyWebService;
import com.example.aj.commenton.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.aj.commenton.util.Utils.isNetworkConnected;

public class AlbumListActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, ReadAlbumListener{

//    done: no internet connection case
//    todo: no data case
//    todo: fix up design of items

    private final String LOG_TAG = LoginFragment.class.getName();

    @BindView(R.id.recycler_view_album_list) RecyclerView mRecyclerView;
    @BindView(R.id.activity_home_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.txt_no_internet) TextView mTextViewNoInternet;

    private AlbumAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<Album> mAlbums;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private int mCurrentPage = 1;
    private  int mFirstPage = 1;
    private boolean isRefreshing = true;
    private AlbumRepo mAlbumRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        init();

        tryToLoadAlbumsByPageNumber(mFirstPage);
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        tryToLoadAlbumsByPageNumber(mFirstPage);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, AuthActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onDataRetrievedFromCache(ArrayList<Album> cachedAlbumList) {
        Log.wtf(LOG_TAG, "Albums: ");
        showProgressBar(false);

        if(isCacheAvailable(cachedAlbumList)){
            showNoInternetConnectionView(false);
            Utils.showMessage(mSwipeRefreshLayout,R.string.no_internet_connection);
            populateList(cachedAlbumList);
        }else{
            showNoInternetConnectionView(true);
        }
    }

    private void init(){

        mAlbums = new ArrayList<>();

        mAlbumRepo = new AlbumRepo(getApplication(), this);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new AlbumAdapter(this,mAlbums);
        mRecyclerView.setAdapter(mAdapter);
        mScrollListener = makeScrollListener();

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private EndlessRecyclerViewScrollListener makeScrollListener(){
        return new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mCurrentPage++;
                tryToLoadAlbumsByPageNumber(mCurrentPage);
            }
        };
    }

    private void tryToLoadAlbumsByPageNumber(int pageNumber){
        if(isRefreshing){
            showProgressBar(true);
        }

        if(!isNetworkConnected(this)){
            removeEndlessScrollListener();
            populateListFromCache();
            return;
        }else{
            showNoInternetConnectionView(false);
            loadAlbumsByPageNumber(pageNumber);
        }
    }

    private void loadAlbumsByPageNumber(int pageNumber){
        try{
            AndroidAcademyWebService webService = RetrofitInstance
//                    .retrofitInstanceWithAndroidAcademyWithBasicAuth("qa@mail.com","12345678")
                    .retrofitInstanceWithAuthenticatedClient()
                    .create(AndroidAcademyWebService.class);

            Call<Albums> albumsCall = webService.listOfAlbumsByPageNumber(pageNumber);
            albumsCall.enqueue(new GetAlbumsCallback());

        } catch (AuthenticationClientNotFound e) {
//            authenticated http client is not found thus user returns to login

            showProgressBar(false);
            navigateToLogin();
        }
    }

    private void removeEndlessScrollListener() {
        mRecyclerView.removeOnScrollListener(mScrollListener);
    }

    private void populateListFromCache() {
        mAlbumRepo.retrieveCache();
    }

    private void populateListWithConsecutivePage(ArrayList<Album> albumArrayList) {

        if(mCurrentPage > 1){
            if(mAlbums.get(mAlbums.size() -1).isLast){
                mAlbums.remove(mAlbums.size() -1);
                mAdapter.notifyItemRemoved(mAlbums.size());
            }
        }

        if(albumArrayList.size() == 0){
            return;
        }

        int currentSize = mAdapter.getItemCount();

        addAllItemsToList(albumArrayList);
//        mAlbums.addAll(albumArrayList);
        cacheData(mAlbums);
        addItemToList(new Album().setLast(true));
//        mAlbums.add(new Album().setLast(true));
        mAdapter.notifyItemRangeInserted(currentSize,mAlbums.size() -1);
    }

    private void populateListWithFirstPage(ArrayList<Album> albumArrayList) {
        clearList();

        EndlessRecyclerViewScrollListener scrollListener = makeScrollListener();

        mRecyclerView.addOnScrollListener(scrollListener);

        mCurrentPage = mFirstPage;

        int currentSize = mAdapter.getItemCount();

//        mAlbums.addAll(albumArrayList);
        addAllItemsToList(albumArrayList);
        cacheData(mAlbums);
        addItemToList(new Album().setLast(true));
//        mAlbums.add(new Album().setLast(true));
        mAdapter.notifyItemRangeInserted(currentSize,mAlbums.size() -1);
    }

    private void clearList() {
        mAlbums.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void cacheData(ArrayList<Album> albumArrayList) {
        mAlbumRepo.insertAll(albumArrayList);
    }

    class GetAlbumsCallback implements Callback<Albums>{

        @Override
        public void onResponse(Call<Albums> call, Response<Albums> response) {
            showProgressBar(false);

            if(response.isSuccessful()){
                if(isRefreshing){
                    isRefreshing = false;
                    populateListWithFirstPage(response.body().getData());
                }else{
                    populateListWithConsecutivePage(response.body().getData());
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

    private void showNoInternetConnectionView(boolean show) {
        mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        mTextViewNoInternet.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private boolean isCacheAvailable(ArrayList<Album> albumList) {
        return albumList.size() > 0;
    }

    private void populateList(ArrayList<Album> albumArrayList){
        clearList();

        addAllItemsToList(albumArrayList);
        mAdapter.notifyDataSetChanged();
    }

    private void addItemToList(Album album){
        if(!mAlbums.contains(album)){
            mAlbums.add(album);
        }
    }

    private void addAllItemsToList(ArrayList<Album> albums){
        for(Album newAlbum : albums){
            if(!mAlbums.contains(newAlbum)){
                mAlbums.add(newAlbum);
            }
        }
    }

}

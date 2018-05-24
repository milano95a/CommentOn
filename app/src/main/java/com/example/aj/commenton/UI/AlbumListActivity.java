package com.example.aj.commenton.UI;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.aj.commenton.R;
import com.example.aj.commenton.adapter.AlbumAdapter;
import com.example.aj.commenton.exception.AuthenticationClientNotFound;
import com.example.aj.commenton.listener.EndlessRecyclerViewScrollListener;
import com.example.aj.commenton.model.Album;
import com.example.aj.commenton.model.Albums;
import com.example.aj.commenton.network.retrofit.RetrofitInstance;
import com.example.aj.commenton.network.retrofit.service.AndroidAcademyWebService;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

//    TODO: no internet connection case
//    todo: no dat case
//    todo: fix up design of items

    private final String LOG_TAG = LoginFragment.class.getName();

    @BindView(R.id.recycler_view_album_list) RecyclerView mRecyclerView;
    @BindView(R.id.activity_home_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    private AlbumAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<Album> mAlbums;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int mLastPage = 1;
    private  int mFirstPage = 1;
    private boolean isRefreshing = true;

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
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAlbums = new ArrayList<>();

        mAdapter = new AlbumAdapter(this,mAlbums);
        mRecyclerView.setAdapter(mAdapter);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mLastPage++;
                loadAlbumsByPageNumber(mLastPage);
            }
        };

        mRecyclerView.addOnScrollListener(scrollListener);
    }

    private void loadAlbumsByPageNumber(int pageNumber){
        try{
            if(isRefreshing){
                showProgressBar(true);
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
        mAlbums.add(new Album().setLast(true));
        mAdapter.notifyItemRangeInserted(currentSize,mAlbums.size() -1);
    }

    private void showProgressBar(ArrayList<Album> albumArrayList) {
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
        mAlbums.add(new Album().setLast(true));
        mAdapter.notifyItemRangeInserted(currentSize,mAlbums.size() -1);
    }

    class AlbumCallback implements Callback<Albums>{

        @Override
        public void onResponse(Call<Albums> call, Response<Albums> response) {
            showProgressBar(false);

            if(response.isSuccessful()){
                if(isRefreshing){
                    isRefreshing = false;
                    showProgressBar(response.body().getData());
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

    private ArrayList<Album> create(){
        ArrayList<Album> albums = new ArrayList<>();
        for(int i = mAlbums.size(), k = mAlbums.size() + 7; i < k; i++){
            albums.add(new Album().setName("Album " + i).setReleaseDate("1995").setSongCount(13));
        }

        return albums;
    }
}

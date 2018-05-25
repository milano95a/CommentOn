package com.example.aj.commenton.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aj.commenton.util.Constants;
import com.example.aj.commenton.R;
import com.example.aj.commenton.adapter.CommentAdapter;
import com.example.aj.commenton.exception.AuthenticationClientNotFound;
import com.example.aj.commenton.model.Album;
import com.example.aj.commenton.model.Comment;
import com.example.aj.commenton.model.Comments;
import com.example.aj.commenton.network.retrofit.RetrofitInstance;
import com.example.aj.commenton.network.service.AndroidAcademyWebService;
import com.example.aj.commenton.util.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity implements Callback<Comments>{

//    todo; use case: start loading then turn off internet
//    todo; save username in shared preferences
//    todo; operations on time
//    todo; rom + sqlite
//    todo; update callback listeners

    private final String LOG_TAG = CommentActivity.class.getName();

    @BindView(R.id.recycler_view_comment_list) RecyclerView mRecyclerView;
    @BindView(R.id.edt_comment) EditText mEdtComment;
    @BindView(R.id.btn_post) Button mBtnPost;
    @BindView(R.id.pb_post) ProgressBar mPbarPost;

    @BindView(R.id.ly_no_internet) View mNoInternetView;
    @BindView(R.id.txt_no_internet) TextView mTextViewNoInternet;
    @BindView(R.id.btn_try_again) Button mBtnTryAgain;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.ly_comment_view) View vCommentView;

    private Album mAlbum;
    private CommentAdapter mAdapter;
    private ArrayList<Comment> mComments;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

//        Log.wtf(LOG_TAG, "" + Utils.convertStringDate("2018-05-04T15:49:27+00:00"));
        init();
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mComments = new ArrayList<>();
        mAlbum = getPassedObject();


        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
//        mLinearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new CommentAdapter(this,mComments);
        mRecyclerView.setAdapter(mAdapter);

        if(mAlbum != null){
            setTitle(mAlbum.getName());
        }


        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postCommentWithAlbumId(mEdtComment.getText().toString(), mAlbum.getId());
            }
        });

        mBtnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressBar(true);

                if(!Utils.isNetworkConnected(CommentActivity.this)){
                    showNoInternetWindow(true);
                    showProgressBar(false);
                }else{
                    getListOfComments();
                }
            }
        });

        if(!Utils.isNetworkConnected(this)){

        }else{
            getListOfComments();

        }
    }

    private void showNoInternetWindow(boolean show){
        mNoInternetView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showProgressBar(boolean show){
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void getListOfComments(){
        try {

            AndroidAcademyWebService webService = RetrofitInstance
                    .retrofitInstanceWithAuthenticatedClient()
                    .create(AndroidAcademyWebService.class);

            Call<Comments> getListOfCommentsTask = webService.getCommentsByAlbumId(mAlbum.getId());
            getListOfCommentsTask.enqueue(this);

        } catch (AuthenticationClientNotFound e) {
            e.printStackTrace();
            navigateToLogin();
        }
    }

    private void postCommentWithAlbumId(final String commentText, int albumId){
        try {
            AndroidAcademyWebService webService = RetrofitInstance
                    .retrofitInstanceWithAuthenticatedClient()
                    .create(AndroidAcademyWebService.class);

            Comment comment = new Comment(albumId, commentText);

            Call<Void> commentPostTask = webService.postComment(comment);
            commentPostTask.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()){
                        Log.wtf(LOG_TAG,new Gson().toJson(response.body()));
                        addCommentToList(commentText);
                    }else{
                        Log.wtf(LOG_TAG,"Status: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.wtf(LOG_TAG,t.getMessage());
                }
            });

        } catch (AuthenticationClientNotFound e) {
            e.printStackTrace();
            navigateToLogin();
        }
    }

    private void addCommentToList(String comment){
        mComments.add(new Comment(
                getAuthorName(),
                comment,
                Utils.convertDateToString(new Date())));
        mAdapter.notifyItemInserted(mComments.size());
        scrollToBottom();
        clearEditFields();
    }

    private void clearEditFields(){
        mEdtComment.setText("");
    }

    private void scrollToBottom(){
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() -1);
    }

    @Override
    public void onResponse(Call<Comments> call, Response<Comments> response) {
        Log.wtf(LOG_TAG, new Gson().toJson(response.body()));

        if (response.body() != null) {
            mComments.addAll(response.body().getData());
            mAdapter.notifyItemRangeInserted(0,mComments.size() -1);
        }
    }

    @Override
    public void onFailure(Call<Comments> call, Throwable t) {
        Log.wtf(LOG_TAG, t.getMessage());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return false;
    }

    private Album getPassedObject(){
        Gson gson = new Gson();
        String albumAsAString = getIntent().getStringExtra(Constants.ALBUM_KEY);
        Album album = gson.fromJson(albumAsAString, Album.class);
        return album;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, AuthActivity.class);
        finish();
        startActivity(intent);
    }

    public String getAuthorName() {
        return Utils.retrieveValue(this,Constants.USERNAME);
    }
}

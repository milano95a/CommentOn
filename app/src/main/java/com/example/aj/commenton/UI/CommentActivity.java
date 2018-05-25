package com.example.aj.commenton.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.aj.commenton.util.Utils.isNetworkConnected;
import static com.example.aj.commenton.util.Utils.showMessage;

public class CommentActivity extends AppCompatActivity{

//    todo; use case: start loading then turn off internet
//    done; save username in shared preferences
//    done; operations on time
//    done; rom + sqlite
//    todo; update callback listeners

    private final String LOG_TAG = CommentActivity.class.getName();

    @BindView(R.id.recycler_view_comment_list) RecyclerView mRecyclerView;
    @BindView(R.id.edt_comment) EditText mEdtComment;
    @BindView(R.id.pb_post) ProgressBar mPbarPost;
    @BindView(R.id.ly_no_internet) View mNoInternetView;
    @BindView(R.id.txt_no_internet) TextView mTextViewNoInternet;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.ly_comment_view) View mCommentView;

    private Album mAlbum;
    private CommentAdapter mAdapter;
    private ArrayList<Comment> mComments;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        init();
    }

    @OnClick(R.id.btn_post)
    void onCommentPost(){
        tryToPostComment();
    }

    @OnClick(R.id.btn_try_again)
    void tryAgain(){
        showProgressBar(true);

        tryToGetComments();
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

    private void init() {

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mComments = new ArrayList<>();

        mAlbum = getPassedObject();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
//        mLinearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CommentAdapter(this,mComments);
        mRecyclerView.setAdapter(mAdapter);

        setTitle(mAlbum.getName());

        tryToGetComments();
    }

    private void tryToGetComments() {
        if(isNetworkConnected(this)){

            getListOfComments();

        }else{

            showProgressBar(false);

            showNoInternetView(true);
        }
    }

    private void tryToPostComment() {
        if(isNetworkConnected(this)){

            postCommentWithAlbumId(mEdtComment.getText().toString(), mAlbum.getId());

        }else{
            showMessage(mCommentView, R.string.no_internet_connection);
        }
    }

    private void getListOfComments(){
        try {

            AndroidAcademyWebService webService = RetrofitInstance
                    .retrofitInstanceWithAuthenticatedClient()
                    .create(AndroidAcademyWebService.class);

            Call<Comments> getListOfCommentsTask = webService.getCommentsByAlbumId(mAlbum.getId());
            getListOfCommentsTask.enqueue(new GetListOfCommentsCallback());

        } catch (AuthenticationClientNotFound e) {
            e.printStackTrace();
            navigateToLogin();
        }
    }

    class GetListOfCommentsCallback implements Callback<Comments>{
        @Override
        public void onResponse(@NonNull Call<Comments> call, @NonNull Response<Comments> response) {
            Log.wtf(LOG_TAG, new Gson().toJson(response.body()));
            if(response.isSuccessful()){

                mComments.addAll(response.body().getData());
                mAdapter.notifyItemRangeInserted(0,mComments.size() -1);

            }else{
                Log.wtf(LOG_TAG,"Status: " + response.code());

                @SuppressLint("StringFormatMatches")
                String message = getResources().getString(R.string.posting_comment_failed, response.code());

                showMessage(mCommentView, message);
            }
        }

        @Override
        public void onFailure(@NonNull Call<Comments> call, @NonNull Throwable t) {
            Log.wtf(LOG_TAG, t.getMessage());

            showMessage(mCommentView, R.string.get_comments_failed_try_again);
        }
    }

    private void postCommentWithAlbumId(final String commentText, int albumId){
        try {
            AndroidAcademyWebService webService = RetrofitInstance
                    .retrofitInstanceWithAuthenticatedClient()
                    .create(AndroidAcademyWebService.class);

            Comment comment = new Comment(albumId, commentText);

            Call<Void> commentPostTask = webService.postComment(comment);
            commentPostTask.enqueue(new PostCommentCallback(commentText));

        } catch (AuthenticationClientNotFound e) {
            e.printStackTrace();
            navigateToLogin();
        }
    }

    class PostCommentCallback implements Callback<Void>{

        private String mCommentText;

        PostCommentCallback(String commentText){
            this.mCommentText = commentText;
        }

        @Override
        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            if(response.isSuccessful()){

                addCommentToList(mCommentText);

            }else{
//                Log.wtf(LOG_TAG,"Status: " + response.code());

                @SuppressLint("StringFormatMatches")
                String message = getResources().getString(R.string.posting_comment_failed, response.code());

                showMessage(mCommentView, message);

            }
        }

        @Override
        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

            Log.wtf(LOG_TAG,t.getMessage());

            showMessage(mCommentView, R.string.post_failed_try_again);
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

    private void showProgressBar(boolean show){
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showNoInternetView(boolean show){
        mNoInternetView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void clearEditFields(){
        mEdtComment.setText("");
    }

    private void scrollToBottom(){
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() -1);
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

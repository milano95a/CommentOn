package com.example.aj.commenton.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.aj.commenton.Constants;
import com.example.aj.commenton.R;
import com.example.aj.commenton.model.Album;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumItemActivity extends AppCompatActivity {

//    todo: clean up

    private Album mAlbum;

    @BindView(R.id.txt_song_name) TextView mTextViewSongName;
    @BindView(R.id.txt_song_count) TextView mTextViewSongCount;
    @BindView(R.id.txt_release_date) TextView mTextViewReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_item);
        ButterKnife.bind(this);

        init();

    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAlbum = getPassedObject();

        if(mAlbum != null){
            setTitle(mAlbum.getName());

            mTextViewSongName.setText(mAlbum.getName());
            mTextViewSongCount.setText(String.valueOf(mAlbum.getSongCount()));
            mTextViewReleaseDate.setText(mAlbum.getReleaseDate().substring(0,4));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_comment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAlbum != null){
                    Gson gson = new Gson();
                    String albumJson = gson.toJson(mAlbum);

                    Intent intent = new Intent(AlbumItemActivity.this, CommentActivity.class);
                    intent.putExtra(Constants.ALBUM_KEY, albumJson);
                    startActivity(intent);
                }else{
                    Snackbar.make(view, "Album is null", Snackbar.LENGTH_LONG).show();
                }
            }
        });
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
}

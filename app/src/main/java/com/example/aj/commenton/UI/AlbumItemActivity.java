package com.example.aj.commenton.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aj.commenton.Constants;
import com.example.aj.commenton.R;
import com.example.aj.commenton.model.Album;
import com.google.gson.Gson;

public class AlbumItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_item);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        Gson gson = new Gson();
        String albumDataObjectAsAString = getIntent().getStringExtra(Constants.ALBUM_KEY);
        Album studentDataObject = gson.fromJson(albumDataObjectAsAString, Album.class);
        if(studentDataObject != null){
            if(studentDataObject.getName() != null){
                setTitle(studentDataObject.getName());
            }
        }
    }
}

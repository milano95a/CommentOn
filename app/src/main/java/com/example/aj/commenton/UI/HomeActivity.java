package com.example.aj.commenton.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.aj.commenton.R;
import com.example.aj.commenton.exception.AuthenticationClientNotFound;
import com.example.aj.commenton.network.retrofit.RetrofitInstance;
import com.example.aj.commenton.network.retrofit.model.Albums;
import com.example.aj.commenton.network.retrofit.service.AndroidAcademyWebService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try{
            AndroidAcademyWebService androidAcademyService = RetrofitInstance
                    .retrofitInstanceWithAuthenticatedClient()
                    .create(AndroidAcademyWebService.class);

            Call<Albums> call2 = androidAcademyService.listAlbums(1);
            call2.enqueue(new Callback<Albums>() {
                @Override
                public void onResponse(Call<Albums> call, Response<Albums> response) {
                    Gson gson = new Gson();
                    String strResponse = gson.toJson(response.body());
                    Log.wtf("SUCCESS", strResponse);
                }

                @Override
                public void onFailure(Call<Albums> call, Throwable t) {
                    Log.wtf("FAILED", t.getMessage());
                }
            });

        }catch (AuthenticationClientNotFound e){
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, AuthActivity.class);
        finish();
        startActivity(intent);
    }
}

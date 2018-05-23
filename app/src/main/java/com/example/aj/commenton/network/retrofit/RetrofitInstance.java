package com.example.aj.commenton.network.retrofit;

import com.example.aj.commenton.exception.AuthenticationClientNotFound;
import com.example.aj.commenton.network.okhttp.OkHttpInstance;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static final String ANDROID_ACADEMY_BASE_URL = "https://android.academy.e-legion.com/api/";

    private static OkHttpClient okHttpClient = null;

    public static Retrofit retrofitInstanceWithAndroidAcademyWithBasicAuth(String username, String password){
        okHttpClient = OkHttpInstance.okHttpClientWithBasicAuth(username, password);
        return new Retrofit.Builder()
                .baseUrl(ANDROID_ACADEMY_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit retrofitInstanceWithAndroidAcademy(){
        return new Retrofit.Builder()
                .baseUrl(ANDROID_ACADEMY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit retrofitInstanceWithAuthenticatedClient() throws AuthenticationClientNotFound{
        if(okHttpClient == null) throw new AuthenticationClientNotFound("Authenticated client is null");

        return new Retrofit.Builder()
                .baseUrl(ANDROID_ACADEMY_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

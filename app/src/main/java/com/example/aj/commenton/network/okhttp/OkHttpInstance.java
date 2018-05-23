package com.example.aj.commenton.network.okhttp;

import okhttp3.OkHttpClient;

public class OkHttpInstance {

    public static OkHttpClient okHttpClientWithBasicAuth(String username, String password){
        return new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(username, password))
                .build();
    }


}

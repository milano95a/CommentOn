package com.example.aj.commenton.network.retrofit.service;

import com.example.aj.commenton.network.retrofit.model.Albums;
import com.example.aj.commenton.network.retrofit.model.User;
import com.example.aj.commenton.network.retrofit.model.Users;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AndroidAcademyWebService {

//    @GET("albums")
//    Call<Album> listAlbums(@Query("page") int pageNumber);

    @GET("user")
    Call<Users> user();

    @POST("registration")
    Call<Void> registration(@Body User user);

    @GET("albums")
    Call<Albums> listAlbums(@Query("page") int pageNumber);

}

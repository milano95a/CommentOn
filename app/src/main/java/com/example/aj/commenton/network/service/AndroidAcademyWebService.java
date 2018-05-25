package com.example.aj.commenton.network.service;

import com.example.aj.commenton.model.Albums;
import com.example.aj.commenton.model.Comment;
import com.example.aj.commenton.model.Comments;
import com.example.aj.commenton.model.User;
import com.example.aj.commenton.model.Users;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AndroidAcademyWebService {

//    @GET("albums")
//    Call<Album> listOfAlbumsByPageNumber(@Query("page") int pageNumber);

    @GET("user")
    Call<Users> getUser();

    @POST("registration")
    Call<Void> registration(@Body User user);

    @GET("albums")
    Call<Albums> listOfAlbumsByPageNumber(@Query("page") int pageNumber);

    @GET("albums/{id}/comments")
    Call<Comments> getCommentsByAlbumId(@Path("id") int id);

    @POST("comments")
    Call<Void> postComment(@Body Comment comment);
}

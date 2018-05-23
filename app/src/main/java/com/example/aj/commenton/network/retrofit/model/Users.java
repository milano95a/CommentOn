package com.example.aj.commenton.network.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Users {
    @SerializedName("data")
    User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}

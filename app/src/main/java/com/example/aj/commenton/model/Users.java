package com.example.aj.commenton.model;

import com.google.gson.annotations.SerializedName;

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

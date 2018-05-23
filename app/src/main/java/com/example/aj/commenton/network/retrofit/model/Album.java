package com.example.aj.commenton.network.retrofit.model;

import com.google.gson.annotations.SerializedName;

public class Album {

    @SerializedName("name")
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

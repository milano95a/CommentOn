package com.example.aj.commenton.network.retrofit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Albums {

    @SerializedName("data")
    List<Album> data;

    public List<Album> getData() {
        return data;
    }

    public void setData(List<Album> data) {
        this.data = data;
    }
}

package com.example.aj.commenton.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Comments {
    @SerializedName("data")
    ArrayList<Comment> data;

    public ArrayList<Comment> getData() {
        return data;
    }

    public void setData(ArrayList<Comment> data) {
        this.data = data;
    }
}

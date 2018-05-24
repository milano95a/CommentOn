package com.example.aj.commenton.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Albums {

    @SerializedName("data")
    ArrayList<Album> data;

    public ArrayList<Album> getData() {
        return data;
    }

    public void setData(ArrayList<Album> data) {
        this.data = data;
    }
}

package com.example.aj.commenton.model;

import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("id")
    public int id;

    @SerializedName("album_id")
    public int album_id;

    @SerializedName("text")
    public String text;

    @SerializedName("author")
    public String author;

    @SerializedName("timestamp")
    public String timestamp;

    public Comment() { }

    public Comment(int album_id, String text) {
        this.album_id = album_id;
        this.text = text;
    }

    public Comment(String author, String comment, String time) {
        this.author = author;
        this.text = comment;
        this.timestamp = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Favorite implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("playlistTitle")
    @Expose
    private String playlistTitle;

    @SerializedName("cartoonTitle")
    @Expose
    private String cartoonTitle;

    @SerializedName("imgUrl")
    @Expose
    private String imgUrl;

    @SerializedName("videoUrl")
    @Expose
    private String videoUrl;

    public Favorite() {
    }

    public Favorite(int id, String title, String playlistTitle, String cartoonTitle, String imgUrl, String videoUrl) {
        this.id = id;
        this.title = title;
        this.playlistTitle = playlistTitle;
        this.cartoonTitle = cartoonTitle;
        this.imgUrl = imgUrl;
        this.videoUrl = videoUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaylistTitle() {
        return playlistTitle;
    }

    public void setPlaylistTitle(String playlistTitle) {
        this.playlistTitle = playlistTitle;
    }

    public String getCartoonTitle() {
        return cartoonTitle;
    }

    public void setCartoonTitle(String cartoonTitle) {
        this.cartoonTitle = cartoonTitle;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}

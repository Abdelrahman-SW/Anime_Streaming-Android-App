package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Episode implements Serializable {

    @SerializedName("id")
    @Expose
    private int id = 0;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("thumb")
    @Expose
    private String thumb;

    @SerializedName("video")
    @Expose
    private String video;

    @SerializedName("video1")
    @Expose
    private String video1;

    @SerializedName("video2")
    @Expose
    private String video2;

    @SerializedName("video3")
    @Expose
    private String video3;

    @SerializedName("video4")
    @Expose
    private String video4;

    @SerializedName("video5")
    @Expose
    private String video5;

    @SerializedName("playlistId")
    @Expose
    private int playlistId;

    @SerializedName("cartoon")
    @Expose
    private CartoonWithInfo cartoon;

    @SerializedName("jResolver")
    @Expose
    private Integer jResolver = 0;

    @SerializedName("jResolver1")
    @Expose
    private Integer jResolver1 = 0;

    @SerializedName("jResolver2")
    @Expose
    private Integer jResolver2 = 0;

    @SerializedName("jResolver3")
    @Expose
    private Integer jResolver3 = 0;

    @SerializedName("jResolver4")
    @Expose
    private Integer jResolver4 = 0;

    @SerializedName("jResolver5")
    @Expose
    private Integer jResolver5 = 0;

    @SerializedName("playlist")
    @Expose
    private Playlist playlist;
    // used for downloaded episode :)
    @SerializedName("video_url")
    @Expose
    private String video_url;

    private boolean Error = false;

    public Episode() {
    }

    public Episode(int id, String title, String thumb, String video, int playlistId) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
        this.video = video;
        this.playlistId = playlistId;
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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideo1() {
        return video1;
    }

    public void setVideo1(String video1) {
        this.video1 = video1;
    }

    public String getVideo2() {
        return video2;
    }

    public void setVideo2(String video2) {
        this.video2 = video2;
    }

    public String getVideo3() {
        return video3;
    }

    public void setVideo3(String video3) {
        this.video3 = video3;
    }

    public String getVideo4() {
        return video4;
    }

    public void setVideo4(String video4) {
        this.video4 = video4;
    }

    public String getVideo5() {
        return video5;
    }

    public void setVideo5(String video5) {
        this.video5 = video5;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public CartoonWithInfo getCartoon() {
        return cartoon;
    }

    public void setCartoon(CartoonWithInfo cartoon) {
        this.cartoon = cartoon;
    }

    public Integer getjResolver() {
        return jResolver;
    }

    public void setjResolver(Integer jResolver) {
        this.jResolver = jResolver;
    }

    public Integer getjResolver1() {
        return jResolver1;
    }

    public void setjResolver1(Integer jResolver1) {
        this.jResolver1 = jResolver1;
    }

    public Integer getjResolver2() {
        return jResolver2;
    }

    public void setjResolver2(Integer jResolver2) {
        this.jResolver2 = jResolver2;
    }

    public Integer getjResolver3() {
        return jResolver3;
    }

    public void setjResolver3(Integer jResolver3) {
        this.jResolver3 = jResolver3;
    }

    public Integer getjResolver4() {
        return jResolver4;
    }

    public void setjResolver4(Integer jResolver4) {
        this.jResolver4 = jResolver4;
    }

    public Integer getjResolver5() {
        return jResolver5;
    }

    public void setjResolver5(Integer jResolver5) {
        this.jResolver5 = jResolver5;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public boolean isError() {
        return Error;
    }

    public void setError(boolean error) {
        Error = error;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
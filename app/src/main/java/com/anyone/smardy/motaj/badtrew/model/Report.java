package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Report {
    @SerializedName("episode_id")
    @Expose
    private int episodeId;
    @SerializedName("episode_title")
    @Expose
    private String episodeTitle;
    @SerializedName("episode_video")
    @Expose
    private String episodeUrl;
    @SerializedName("playlist_title")
    @Expose
    private String playlistTitle;
    @SerializedName("cartoon_title")
    @Expose
    private String cartoonTitle;

    public Report() {
    }

    public Report(int episodeId, String episodeTitle, String episodeUrl, String playlistTitle, String cartoonTitle) {
        this.episodeId = episodeId;
        this.episodeTitle = episodeTitle;
        this.episodeUrl = episodeUrl;
        this.playlistTitle = playlistTitle;
        this.cartoonTitle = cartoonTitle;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }

    public String getEpisodeUrl() {
        return episodeUrl;
    }

    public void setEpisodeUrl(String episodeUrl) {
        this.episodeUrl = episodeUrl;
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
}

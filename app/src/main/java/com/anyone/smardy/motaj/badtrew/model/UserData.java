package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserData implements Serializable {
    @SerializedName("favourite")
    @Expose
    private List<CartoonWithInfo> favouriteCartoons ;
    @SerializedName("watched")
    @Expose
    private List<CartoonWithInfo> watchCartoons ;
    @SerializedName("watch_later")
    @Expose
    private List<CartoonWithInfo> watchLaterCartoons ;
    @SerializedName("seen")
    @Expose
    private List<Integer> seenEpisodesIds ;
    @SerializedName("latest")
    @Expose
    List<EpisodeWithInfo> latestEpisodes ;

    public List<CartoonWithInfo> getFavouriteCartoons() {
        return favouriteCartoons;
    }

    public void setFavouriteCartoons(List<CartoonWithInfo> favouriteCartoons) {
        this.favouriteCartoons = favouriteCartoons;
    }

    public List<CartoonWithInfo> getWatchLaterCartoons() {
        return watchLaterCartoons;
    }

    public void setWatchLaterCartoons(List<CartoonWithInfo> watchLaterCartoons) {
        this.watchLaterCartoons = watchLaterCartoons;
    }

    public List<Integer> getSeenEpisodesIds() {
        return seenEpisodesIds;
    }

    public void setSeenEpisodesIds(List<Integer> seenEpisodesIds) {
        this.seenEpisodesIds = seenEpisodesIds;
    }

    public List<EpisodeWithInfo> getLatestEpisodes() {
        return latestEpisodes;
    }

    public void setLatestEpisodes(List<EpisodeWithInfo> latestEpisodes) {
        this.latestEpisodes = latestEpisodes;
    }

    public List<CartoonWithInfo> getWatchCartoons() {
        return watchCartoons;
    }

    public void setWatchCartoons(List<CartoonWithInfo> watchCartoons) {
        this.watchCartoons = watchCartoons;
    }
}

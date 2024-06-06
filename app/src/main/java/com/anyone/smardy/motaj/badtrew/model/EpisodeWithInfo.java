package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpisodeWithInfo extends Episode {

    @SerializedName("world_rate")
    @Expose
    private String world_rate;

    @SerializedName("view_date")
    @Expose
    private String view_date;

    @SerializedName("status")
    @Expose
    private Integer status;

    public EpisodeWithInfo() {

    }

    public EpisodeWithInfo(int id, String title, String thumb, String video, int playlistId, String world_rate, String view_date, Integer status) {
        super(id, title, thumb, video, playlistId);
        this.world_rate = world_rate;
        this.view_date = view_date;
        this.status = status;
    }

    public String getWorld_rate() {
        return world_rate;
    }

    public void setWorld_rate(String world_rate) {
        this.world_rate = world_rate;
    }

    public String getView_date() {
        return view_date;
    }

    public void setView_date(String view_date) {
        this.view_date = view_date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}

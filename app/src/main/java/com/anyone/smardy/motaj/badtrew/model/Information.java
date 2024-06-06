package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Information implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("story")
    @Expose
    private String story;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("world_rate")
    @Expose
    private String world_rate;

    @SerializedName("view_date")
    @Expose
    private String view_date;

    @SerializedName("age_rate")
    @Expose
    private Integer age_rate;

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("cartoon_id")
    @Expose
    private String cartoon_id;

    public Information(Integer id, String story, String category, String world_rate, String view_date, Integer age_rate, Integer status, String cartoon_id) {
        this.id = id;
        this.story = story;
        this.category = category;
        this.world_rate = world_rate;
        this.view_date = view_date;
        this.age_rate = age_rate;
        this.status = status;
        this.cartoon_id = cartoon_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Integer getAge_rate() {
        return age_rate;
    }

    public void setAge_rate(Integer age_rate) {
        this.age_rate = age_rate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCartoon_id() {
        return cartoon_id;
    }

    public void setCartoon_id(String cartoon_id) {
        this.cartoon_id = cartoon_id;
    }
}


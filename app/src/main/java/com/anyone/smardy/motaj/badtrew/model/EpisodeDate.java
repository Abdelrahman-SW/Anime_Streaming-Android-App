package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EpisodeDate implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("day")
    @Expose
    private int day;
    @SerializedName("cartoon_id")
    @Expose
    private Integer cartoon_id = 0;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("thumb")
    @Expose
    private String thumb;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("classification")
    @Expose
    private int classification;
    @SerializedName("world_rate")
    @Expose
    private String world_rate;
    @SerializedName("view_date")
    @Expose
    private String view_date;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("age_rate")
    @Expose
    private String age_rate;
    public EpisodeDate() {
    }

    public EpisodeDate(Integer id, String name, int day, Integer cartoon_id, String title, String thumb, int type, String world_rate, String view_date, Integer status) {
        this.id = id;
        this.name = name;
        this.day = day;
        this.cartoon_id = cartoon_id;
        this.title = title;
        this.thumb = thumb;
        this.type = type;
        this.world_rate = world_rate;
        this.view_date = view_date;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Integer getCartoon_id() {
        return cartoon_id;
    }

    public void setCartoon_id(Integer cartoon_id) {
        this.cartoon_id = cartoon_id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getClassification() {
        return classification;
    }

    public void setClassification(int classification) {
        this.classification = classification;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAge_rate() {
        return age_rate;
    }

    public void setAge_rate(String age_rate) {
        this.age_rate = age_rate;
    }
}

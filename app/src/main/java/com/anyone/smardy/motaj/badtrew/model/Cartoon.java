
package com.anyone.smardy.motaj.badtrew.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class Cartoon implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id = 0;
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
    @SerializedName("visibility")
    @Expose
    private Boolean visibility;
    @SerializedName("rate")
    private int rate ;

    public Cartoon(Integer id, String title, String thumb, int type, Boolean visibility, int rate) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
        this.type = type;
        this.visibility = visibility;
        this.rate = rate;
    }

    public Cartoon() {

    }
    public Cartoon(Integer id, String title, String thumb, int type) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
        this.type = type;
    }

    public Cartoon(Integer id, String title, String thumb, int type, Boolean visibility) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
        this.type = type;
        this.visibility = visibility;
    }

    public Cartoon(Integer id, String title, String thumb, int type, int classification, Boolean visibility, int rate) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
        this.type = type;
        this.classification = classification;
        this.visibility = visibility;
        this.rate = rate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getClassification() {
        return classification;
    }

    public void setClassification(int classification) {
        this.classification = classification;
    }
}

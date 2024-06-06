package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Redirect implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("app_name")
    @Expose
    private String app_name;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("redirect_type")
    @Expose
    private String redirect_type;

    @SerializedName("package_name")
    @Expose
    private String package_name;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("is_active")
    @Expose
    private String is_active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRedirect_type() {
        return redirect_type;
    }

    public void setRedirect_type(String redirect_type) {
        this.redirect_type = redirect_type;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }
}

package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    @Expose
    private int id ;
    @SerializedName("is_blocked")
    @Expose
    private int is_blocked ;
    @SerializedName("name")
    @Expose
    private String name ;
    @SerializedName("photo_url")
    @Expose
    private String photo_url ;
    @SerializedName("watched_episodes")
    @Expose
    int watched_episodes;
    private String token ;
    private String email ;

    public static final int IS_BLOCKED = 1 ;
    public static final int IS_NOT_BLOCKED = 0 ;
    public static final int UNKNOWN = -1 ;


    public User(int id, int is_blocked, String name, String photo_url, int watched_episodes, String token, String email) {
        this.id = id;
        this.is_blocked = is_blocked;
        this.name = name;
        this.photo_url = photo_url;
        this.watched_episodes = watched_episodes;
        this.token = token;
        this.email = email;
    }

    public int getWatched_episodes() {
        return watched_episodes;
    }

    public void setWatched_episodes(int watched_episodes) {
        this.watched_episodes = watched_episodes;
    }

    public int getIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(int is_blocked) {
        this.is_blocked = is_blocked;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse{
    @SerializedName("id")
    @Expose
    private int returned_id ;
    @SerializedName("user_info")
    @Expose
    private User user;
    @SerializedName("message")
    @Expose
    private String message ;
    @SerializedName("code")
    @Expose
    private int code ;
    @SerializedName("error")
    @Expose
    private boolean error ;

    public UserResponse(String message, int code, boolean error) {
        this.message = message;
        this.code = code;
        this.error = error;
    }



    public UserResponse() {
    }

    public UserResponse(int returned_id, User user, String message, int code, boolean error) {
        this.returned_id = returned_id;
        this.user = user;
        this.message = message;
        this.code = code;
        this.error = error;
    }

    public int getReturned_id() {
        return returned_id;
    }

    public void setReturned_id(int returned_id) {
        this.returned_id = returned_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}

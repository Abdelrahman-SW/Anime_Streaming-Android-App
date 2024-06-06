package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feedback {
    @SerializedName("feedback_id")
    @Expose
    private Integer feedbackId;
    @SerializedName("cartoonId")
    @Expose
    private int cartoonId;
    @SerializedName("userId")
    @Expose
    private int userID;
    @SerializedName("_feedback")
    @Expose
    private String feedback;
    @SerializedName("name")
    @Expose
    private String username;
    @SerializedName("photo_Uri")
    @Expose
    private String photo_Uri;
    @SerializedName("likes")
    @Expose
    private int likes;
    @SerializedName("dislikes")
    @Expose
    private int dislikes;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("num_of_replies")
    @Expose
    private int num_of_replies;

    public Feedback() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Feedback(Integer feedbackId, int cartoonId, int userID, String feedback, String username, String photo_Uri, int likes, int dislikes, String time) {
        this.feedbackId = feedbackId;
        this.cartoonId = cartoonId;
        this.userID = userID;
        this.feedback = feedback;
        this.username = username;
        this.photo_Uri = photo_Uri;
        this.likes = likes;
        this.dislikes = dislikes;
        this.time = time;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }


    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getCartoonId() {
        return cartoonId;
    }

    public void setCartoonId(int cartoonId) {
        this.cartoonId = cartoonId;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto_Uri() {
        return photo_Uri;
    }

    public void setPhoto_Uri(String photo_Uri) {
        this.photo_Uri = photo_Uri;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void incrementLikes() {
        likes+=1;
    }
    public void incrementDisLikes() {
        dislikes+=1;
    }
    public void decrementLikes() {
        likes-=1;
    }
    public void decrementDisLikes() {
        dislikes-=1;
    }

    public int getNum_of_replies() {
        return num_of_replies;
    }

    public void setNum_of_replies(int num_of_replies) {
        this.num_of_replies = num_of_replies;
    }
}

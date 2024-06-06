package com.anyone.smardy.motaj.badtrew.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpisodeComment {

    @SerializedName("comment_id")
    @Expose
    private Integer commentId;
    @SerializedName("episodeId")
    @Expose
    private int episodeId;
    @SerializedName("userId")
    @Expose
    private int userID;
    @SerializedName("_comment")
    @Expose
    private String comment;
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

    public EpisodeComment() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public EpisodeComment(Integer commentId, int episodeId, int userID, String comment, String username, String photo_Uri, int likes, int dislikes, String time) {
        this.commentId = commentId;
        this.episodeId = episodeId;
        this.userID = userID;
        this.comment = comment;
        this.username = username;
        this.photo_Uri = photo_Uri;
        this.likes = likes;
        this.dislikes = dislikes;
        this.time = time;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

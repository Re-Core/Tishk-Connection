package com.recore.tishkconnection.Model;

import com.google.firebase.database.ServerValue;

public class Post {

    private String postKey;
    private String title;
    private String description;
    private String pictureId;
    private String userId;
    private String userPhoto;
    private Object timeStamp;

    public Post(String title, String description, String pictureId, String userId, String userPhoto) {
        this.title = title;
        this.description = description;
        this.pictureId = pictureId;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Post() {
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureId() {
        return pictureId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}

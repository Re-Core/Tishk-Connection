package com.recore.tishkconnection.Model;

public class User {

    private String username;
    private String password;
    private String userId;
    private String userPhoneNumber;
    private String userDepartment;
    private String userImage;
    private String userMail;


    public User() {
    }

    public User(String username, String password, String userImage, String userMail, String userId) {
        this.username = username;
        this.password = password;
        this.userImage = userImage;
        this.userMail = userMail;
        this.userId = userId;
    }

    public User(String username, String password, String userId, String userPhoneNumber, String userDepartment, String userImage, String userMail) {
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.userPhoneNumber = userPhoneNumber;
        this.userDepartment = userDepartment;
        this.userImage = userImage;
        this.userMail = userMail;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}

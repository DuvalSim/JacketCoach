package com.mas.jacketcoach.model;

public class User {
    private String uid;
    private String playNickname;
    private String fullName;
    private String email;

    // Model class for a firebase user
    public User(String uid, String playNickname, String fullName, String email) {
        this.uid = uid;
        this.playNickname = playNickname;
        this.fullName = fullName;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPlayNickname() {
        return playNickname;
    }

    public void setPlayNickname(String playNickname) {
        this.playNickname = playNickname;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

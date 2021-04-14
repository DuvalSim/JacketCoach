package com.mas.jacketcoach.model;

import java.util.ArrayList;

public class User {
    private String uid;
    private String playNickname;
    private String fullName;
    private String email;
    private String phoneNumber;

    // This list is used for both hosting and participating events (distinguished by OrganizerId)
    private ArrayList<String> userEventsIds;

    // Empty constructor required for reading database into an object using ValueEventListener
    public User() {

    }

    // Model class for a firebase user
    public User(String uid, String playNickname, String fullName, String email, String phoneNumber, ArrayList<String> userEventsIds) {
        this.uid = uid;
        this.playNickname = playNickname;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userEventsIds = userEventsIds;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<String> getUserEvents() {
        return userEventsIds;
    }

    public void setUserEvents(ArrayList<String> userEvents) {
        this.userEventsIds = userEvents;
    }
}

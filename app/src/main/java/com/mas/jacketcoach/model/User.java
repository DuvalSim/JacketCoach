package com.mas.jacketcoach.model;

import java.util.ArrayList;

public class User {
    private String uid;
    private String playNickname;
    private String fullName;
    private String email;
    private String phoneNumber;

    // This list is used for both hosting and participating events (distinguished by OrganizerId)
    private ArrayList<Event> userEvents;

    // Empty constructor required for reading database into an object using ValueEventListener
    public User() {

    }

    // Model class for a firebase user
    public User(String uid, String playNickname, String fullName, String email, String phoneNumber) {
        this.uid = uid;
        this.playNickname = playNickname;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;

        // Initialize an empty events list
        userEvents = new ArrayList<>();

        // Uncomment this to make this field show up for a user in Firebase DB **UPON REGISTERING**
//        Event test = new Event(1, "Test", "test", "test", "test", 1.0, 1.0, null);
//        userEvents.add(test);
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

    public ArrayList<Event> getUserEvents() {
        return userEvents;
    }

    public void setUserEvents(ArrayList<Event> userEvents) {
        this.userEvents = userEvents;
    }
}

package com.example.chitchat;

public class Contacts {
    public String username, status, profile;

    public Contacts() {
    }

    public Contacts(String username, String status, String profile) {
        this.username = username;
        this.status = status;
        this.profile = profile;
    }

    public String getName() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}

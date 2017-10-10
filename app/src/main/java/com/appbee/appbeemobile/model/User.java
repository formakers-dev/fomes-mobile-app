package com.appbee.appbeemobile.model;

public class User {
    private String userId;
    private String registrationToken;

    public User(String userId, String registrationToken) {
        this.userId = userId;
        this.registrationToken = registrationToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }
}

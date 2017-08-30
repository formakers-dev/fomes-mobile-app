package com.appbee.appbeemobile.model;

public class User {
    private String userId;
    private String firstUsedDate;
    private String lastUsedDate;

    public User(String userId, String lastUsedDate) {
        this.userId = userId;
        this.lastUsedDate = lastUsedDate;
    }

    public User(String userId, String firstUsedDate, String lastUsedDate) {
        this.userId = userId;
        this.firstUsedDate = firstUsedDate;
        this.lastUsedDate = lastUsedDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstUsedDate() {
        return firstUsedDate;
    }

    public void setFirstUsedDate(String firstUsedDate) {
        this.firstUsedDate = firstUsedDate;
    }

    public String getLastUsedDate() {
        return lastUsedDate;
    }

    public void setLastUsedDate(String lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }
}

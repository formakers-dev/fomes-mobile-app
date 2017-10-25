package com.appbee.appbeemobile.model;

public class User {
    private String userId;
    private String name;
    private String email;
    private int maxAge;
    private int minAge;
    private int gender;
    private String registrationToken;

    public User(String userId, String email, int maxAge, int minAge, int gender, String registrationToken) {
        this.userId = userId;
        this.email = email;
        this.maxAge = maxAge;
        this.minAge = minAge;
        this.gender = gender;
        this.registrationToken = registrationToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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

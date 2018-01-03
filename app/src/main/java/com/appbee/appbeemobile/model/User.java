package com.appbee.appbeemobile.model;

public class User {
    private String userId;
    private String name;
    private String email;
    private Integer birthday;
    private String gender;
    private String registrationToken;

    public User() {
    }

    public User(String userId, String email, String registrationToken) {
        this.userId = userId;
        this.email = email;
        this.registrationToken = registrationToken;
    }

    public User(String registrationToken) {
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

    public Integer getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
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

package com.formakers.fomes.model;

import android.support.annotation.StringRes;

import com.formakers.fomes.R;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class User {
    private String userId;
    private String name;
    private String nickName;
    private String email;
    private Integer birthday;
    private String job;
    private String gender;
    private String registrationToken;
    private List<String> lifeApps;

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

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public User setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public Integer getBirthday() {
        return birthday;
    }

    public User setBirthday(Integer birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getJob() {
        return job;
    }

    public User setJob(String job) {
        this.job = job;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public User setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public User setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public User setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
        return this;
    }

    public List<String> getLifeApps() {
        return lifeApps;
    }

    public User setLifeApps(List<String> lifeApps) {
        this.lifeApps = lifeApps;
        return this;
    }

    public int getAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return ((currentYear - this.birthday) / 10) * 10;
    }

    public @StringRes int getGenderToStringResId() {
        return "male".equals(this.gender) ? R.string.common_male : R.string.common_female;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getUserId(), user.getUserId()) &&
                Objects.equals(getName(), user.getName()) &&
                Objects.equals(getNickName(), user.getNickName()) &&
                Objects.equals(getEmail(), user.getEmail()) &&
                Objects.equals(getBirthday(), user.getBirthday()) &&
                Objects.equals(getJob(), user.getJob()) &&
                Objects.equals(getGender(), user.getGender()) &&
                Objects.equals(getRegistrationToken(), user.getRegistrationToken()) &&
                Objects.equals(getLifeApps(), user.getLifeApps());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getName(), getNickName(), getEmail(), getBirthday(), getJob(), getGender(), getRegistrationToken(), getLifeApps());
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", job='" + job + '\'' +
                ", gender='" + gender + '\'' +
                ", registrationToken='" + registrationToken + '\'' +
                ", lifeApps=" + lifeApps +
                '}';
    }
}

package com.formakers.fomes.common.repository.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserRealmObject extends RealmObject {
    @PrimaryKey private String userId;
    private String name;
    private String nickName;
    private String email;
    private Integer birthday;
    private String job;
    private String gender;
    private RealmList<String> lifeApps;

    public String getUserId() {
        return userId;
    }

    public UserRealmObject setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserRealmObject setName(String name) {
        this.name = name;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public UserRealmObject setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRealmObject setEmail(String email) {
        this.email = email;
        return this;
    }

    public Integer getBirthday() {
        return birthday;
    }

    public UserRealmObject setBirthday(Integer birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getJob() {
        return job;
    }

    public UserRealmObject setJob(String job) {
        this.job = job;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public UserRealmObject setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public RealmList<String> getLifeApps() {
        return lifeApps;
    }

    public UserRealmObject setLifeApps(RealmList<String> lifeApps) {
        this.lifeApps = lifeApps;
        return this;
    }

    @Override
    public String toString() {
        return "UserRealmObject{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", job='" + job + '\'' +
                ", gender='" + gender + '\'' +
                ", lifeApps=" + lifeApps +
                '}';
    }
}

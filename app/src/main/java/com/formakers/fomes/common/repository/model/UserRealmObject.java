package com.formakers.fomes.common.repository.model;

import com.formakers.fomes.common.util.Log;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.annotations.PrimaryKey;

public class UserRealmObject extends RealmObject {

    public static final String TAG = "UserRealmObject";

    // 수정금지
    @PrimaryKey private Integer id = 1;

    private String name;
    private String nickName;
    private String email;
    private Integer birthday;
    private Integer job;
    private String gender;
    private RealmList<String> lifeApps;

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

    public Integer getJob() {
        return job;
    }

    public UserRealmObject setJob(Integer job) {
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

    public static void migration(RealmSchema realmSchema, long oldVersion, long newVersion) {
        Log.d(TAG, "migration) oldVersion=" + oldVersion + ", newVersion=" + newVersion);

        RealmObjectSchema schema = realmSchema.get(TAG);

        if (oldVersion <= 0) {
            schema.removePrimaryKey().removeField("userId");
            schema.addPrimaryKey("id");
        }
    }

    @Override
    public String toString() {
        return "UserRealmObject{" +
                "name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", job='" + job + '\'' +
                ", gender='" + gender + '\'' +
                ", lifeApps=" + lifeApps +
                '}';
    }
}

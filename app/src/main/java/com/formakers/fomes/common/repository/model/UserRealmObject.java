package com.formakers.fomes.common.repository.model;

import com.formakers.fomes.common.util.Log;

import io.realm.FieldAttribute;
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

    private String monthlyPayment;
    private RealmList<String> favoritePlatforms;
    private RealmList<String> favoriteGenres;
    private RealmList<String> leastFavoriteGenres;
    private RealmList<String> feedbackStyles;

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

    public String getMonthlyPayment() {
        return monthlyPayment;
    }

    public UserRealmObject setMonthlyPayment(String monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
        return this;
    }

    public RealmList<String> getFavoritePlatforms() {
        return favoritePlatforms;
    }

    public UserRealmObject setFavoritePlatforms(RealmList<String> favoritePlatforms) {
        this.favoritePlatforms = favoritePlatforms;
        return this;
    }

    public RealmList<String> getFavoriteGenres() {
        return favoriteGenres;
    }

    public UserRealmObject setFavoriteGenres(RealmList<String> favoriteGenres) {
        this.favoriteGenres = favoriteGenres;
        return this;
    }

    public RealmList<String> getLeastFavoriteGenres() {
        return leastFavoriteGenres;
    }

    public UserRealmObject setLeastFavoriteGenres(RealmList<String> leastFavoriteGenres) {
        this.leastFavoriteGenres = leastFavoriteGenres;
        return this;
    }

    public RealmList<String> getFeedbackStyles() {
        return feedbackStyles;
    }

    public UserRealmObject setFeedbackStyles(RealmList<String> feedbackStyles) {
        this.feedbackStyles = feedbackStyles;
        return this;
    }

    public static void migration(RealmSchema realmSchema, long oldVersion, long newVersion) {
        Log.d(TAG, "migration) oldVersion=" + oldVersion + ", newVersion=" + newVersion);

        RealmObjectSchema schema = realmSchema.get(TAG);

        if (oldVersion <= 0) {
            schema.removePrimaryKey().removeField("userId");
            schema.addField("id", Integer.class, FieldAttribute.PRIMARY_KEY);
        }

        if (oldVersion == 1) {
            schema.addField("monthlyPayment", String.class);
            schema.addRealmListField("favoritePlatforms", String.class);
            schema.addRealmListField("favoriteGenres", String.class);
            schema.addRealmListField("leastFavoriteGenres", String.class);
            schema.addRealmListField("feedbackStyles", String.class);
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
                ", monthlyPayment='" + monthlyPayment + '\'' +
                ", favoritePlatforms=" + favoritePlatforms +
                ", favoriteGenres=" + favoriteGenres +
                ", leastFavoriteGenres=" + leastFavoriteGenres +
                ", feedbackStyles=" + feedbackStyles +
                '}';
    }
}

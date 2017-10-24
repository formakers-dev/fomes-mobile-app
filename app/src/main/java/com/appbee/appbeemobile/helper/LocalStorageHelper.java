package com.appbee.appbeemobile.helper;


import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocalStorageHelper {

    private static final String NAME = "APP_BEE_SHARED_PREFERENCES";
    private static final String KEY_ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_REGISTRATION_TOKEN = "REGISTRATION_TOKEN";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_MAX_AGE = "MAX_AGE";
    private static final String KEY_MIN_AGE = "MIN_AGE";
    private static final String KEY_GENDER = "GENDER";

    private SharedPreferences sf;

    @Inject
    public LocalStorageHelper(Context context) {
        this.sf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private String getString(String key, String defaultValue) {
        return sf.getString(key, defaultValue);
    }

    private long getLong(String key, long defaultValue) {
        return sf.getLong(key, defaultValue);
    }
    private int getInt(String key, int defaultValue) {
        return sf.getInt(key, defaultValue);
    }

    private void putString(String key, String value) {
        SharedPreferences.Editor edit = sf.edit();
        edit.putString(key, value);
        edit.apply();
    }

    private void putLong(String key, long value) {
        SharedPreferences.Editor edit = sf.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    private void putInt(String key, int value) {
        SharedPreferences.Editor edit = sf.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public void setAccessToken(String accessToken) {
        putString(KEY_ACCESS_TOKEN, accessToken);
    }

    public String getAccessToken() {
        return getString(KEY_ACCESS_TOKEN, "");
    }

    public void setUserId(String userId) {
        putString(KEY_USER_ID, userId);
    }

    public String getUserId(){
        return getString(KEY_USER_ID, "");
    }

    public void setRegistrationToken(String registrationToken) {
        putString(KEY_REGISTRATION_TOKEN, registrationToken);
    }

    public String getRegistrationToken() {
        return getString(KEY_REGISTRATION_TOKEN, "");
    }


    public String getEmail() {
        return getString(KEY_EMAIL, "");
    }

    public void setEmail(String email) {
        putString(KEY_EMAIL, email);
    }

    public void setMaxAge(int maxAge) {
        putInt(KEY_MAX_AGE, maxAge);
    }

    public void setMinAge(int minAge) {
        putInt(KEY_MIN_AGE, minAge);
    }

    public void setGender(int gender) {
        putInt(KEY_GENDER, gender);
    }

    public int getMinAge() {
        return getInt(KEY_MIN_AGE, 0);
    }

    public int getMaxAge() {
        return getInt(KEY_MAX_AGE, 0);
    }

    public int getGender() {
        return getInt(KEY_GENDER, -1);
    }
}

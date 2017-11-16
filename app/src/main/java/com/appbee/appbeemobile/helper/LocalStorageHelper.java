package com.appbee.appbeemobile.helper;


import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocalStorageHelper {

    private static final String EMPTY_STRING = "";
    private static final String NAME = "APP_BEE_SHARED_PREFERENCES";
    private static final String KEY_ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_REGISTRATION_TOKEN = "REGISTRATION_TOKEN";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_GENDER = "GENDER";
    private static final String KEY_BIRTHDAY = "BIRTHDAY";
    private static final String KEY_LAST_UPDATE_STAT_TIMESTAMP = "LAST_UPDATE_STAT_TIMESTAMP";

    private SharedPreferences sf;

    @Inject
    public LocalStorageHelper(Context context) {
        this.sf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private String getString(String key, String defaultValue) {
        return sf.getString(key, defaultValue);
    }

    private int getInt(String key, int defaultValue) {
        return sf.getInt(key, defaultValue);
    }
    private long getLong(String key, long defaultValue) {
        return sf.getLong(key, defaultValue);
    }

    private void putString(String key, String value) {
        SharedPreferences.Editor edit = sf.edit();
        edit.putString(key, value);
        edit.apply();
    }

    private void putInt(String key, int value) {
        SharedPreferences.Editor edit = sf.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    private void putLong(String key, long value) {
        SharedPreferences.Editor edit = sf.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public void setAccessToken(String accessToken) {
        putString(KEY_ACCESS_TOKEN, accessToken);
    }

    public String getAccessToken() {
        return getString(KEY_ACCESS_TOKEN, EMPTY_STRING);
    }

    public void setUserId(String userId) {
        putString(KEY_USER_ID, userId);
    }

    public String getUserId(){
        return getString(KEY_USER_ID, EMPTY_STRING);
    }

    public void setRegistrationToken(String registrationToken) {
        putString(KEY_REGISTRATION_TOKEN, registrationToken);
    }

    public String getRegistrationToken() {
        return getString(KEY_REGISTRATION_TOKEN, EMPTY_STRING);
    }


    public String getEmail() {
        return getString(KEY_EMAIL, EMPTY_STRING);
    }

    public void setEmail(String email) {
        putString(KEY_EMAIL, email);
    }

    public void setGender(String gender) {
        putString(KEY_GENDER, gender);
    }

    public String getGender() {
        return getString(KEY_GENDER, EMPTY_STRING);
    }

    public int getBirthday() {
        return getInt(KEY_BIRTHDAY, 0);
    }

    public void setBirthday(int birthday) {
        putInt(KEY_BIRTHDAY, birthday);
    }

    public long getLastUpdateStatTimestamp() {
        return getLong(KEY_LAST_UPDATE_STAT_TIMESTAMP, 0L);
    }

    public void setLastUpdateStatTimestamp(long timestamp) {
        putLong(KEY_LAST_UPDATE_STAT_TIMESTAMP, timestamp);
    }
}

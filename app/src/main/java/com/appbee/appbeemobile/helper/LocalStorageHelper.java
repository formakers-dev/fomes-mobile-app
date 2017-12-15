package com.appbee.appbeemobile.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

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
    private static final String KEY_LAST_UPDATE_STAT_TIMESTAMP = "LAST_UPDATE_STAT_TIMESTAMP";
    private static final String KEY_INVITATION_CODE = "INVITATION_CODE";

    private SharedPreferences sharedPreferences;

    @Inject
    public LocalStorageHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    private long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    private void putString(String key, String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    private void putLong(String key, long value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public void setAccessToken(String accessToken) {
        putString(KEY_ACCESS_TOKEN, accessToken);
    }

    @NonNull
    public String getAccessToken() {
        return getString(KEY_ACCESS_TOKEN, EMPTY_STRING);
    }

    public void setUserId(String userId) {
        putString(KEY_USER_ID, userId);
    }

    @NonNull
    public String getUserId() {
        return getString(KEY_USER_ID, EMPTY_STRING);
    }

    public void setRegistrationToken(String registrationToken) {
        putString(KEY_REGISTRATION_TOKEN, registrationToken);
    }

    @NonNull
    public String getRegistrationToken() {
        return getString(KEY_REGISTRATION_TOKEN, EMPTY_STRING);
    }

    @NonNull
    public String getEmail() {
        return getString(KEY_EMAIL, EMPTY_STRING);
    }

    public void setEmail(String email) {
        putString(KEY_EMAIL, email);
    }

    public long getLastUpdateStatTimestamp() {
        return getLong(KEY_LAST_UPDATE_STAT_TIMESTAMP, 0L);
    }

    public void setLastUpdateStatTimestamp(long timestamp) {
        putLong(KEY_LAST_UPDATE_STAT_TIMESTAMP, timestamp);
    }

    @NonNull
    public String getInvitationCode() {
        return getString(KEY_INVITATION_CODE, EMPTY_STRING);
    }

    public void setInvitationCode(String code) {
        putString(KEY_INVITATION_CODE, code);
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getAccessToken());
    }
}

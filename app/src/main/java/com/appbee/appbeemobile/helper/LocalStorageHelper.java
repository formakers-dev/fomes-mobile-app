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
}

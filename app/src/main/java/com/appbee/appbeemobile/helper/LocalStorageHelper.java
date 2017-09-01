package com.appbee.appbeemobile.helper;


import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocalStorageHelper {

    private static final String NAME = "APP_BEE_SHARED_PREFERENCES";
    private static final String KEY_LAST_USAGE_TIME = "LAST_USAGE_TIME";
    private static final String KEY_ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String KEY_MIN_STARTED_STAT_TIME_STAMP = "MIN_STARTED_STAT_TIME_STAMP";
    private static final String KEY_UUID = "UUID";

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

    public void setLastUsageTime(long lastUsageTime) {
        putLong(KEY_LAST_USAGE_TIME, lastUsageTime);
    }

    public long getLastUsageTime() {
        return getLong(KEY_LAST_USAGE_TIME, 0L);
    }

    public void setMinStartedStatTimeStamp(long minStartedStatTimeStamp) {
        putLong(KEY_MIN_STARTED_STAT_TIME_STAMP, minStartedStatTimeStamp);
    }

    public long getMinStartedStatTimeStamp() {
        return getLong(KEY_MIN_STARTED_STAT_TIME_STAMP, 0L);
    }

    public void setUUID(String uuid) {
        putString(KEY_UUID, uuid);
    }

    public String getUUID(){
        return getString(KEY_UUID, "");
    }
}

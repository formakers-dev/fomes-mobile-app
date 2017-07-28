package com.appbee.appbeemobile.util;


import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PropertyUtil {

    private SharedPreferences sf;

    @Inject
    public PropertyUtil(Context context) {
        this.sf = context.getSharedPreferences(AppBeeConstants.SharedPreference.NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key, String defaultValue) {
        return sf.getString(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return sf.getLong(key, defaultValue);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor edit = sf.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor edit = sf.edit();
        edit.putLong(key, value);
        edit.apply();
    }
}

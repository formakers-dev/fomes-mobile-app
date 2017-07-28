package com.appbee.appbeemobile.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.appbee.appbeemobile.R;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PropertyUtil {

    private Context context;
    private SharedPreferences sf;

    @Inject
    public PropertyUtil(Context context) {
        this.context = context;
        this.sf = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
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

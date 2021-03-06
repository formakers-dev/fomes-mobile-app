package com.formakers.fomes.common.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPreferencesHelper {

    public static final String TAG = SharedPreferencesHelper.class.getSimpleName();

    private static final String EMPTY_STRING = "";
    private static final long DEFAULT_LONG = 0L;
    private static final int DEFAULT_INT = 0;

    private static final String NAME = "FOMES_SHARED_PREFERENCES";

    private static final String KEY_ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_REGISTRATION_TOKEN = "REGISTRATION_TOKEN";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_NICK_NAME = "NICK_NAME";
    private static final String KEY_LAST_UPDATE_APP_USAGE_TIMESTAMP = "LAST_UPDATE_STAT_TIMESTAMP";
    private static final String KEY_LAST_UPDATE_SHORT_TERM_STAT_TIMESTAMP = "LAST_UPDATE_SHORT_TERM_STAT_TIMESTAMP";
    private static final String KEY_MIGRATION_NOTICE_VERSION = "MIGRATION_NOTICE_VERSION";

    private static final String KEY_PROVISIONING_PROGRESS_STATUS = "PROVISIONING_PROGRESS_STATUS";
    private static final String KEY_OLD_LATEST_MIGRATION_VERSION = "OLD_LATEST_MIGRATION_VERSION";

    private static final String KEY_SETTING_NOTIFICATION = "KEY_SETTING_NOTIFICATION";

    private SharedPreferences sharedPreferences;

    @Inject
    public SharedPreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private void reset(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    private String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    private long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    private int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    private void putString(String key, String value) {
        Log.v(TAG, "putString) key=" + key + ", value=" + value);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    private void putLong(String key, long value) {
        Log.v(TAG, "putLong) key=" + key + ", value=" + value);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    private void putInt(String key, int value) {
        Log.v(TAG, "putInt) key=" + key + ", value=" + value);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    private void putBoolean(String key, boolean value) {
        Log.v(TAG, "putBoolean) key=" + key + ", value=" + value);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public void setAccessToken(String accessToken) {
        putString(KEY_ACCESS_TOKEN, accessToken);
    }

    @NonNull
    public String getAccessToken() {
        return getString(KEY_ACCESS_TOKEN, EMPTY_STRING);
    }

    public void setUserEmail(String email) {
        putString(KEY_EMAIL, email);
    }

    public String getUserEmail() {
        return getString(KEY_EMAIL, "");
    }

    public void setUserNickName(String nickName) {
        putString(KEY_NICK_NAME, nickName);
    }

    public String getUserNickName() {
        return getString(KEY_NICK_NAME, "");
    }

    public void setUserRegistrationToken(String registrationToken) {
        putString(KEY_REGISTRATION_TOKEN, registrationToken);
    }

    @NonNull
    public String getUserRegistrationToken() {
        return getString(KEY_REGISTRATION_TOKEN, EMPTY_STRING);
    }

    public long getLastUpdateAppUsageTimestamp() {
        return getLong(KEY_LAST_UPDATE_APP_USAGE_TIMESTAMP, DEFAULT_LONG);
    }

    public void setLastUpdateAppUsageTimestamp(long timestamp) {
        putLong(KEY_LAST_UPDATE_APP_USAGE_TIMESTAMP, timestamp);
    }

    public long getLastUpdateShortTermStatTimestamp() {
        return getLong(KEY_LAST_UPDATE_SHORT_TERM_STAT_TIMESTAMP, DEFAULT_LONG);
    }

    public void setLastUpdateShortTermStatTimestamp(long timestamp) {
        putLong(KEY_LAST_UPDATE_SHORT_TERM_STAT_TIMESTAMP, timestamp);
    }

    public boolean hasAccessToken() {
        return !TextUtils.isEmpty(getAccessToken());
    }

    public int getProvisioningProgressStatus() {
        return getInt(KEY_PROVISIONING_PROGRESS_STATUS, FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);
    }

    public void setProvisioningProgressStatus(int status) {
        int currentStatus = getProvisioningProgressStatus();
        if (currentStatus == FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED) {
            Log.e(TAG, "You tried to set older progress status (" + status + ") "
                    + "although you already have completed Provisioning Flow (" + currentStatus + "). "
                    + "It will be not set. Please check it.");
            return;
        }

        if (status <= FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED || status >= currentStatus) {
            putInt(KEY_PROVISIONING_PROGRESS_STATUS, status);
        } else {
            Log.e(TAG, "You tried to set older progress status (" + status + ") than " +
                    "current progress status (" + currentStatus +"). It will be not set. Please check it.");
        }
    }

    public void resetProvisioningProgressStatus() {
        this.reset(KEY_PROVISIONING_PROGRESS_STATUS);
    }

    public int getMigrationNoticeVersion() {
        return getInt(KEY_MIGRATION_NOTICE_VERSION, -1);
    }

    public void setMigrationNoticeVersion(int version) {
        putInt(KEY_MIGRATION_NOTICE_VERSION, version);
    }

    public boolean getSettingNotificationTopic(String topic) {
        return getBoolean(KEY_SETTING_NOTIFICATION + topic, true);
    }

    public void setSettingNotificationTopic(String topic, boolean isSubscribed) {
        putBoolean(KEY_SETTING_NOTIFICATION + topic, isSubscribed);
    }
}

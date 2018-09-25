package com.formakers.fomes.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.formakers.fomes.util.FomesConstants;

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
    private static final String KEY_LAST_UPDATE_APP_USAGE_TIMESTAMP = "LAST_UPDATE_STAT_TIMESTAMP";
    private static final String KEY_LAST_UPDATE_SHORT_TERM_STAT_TIMESTAMP = "LAST_UPDATE_SHORT_TERM_STAT_TIMESTAMP";

    private static final String KEY_PROVISIONING_PROGRESS_STATUS = "PROVISIONING_PROGRESS_STATUS";

    @Deprecated
    private static final String KEY_INVITATION_CODE = "INVITATION_CODE";

    private SharedPreferences sharedPreferences;

    @Inject
    public SharedPreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
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

    @Deprecated
    @NonNull
    public String getInvitationCode() {
        return getString(KEY_INVITATION_CODE, EMPTY_STRING);
    }

    @Deprecated
    public void setInvitationCode(String code) {
        putString(KEY_INVITATION_CODE, code);
    }

    public boolean isLoggedIn() {
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
}

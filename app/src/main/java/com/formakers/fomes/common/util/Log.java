package com.formakers.fomes.common.util;

import com.formakers.fomes.BuildConfig;

public class Log {
    private static final String APP_NAME = "FOMES";
    private static final String LOG_FORMAT = "[%s] %s";

    public static void v(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(APP_NAME, String.format(LOG_FORMAT, TAG, message));
        }
    }

    public static void d(String TAG, String message) {
        android.util.Log.d(APP_NAME, String.format(LOG_FORMAT, TAG, message));
    }

    public static void i(String TAG, String message) {
        android.util.Log.i(APP_NAME, String.format(LOG_FORMAT, TAG, message));
    }

    public static void w(String TAG, String message) {
        android.util.Log.w(APP_NAME, String.format(LOG_FORMAT, TAG, message));
    }

    public static void e(String TAG, String message) {
        android.util.Log.e(APP_NAME, String.format(LOG_FORMAT, TAG, message));
    }

    public static void wtf(String TAG, String message) {
        android.util.Log.wtf(APP_NAME, String.format(LOG_FORMAT, TAG, message));
    }
}

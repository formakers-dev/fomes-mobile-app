package com.formakers.fomes.model;

import android.graphics.drawable.Drawable;

public class NativeAppInfo {
    private String packageName;
    private String appName;
    private Drawable icon;

    public NativeAppInfo(String packageName, String appName) {
        this.packageName = packageName;
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public NativeAppInfo setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public NativeAppInfo setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public Drawable getIcon() {
        return icon;
    }

    public NativeAppInfo setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }
}

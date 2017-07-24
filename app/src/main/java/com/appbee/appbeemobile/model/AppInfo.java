package com.appbee.appbeemobile.model;

public class AppInfo {
    String packageName;
    String appName;

    public AppInfo(String resolvePackageName, String appName) {
        this.packageName = resolvePackageName;
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}

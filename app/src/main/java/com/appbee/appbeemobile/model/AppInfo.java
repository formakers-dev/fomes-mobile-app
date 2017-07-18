package com.appbee.appbeemobile.model;

public class AppInfo {
    String pakageName;
    String appName;

    public AppInfo(String resolvePackageName, String appName) {
        this.pakageName = resolvePackageName;
        this.appName = appName;
    }

    public String getPakageName() {
        return pakageName;
    }

    public void setPakageName(String pakageName) {
        this.pakageName = pakageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}

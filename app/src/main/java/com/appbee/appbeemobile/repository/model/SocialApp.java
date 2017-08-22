package com.appbee.appbeemobile.repository.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SocialApp extends RealmObject {
    @PrimaryKey
    private String packageName;
    private String appName;

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

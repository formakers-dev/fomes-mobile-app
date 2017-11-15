package com.appbee.appbeemobile.repository.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AppUsageRealmObject extends RealmObject {
    @PrimaryKey
    private String packageName;
    private long totalUsedTime;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTotalUsedTime() {
        return totalUsedTime;
    }

    public void setTotalUsedTime(long totalUsedTime) {
        this.totalUsedTime = totalUsedTime;
    }
}

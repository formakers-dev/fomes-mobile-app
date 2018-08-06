package com.appbee.appbeemobile.repository.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class AppUsageRealmObject extends RealmObject {

    @PrimaryKey
    private String appUsageKey;
    private String packageName;

    @Index
    private int yyyymmdd;
    private long totalUsedTime;

    public String getAppUsageKey() {
        return appUsageKey;
    }

    public void setAppUsageKey(String appUsageKey) {
        this.appUsageKey = appUsageKey;
    }

    public int getYyyymmdd() {
        return yyyymmdd;
    }

    public void setYyyymmdd(int yyyymmdd) {
        this.yyyymmdd = yyyymmdd;
    }

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

package com.appbee.appbeemobile.model;

public class DailyUsageStat {
    private String packageName;
    private long lastUsedDateTime;
    private long totalUsedTime;

    public DailyUsageStat(String packageName, long lastUsedDateTime, long totalUsedTime) {
        setPackageName(packageName);
        setLastUsedDateTime(lastUsedDateTime);
        setTotalUsedTime(totalUsedTime);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getLastUsedDateTime() {
        return lastUsedDateTime;
    }

    public void setLastUsedDateTime(long lastUsedDateTime) {
        this.lastUsedDateTime = lastUsedDateTime;
    }

    public long getTotalUsedTime() {
        return totalUsedTime;
    }

    public void setTotalUsedTime(long totalUsedTime) {
        this.totalUsedTime = totalUsedTime;
    }
}

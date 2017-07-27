package com.appbee.appbeemobile.model;

public class LongTermStat {
    private String packageName;
    private String lastUsedDate;
    private long totalUsedTime;

    public LongTermStat(String packageName, String lastUsedDate, long totalUsedTime) {
        setPackageName(packageName);
        setLastUsedDate(lastUsedDate);
        setTotalUsedTime(totalUsedTime);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLastUsedDate() {
        return lastUsedDate;
    }

    public void setLastUsedDate(String lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    public long getTotalUsedTime() {
        return totalUsedTime;
    }

    public void setTotalUsedTime(long totalUsedTime) {
        this.totalUsedTime = totalUsedTime;
    }
}

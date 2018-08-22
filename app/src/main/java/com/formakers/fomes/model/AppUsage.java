package com.formakers.fomes.model;

public class AppUsage {
    String packageName;
    long totalUsedTime;

    public AppUsage(String packageName, long totalUsedTime) {
        this.packageName = packageName;
        this.totalUsedTime = totalUsedTime;
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

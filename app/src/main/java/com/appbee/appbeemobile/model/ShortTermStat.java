package com.appbee.appbeemobile.model;

public class ShortTermStat {
    private String packageName;
    private long startTimestamp;
    private long endTimestamp;
    private long totalUsedTime;

    public ShortTermStat(String packageName, long startTimestamp, long endTimeStamp, long totalUsedTime) {
        this.packageName = packageName;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimeStamp;
        this.totalUsedTime = totalUsedTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public long getTotalUsedTime() {
        return totalUsedTime;
    }

    public void setTotalUsedTime(long totalUsedTime) {
        this.totalUsedTime = totalUsedTime;
    }
}

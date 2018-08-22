package com.formakers.fomes.model;

public class ShortTermStat {
    private String packageName;
    private long startTimeStamp;
    private long endTimeStamp;
    private long totalUsedTime;

    public ShortTermStat(String packageName, long startTimeStamp, long endTimeStamp, long totalUsedTime) {
        this.packageName = packageName;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.totalUsedTime = totalUsedTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public long getTotalUsedTime() {
        return totalUsedTime;
    }

    public void setTotalUsedTime(long totalUsedTime) {
        this.totalUsedTime = totalUsedTime;
    }
}

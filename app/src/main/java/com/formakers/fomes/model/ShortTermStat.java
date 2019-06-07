package com.formakers.fomes.model;

public class ShortTermStat {
    private String packageName;
    private long startTimeStamp;
    private long endTimeStamp;
    private long totalUsedTime;
    private String versionName;

    public ShortTermStat(String packageName, long startTimeStamp, long endTimeStamp, long totalUsedTime) {
        this.packageName = packageName;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.totalUsedTime = totalUsedTime;
    }

    public ShortTermStat(String packageName, long startTimeStamp, long endTimeStamp) {
        this.packageName = packageName;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.totalUsedTime = endTimeStamp - startTimeStamp;
    }

    public String getPackageName() {
        return packageName;
    }

    public ShortTermStat setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public ShortTermStat setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
        return this;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    public ShortTermStat setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
        return this;
    }

    public long getTotalUsedTime() {
        return totalUsedTime;
    }

    public ShortTermStat setTotalUsedTime(long totalUsedTime) {
        this.totalUsedTime = totalUsedTime;
        return this;
    }

    public String getVersionName() {
        return versionName;
    }

    public ShortTermStat setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    @Override
    public String toString() {
        return "ShortTermStat{" +
                "packageName='" + packageName + '\'' +
                ", startTimeStamp=" + startTimeStamp +
                ", endTimeStamp=" + endTimeStamp +
                ", totalUsedTime=" + totalUsedTime +
                ", versionName='" + versionName + '\'' +
                '}';
    }
}

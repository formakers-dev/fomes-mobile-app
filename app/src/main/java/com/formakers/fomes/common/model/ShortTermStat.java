package com.formakers.fomes.common.model;

public class ShortTermStat {
    private final String packageName;
    private final long startTimeStamp;
    private final long endTimeStamp;
    private long totalUsedTime;
    private String versionName;
    private User.DeviceInfo device = new User.DeviceInfo();

    @Deprecated
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

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
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
                ", device=" + device +
                '}';
    }
}

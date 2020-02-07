package com.formakers.fomes.common.model;

public class ShortTermStat {
    private final String packageName;
    private final long startTimeStamp;
    private final long endTimeStamp;
    private String versionName;
    private User.DeviceInfo device = new User.DeviceInfo();

    public ShortTermStat(String packageName, long startTimeStamp, long endTimeStamp) {
        this.packageName = packageName;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
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
        return endTimeStamp - startTimeStamp;
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
                ", versionName='" + versionName + '\'' +
                ", device=" + device +
                '}';
    }
}

package com.appbee.appbeemobile.model;

import android.app.usage.UsageStats;

public class NativeLongTermStat {
    private String packageName;
    private long beginTimeStamp;
    private long endTimeStamp;
    private long lastTimeUsed;
    private long totalTimeInForeground;

    public NativeLongTermStat(String packageName, long beginTimeStamp, long endTimeStamp, long lastTimeUsed, long totalTimeInForeground) {
        this.packageName = packageName;
        this.beginTimeStamp = beginTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.lastTimeUsed = lastTimeUsed;
        this.totalTimeInForeground = totalTimeInForeground;
    }

    public NativeLongTermStat(UsageStats usageStats) {
        this.packageName = usageStats.getPackageName();
        this.beginTimeStamp = usageStats.getFirstTimeStamp();
        this.endTimeStamp = usageStats.getLastTimeStamp();
        this.lastTimeUsed = usageStats.getLastTimeUsed();
        this.totalTimeInForeground = usageStats.getTotalTimeInForeground();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getBeginTimeStamp() {
        return beginTimeStamp;
    }

    public void setBeginTimeStamp(long beginTimeStamp) {
        this.beginTimeStamp = beginTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public long getTotalTimeInForeground() {
        return totalTimeInForeground;
    }

    public void setTotalTimeInForeground(long totalTimeInForeground) {
        this.totalTimeInForeground = totalTimeInForeground;
    }
}

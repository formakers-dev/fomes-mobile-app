package com.appbee.appbeemobile.model;

import com.google.gson.annotations.SerializedName;

public class DetailUsageStat {
    @SerializedName("packageName")
    private String packageName;
    @SerializedName("startTimeStamp")
    private long startTimeStamp;
    @SerializedName("endTimeStamp")
    private long endTimeStamp;
    @SerializedName("totalUsedTime")
    private long totalUsedTime;

    public DetailUsageStat(String packageName, long startTimeStamp, long endTimeStamp, long totalUsedTime) {
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

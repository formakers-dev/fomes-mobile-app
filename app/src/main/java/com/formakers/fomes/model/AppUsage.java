package com.formakers.fomes.model;

import java.util.Date;

public class AppUsage {
    Date date;
    String packageName;
    long totalUsedTime;
    AppInfo appInfo;

    public AppUsage(Date date, String packageName, long totalUsedTime, AppInfo appInfo) {
        this.date = date;
        this.packageName = packageName;
        this.totalUsedTime = totalUsedTime;
        this.appInfo = appInfo;
    }

    public AppUsage(String packageName, long totalUsedTime) {
        this.packageName = packageName;
        this.totalUsedTime = totalUsedTime;
    }

    public Date getDate() {
        return date;
    }

    public AppUsage setDate(Date date) {
        this.date = date;
        return this;
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

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    @Override
    public String toString() {
        return "AppUsage{" +
                "date=" + date +
                ", packageName='" + packageName + '\'' +
                ", totalUsedTime=" + totalUsedTime +
                ", appInfo=" + appInfo +
                '}';
    }
}

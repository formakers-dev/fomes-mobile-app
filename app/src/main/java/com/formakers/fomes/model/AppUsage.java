package com.formakers.fomes.model;

import com.formakers.fomes.common.util.DateUtil;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUsage {
    Date date;
    String packageName;
    long totalUsedTime;
    String appVersion;
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

    // ShortTermStats 사용시간 누적 (리듀스) 해서 AppUsage로 바꿈
    public static List<AppUsage> createListFromShortTermStats(List<ShortTermStat> shortTermStats) {
        Map<String, AppUsage> map = new HashMap<>();

        String key;
        AppUsage value;

        for (ShortTermStat shortTermStat : shortTermStats) {
            // 날짜 + 패키지네임
            key = DateUtil.getDateStringFromTimestamp(shortTermStat.getStartTimeStamp())
                    + shortTermStat.getPackageName();

            if (map.containsKey(key)) {
                value = map.get(key);
                value.setTotalUsedTime(value.getTotalUsedTime() + shortTermStat.getTotalUsedTime());
            } else {
                value = new AppUsage(shortTermStat.getPackageName(), shortTermStat.getTotalUsedTime())
                        .setDate(DateUtil.getDateWithoutTime(new Date(shortTermStat.getStartTimeStamp())))
                        .setAppVersion(shortTermStat.getVersionName());
            }
            map.put(key, value);
        }

        return Lists.newArrayList(map.values());
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

    public String getAppVersion() {
        return appVersion;
    }

    public AppUsage setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
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
                ", appVersion='" + appVersion + '\'' +
                ", appInfo=" + appInfo +
                '}';
    }
}

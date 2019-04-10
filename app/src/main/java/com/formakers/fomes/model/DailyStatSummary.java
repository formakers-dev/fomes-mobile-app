package com.formakers.fomes.model;

@Deprecated
public class DailyStatSummary {
    private String packageName;
    private int yyyymmdd;
    private long totalUsedTime;

    public DailyStatSummary(String packageName, int yyyymmdd, long totalUsedTime) {
        this.packageName = packageName;
        this.yyyymmdd = yyyymmdd;
        this.totalUsedTime = totalUsedTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getYyyymmdd() {
        return yyyymmdd;
    }

    public void setYyyymmdd(int yyyymmdd) {
        this.yyyymmdd = yyyymmdd;
    }

    public long getTotalUsedTime() {
        return totalUsedTime;
    }

    public void setTotalUsedTime(long totalUsedTime) {
        this.totalUsedTime = totalUsedTime;
    }
}

package com.formakers.fomes.common.network.vo;

import com.formakers.fomes.model.AppInfo;

import java.util.List;

public class Usage {
    String id;
    String name;
    Long totalUsedTime;
    List<AppInfo> appInfos;  // optional

    public Usage(String id, String name, Long totalUsedTime, List<AppInfo> appInfos) {
        this.id = id;
        this.name = name;
        this.totalUsedTime = totalUsedTime;
        this.appInfos = appInfos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotalUsedTime() {
        return totalUsedTime;
    }

    public void setTotalUsedTime(Long totalUsedTime) {
        this.totalUsedTime = totalUsedTime;
    }

    public List<AppInfo> getAppInfos() {
        return appInfos;
    }

    public void setAppInfos(List<AppInfo> appInfos) {
        this.appInfos = appInfos;
    }

    @Override
    public String toString() {
        return "Usage{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", totalUsedTime=" + totalUsedTime +
                ", appInfos=" + appInfos +
                '}';
    }
}
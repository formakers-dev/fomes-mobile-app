package com.formakers.fomes.common.network.vo;

import java.util.List;

public class UsageGroup {
    public static final int TYPE_ALL = 0xFFFFFFFF;
    public static final int TYPE_MINE = 0x00000001;
    public static final int TYPE_GENDER = 0x00000002;
    public static final int TYPE_AGE = 0x00000004;
    public static final int TYPE_JOB = 0x00000008;

    Integer groupType;
    List<Usage> appUsages;
    List<Usage> categoryUsages;
    List<Usage> developerUsages;

    public UsageGroup(Integer groupType, List<Usage> appUsages, List<Usage> categoryUsages, List<Usage> developerUsages) {
        this.groupType = groupType;
        this.appUsages = appUsages;
        this.categoryUsages = categoryUsages;
        this.developerUsages = developerUsages;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public List<Usage> getAppUsages() {
        return appUsages;
    }

    public void setAppUsages(List<Usage> appUsages) {
        this.appUsages = appUsages;
    }

    public List<Usage> getCategoryUsages() {
        return categoryUsages;
    }

    public void setCategoryUsages(List<Usage> categoryUsages) {
        this.categoryUsages = categoryUsages;
    }

    public List<Usage> getDeveloperUsages() {
        return developerUsages;
    }

    public void setDeveloperUsages(List<Usage> developerUsages) {
        this.developerUsages = developerUsages;
    }

    @Override
    public String toString() {
        return "UsageGroup{" +
                "groupType=" + groupType +
                ", appUsages=" + appUsages +
                ", categoryUsages=" + categoryUsages +
                ", developerUsages=" + developerUsages +
                '}';
    }
}
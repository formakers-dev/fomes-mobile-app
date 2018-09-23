package com.formakers.fomes.model;

public class AppInfo {
    private String packageName;
    private String appName;
    private String categoryId1;
    private String categoryName1;
    private String categoryId2;
    private String categoryName2;
    private String developer;
    private String iconUrl;
    private Long totalUsedTime;

    public AppInfo(String packageName, String appName) {
        this.packageName = packageName;
        this.appName = appName;
    }

    public AppInfo(String packageName, String appName, String categoryId1, String categoryName1, String categoryId2, String categoryName2, String iconUrl) {
        this.packageName = packageName;
        this.appName = appName;
        this.categoryId1 = categoryId1;
        this.categoryName1 = categoryName1;
        this.categoryId2 = categoryId2;
        this.categoryName2 = categoryName2;
        this.iconUrl = iconUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public AppInfo setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public AppInfo setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getCategoryId1() {
        return categoryId1;
    }

    public AppInfo setCategoryId1(String categoryId1) {
        this.categoryId1 = categoryId1;
        return this;
    }

    public String getCategoryName1() {
        return categoryName1;
    }

    public AppInfo setCategoryName1(String categoryName1) {
        this.categoryName1 = categoryName1;
        return this;
    }

    public String getCategoryId2() {
        return categoryId2;
    }

    public AppInfo setCategoryId2(String categoryId2) {
        this.categoryId2 = categoryId2;
        return this;
    }

    public String getCategoryName2() {
        return categoryName2;
    }

    public AppInfo setCategoryName2(String categoryName2) {
        this.categoryName2 = categoryName2;
        return this;
    }

    public String getDeveloper() {
        return developer;
    }

    public AppInfo setDeveloper(String developer) {
        this.developer = developer;
        return this;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public AppInfo setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public Long getTotalUsedTime() {
        return totalUsedTime;
    }

    public AppInfo setTotalUsedTime(Long totalUsedTime) {
        this.totalUsedTime = totalUsedTime;
        return this;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", categoryId1='" + categoryId1 + '\'' +
                ", categoryName1='" + categoryName1 + '\'' +
                ", categoryId2='" + categoryId2 + '\'' +
                ", categoryName2='" + categoryName2 + '\'' +
                ", developer='" + developer + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", totalUsedTime=" + totalUsedTime +
                '}';
    }
}

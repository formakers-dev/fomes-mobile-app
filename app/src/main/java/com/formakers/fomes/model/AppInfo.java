package com.formakers.fomes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AppInfo implements Parcelable {
    private String packageName;
    private String appName;
    private String categoryId1;
    private String categoryName1;
    private String categoryId2;
    private String categoryName2;
    private String developer;
    private String iconUrl;
    private Double star;
    private Integer installsMin;
    private Integer installsMax;
    private String contentsRating;
    private Long totalUsedTime;
    private List<String> imageUrls;

    public AppInfo(Parcel in) {
        readFromParcel(in);
    }

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

    public Double getStar() {
        return star;
    }

    public AppInfo setStar(Double star) {
        this.star = star;
        return this;
    }

    public Integer getInstallsMin() {
        return installsMin;
    }

    public AppInfo setInstallsMin(Integer installsMin) {
        this.installsMin = installsMin;
        return this;
    }

    public Integer getInstallsMax() {
        return installsMax;
    }

    public AppInfo setInstallsMax(Integer installsMax) {
        this.installsMax = installsMax;
        return this;
    }

    public String getContentsRating() {
        return contentsRating;
    }

    public AppInfo setContentsRating(String contentsRating) {
        this.contentsRating = contentsRating;
        return this;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public AppInfo setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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
                ", star=" + star +
                ", installsMin=" + installsMin +
                ", installsMax=" + installsMax +
                ", contentsRating='" + contentsRating + '\'' +
                ", totalUsedTime=" + totalUsedTime +
                ", imageUrls=" + imageUrls +
                '}';
    }

    /**
     * for Parcelable
     */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeString(appName);
        dest.writeString(categoryId1);
        dest.writeString(categoryName1);
        dest.writeString(categoryId2);
        dest.writeString(categoryName2);
        dest.writeString(developer);
        dest.writeString(iconUrl);
        dest.writeLong(totalUsedTime == null ? 0L : totalUsedTime);
    }

    private void readFromParcel(Parcel in) {
        packageName = in.readString();
        appName = in.readString();
        categoryId1 = in.readString();
        categoryName1 = in.readString();
        categoryId2 = in.readString();
        categoryName2 = in.readString();
        developer = in.readString();
        iconUrl = in.readString();
        totalUsedTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };
}

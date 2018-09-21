package com.formakers.fomes.model;

@Deprecated
public class CategoryUsage {
    String categoryId;
    String categoryName;
    long totalUsedTime;

    public CategoryUsage(String categoryId, String categoryName, long totalUsedTime) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalUsedTime = totalUsedTime;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getTotalUsedTime() {
        return totalUsedTime;
    }

    public void setTotalUsedTime(long totalUsedTime) {
        this.totalUsedTime = totalUsedTime;
    }

    @Override
    public String toString() {
        return "CategoryUsage{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", totalUsedTime=" + totalUsedTime +
                '}';
    }
}

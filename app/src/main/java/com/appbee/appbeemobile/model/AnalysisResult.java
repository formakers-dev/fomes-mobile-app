package com.appbee.appbeemobile.model;

import java.util.List;

public class AnalysisResult {
    private String characterType;
    private int totalInstalledAppCount;
    private int averageUsedMinutesPerDay;
    private String mostUsedApp;
    private List<String> mostDownloadCategories;
    private String leastDownloadCategory;
    private List<String> mostUsedCategories;
    private String leastUsedCategory;

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }

    public int getTotalInstalledAppCount() {
        return totalInstalledAppCount;
    }

    public void setTotalInstalledAppCount(int totalInstalledAppCount) {
        this.totalInstalledAppCount = totalInstalledAppCount;
    }

    public int getAverageUsedMinutesPerDay() {
        return averageUsedMinutesPerDay;
    }

    public void setAverageUsedMinutesPerDay(int averageUsedMinutesPerDay) {
        this.averageUsedMinutesPerDay = averageUsedMinutesPerDay;
    }

    public String getMostUsedApp() {
        return mostUsedApp;
    }

    public void setMostUsedApp(String mostUsedApp) {
        this.mostUsedApp = mostUsedApp;
    }

    public List<String> getMostDownloadCategories() {
        return mostDownloadCategories;
    }

    public void setMostDownloadCategories(List<String> mostDownloadCategories) {
        this.mostDownloadCategories = mostDownloadCategories;
    }

    public String getLeastDownloadCategory() {
        return leastDownloadCategory;
    }

    public void setLeastDownloadCategory(String leastDownloadCategory) {
        this.leastDownloadCategory = leastDownloadCategory;
    }

    public List<String> getMostUsedCategories() {
        return mostUsedCategories;
    }

    public void setMostUsedCategories(List<String> mostUsedCategories) {
        this.mostUsedCategories = mostUsedCategories;
    }

    public String getLeastUsedCategory() {
        return leastUsedCategory;
    }

    public void setLeastUsedCategory(String leastUsedCategory) {
        this.leastUsedCategory = leastUsedCategory;
    }
}

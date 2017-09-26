package com.appbee.appbeemobile.model;

public class OverviewInfo {
    private int averageUsedMinutesPerDay;
    private String mostUsedApp;

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
}

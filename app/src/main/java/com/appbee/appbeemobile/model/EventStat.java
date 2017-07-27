package com.appbee.appbeemobile.model;

public class EventStat {
    private String packageName;
    private int eventType;
    private long timeStamp;

    public EventStat(String packageName, int eventType, long timeStamp) {
        this.packageName = packageName;
        this.eventType = eventType;
        this.timeStamp = timeStamp;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}

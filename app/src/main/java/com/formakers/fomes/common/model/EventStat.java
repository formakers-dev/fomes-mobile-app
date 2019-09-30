package com.formakers.fomes.common.model;

public class EventStat {
    private String packageName;
    private int eventType;  // background, foreground etc.....
    private long eventTime;

    public EventStat(String packageName, int eventType, long eventTime) {
        this.packageName = packageName;
        this.eventType = eventType;
        this.eventTime = eventTime;
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

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
}

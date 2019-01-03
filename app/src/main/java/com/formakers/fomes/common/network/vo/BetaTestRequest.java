package com.formakers.fomes.common.network.vo;

import java.util.Date;
import java.util.List;

public class BetaTestRequest {
    String title;
    String subTitle;

    String type;
    List<String> typeTags;

    Date openDate;
    Date closeDate;

    List<String> apps;

    String actionType;
    String action;

    boolean isOpened;
    boolean isRegistered;

    public BetaTestRequest() {
    }

    public String getTitle() {
        return title;
    }

    public BetaTestRequest setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public BetaTestRequest setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getType() {
        return type;
    }

    public BetaTestRequest setType(String type) {
        this.type = type;
        return this;
    }

    public List<String> getTypeTags() {
        return typeTags;
    }

    public BetaTestRequest setTypeTags(List<String> typeTags) {
        this.typeTags = typeTags;
        return this;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public BetaTestRequest setOpenDate(Date openDate) {
        this.openDate = openDate;
        return this;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public BetaTestRequest setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
        return this;
    }

    public List<String> getApps() {
        return apps;
    }

    public BetaTestRequest setApps(List<String> apps) {
        this.apps = apps;
        return this;
    }

    public String getActionType() {
        return actionType;
    }

    public BetaTestRequest setActionType(String actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getAction() {
        return action;
    }

    public BetaTestRequest setAction(String action) {
        this.action = action;
        return this;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public BetaTestRequest setOpened(boolean opened) {
        isOpened = opened;
        return this;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public BetaTestRequest setRegistered(boolean registered) {
        isRegistered = registered;
        return this;
    }

    @Override
    public String toString() {
        return "BetaTestRequest{" +
                "title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", type='" + type + '\'' +
                ", typeTags=" + typeTags +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", apps=" + apps +
                ", actionType='" + actionType + '\'' +
                ", action='" + action + '\'' +
                ", isOpened=" + isOpened +
                ", isRegistered=" + isRegistered +
                '}';
    }
}

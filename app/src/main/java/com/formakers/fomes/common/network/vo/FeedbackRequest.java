package com.formakers.fomes.common.network.vo;

import java.util.Date;
import java.util.List;

public class FeedbackRequest {
    String title;
    String subTitle;

    String type;
    List<String> typeTags;

    Date openDate;
    Date closeDate;

    List<String> apps;

    String actionType;
    String action;

    public FeedbackRequest() {
    }

    public String getTitle() {
        return title;
    }

    public FeedbackRequest setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public FeedbackRequest setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getType() {
        return type;
    }

    public FeedbackRequest setType(String type) {
        this.type = type;
        return this;
    }

    public List<String> getTypeTags() {
        return typeTags;
    }

    public FeedbackRequest setTypeTags(List<String> typeTags) {
        this.typeTags = typeTags;
        return this;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public FeedbackRequest setOpenDate(Date openDate) {
        this.openDate = openDate;
        return this;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public FeedbackRequest setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
        return this;
    }

    public List<String> getApps() {
        return apps;
    }

    public FeedbackRequest setApps(List<String> apps) {
        this.apps = apps;
        return this;
    }

    public String getActionType() {
        return actionType;
    }

    public FeedbackRequest setActionType(String actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getAction() {
        return action;
    }

    public FeedbackRequest setAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public String toString() {
        return "FeedbackRequest{" +
                "title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", type='" + type + '\'' +
                ", typeTags=" + typeTags +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", apps=" + apps +
                ", actionType='" + actionType + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}

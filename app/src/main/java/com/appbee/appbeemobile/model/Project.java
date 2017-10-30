package com.appbee.appbeemobile.model;

import java.util.List;

public class Project {

    private String name;
    private String introduce;
    private List<String> images;
    private List<String> apps;
    private String interviewerIntroduce;
    private String description;
    private List<String> descriptionImages;
    private String interviewType;
    private boolean interviewNegotiable;
    private String location;
    private String openDate;
    private String closeDate;
    private String startDate;
    private String endDate;
    private List<InterviewPlan> plans;
    private String status;

    public Project(String name, String introduce, List<String> images, List<String> apps, String status) {
        this.name = name;
        this.introduce = introduce;
        this.images = images;
        this.apps = apps;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getApps() {
        return apps;
    }

    public void setApps(List<String> apps) {
        this.apps = apps;
    }

    public List<String> getDescriptionImages() {
        return descriptionImages;
    }

    public void setDescriptionImages(List<String> descriptionImages) {
        this.descriptionImages = descriptionImages;
    }

    public String getInterviewerIntroduce() {
        return interviewerIntroduce;
    }

    public void setInterviewerIntroduce(String interviewerIntroduce) {
        this.interviewerIntroduce = interviewerIntroduce;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(String interviewType) {
        this.interviewType = interviewType;
    }

    public boolean isInterviewNegotiable() {
        return interviewNegotiable;
    }

    public void setInterviewNegotiable(boolean interviewNegotiable) {
        this.interviewNegotiable = interviewNegotiable;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<InterviewPlan> getPlans() {
        return plans;
    }

    public void setPlans(List<InterviewPlan> plans) {
        this.plans = plans;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private class InterviewPlan {
        int minute;
        String plan;
    }
}

package com.appbee.appbeemobile.model;

import java.util.List;

public class Project {

    private String projectId;
    private String name;
    private String introduce;
    private List<ImageObject> images;
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
    private int status;

    private boolean isCLab;
    private boolean isFavorite;
    private boolean isOpen;

    public Project(String projectId, String name, String introduce, List<String> apps, int status) {
        this.projectId = projectId;
        this.name = name;
        this.introduce = introduce;
        this.apps = apps;
        this.status = status;
    }

    public Project(String projectId, String name, String introduce, List<ImageObject> images, List<String> apps, String interviewerIntroduce, String description, String interviewType, boolean interviewNegotiable, String location, String openDate, String closeDate, String startDate, String endDate, List<InterviewPlan> plans, int status) {
        this.projectId = projectId;
        this.name = name;
        this.introduce = introduce;
        this.images = images;
        this.apps = apps;
        this.interviewerIntroduce = interviewerIntroduce;
        this.description = description;
        this.interviewType = interviewType;
        this.interviewNegotiable = interviewNegotiable;
        this.location = location;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.plans = plans;
        this.status = status;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public List<ImageObject> getImages() {
        return images;
    }

    public void setImages(List<ImageObject> images) {
        this.images = images;
    }

    public List<String> getApps() {
        return apps;
    }

    public void setApps(List<String> apps) {
        this.apps = apps;
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

    public List<String> getDescriptionImages() {
        return descriptionImages;
    }

    public void setDescriptionImages(List<String> descriptionImages) {
        this.descriptionImages = descriptionImages;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isCLab() {
        return isCLab;
    }

    public void setCLab(boolean CLab) {
        isCLab = CLab;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public static class InterviewPlan {
        public InterviewPlan(int minute, String plan) {
            this.minute = minute;
            this.plan = plan;
        }

        int minute;
        String plan;

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }
    }

    public static class ImageObject {
        public ImageObject(String url, String name) {
            this.url = url;
            this.name = name;
        }

        String url;
        String name;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

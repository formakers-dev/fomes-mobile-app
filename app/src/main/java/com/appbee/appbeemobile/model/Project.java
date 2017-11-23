package com.appbee.appbeemobile.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Project {

    private String projectId;
    private String name;
    private String introduce;
    private ImageObject image;
    private String description;
    private List<ImageObject> descriptionImages;
    private Person owner;
    private String status;
    @SerializedName("interviews")
    private Interview interview;

    // TODO: 삭제예정
    private List<ImageObject> images;

    // for project test
    public Project(String projectId, String name, String introduce, ImageObject image, String description, List<ImageObject> descriptionImages, Person owner, String status) {
        this.projectId = projectId;
        this.name = name;
        this.introduce = introduce;
        this.description = description;
        this.descriptionImages = descriptionImages;
        this.image = image;
        this.owner = owner;
        this.status = status;
    }

    // for interview test
    public Project(String projectId, String name, String introduce, ImageObject image, String description, List<ImageObject> descriptionImages, Person owner, String status, Interview interview) {
        this.projectId = projectId;
        this.name = name;
        this.introduce = introduce;
        this.image = image;
        this.description = description;
        this.descriptionImages = descriptionImages;
        this.owner = owner;
        this.status = status;
        this.interview = interview;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ImageObject> getDescriptionImages() {
        return descriptionImages;
    }

    public void setDescriptionImages(List<ImageObject> descriptionImages) {
        this.descriptionImages = descriptionImages;
    }

    public ImageObject getImage() {
        return image;
    }

    public void setImage(ImageObject image) {
        this.image = image;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public List<ImageObject> getImages() {
        return images;
    }

    public void setImages(List<ImageObject> images) {
        this.images = images;
    }

    public static class Person {
        private String name;
        private String url;
        private String introduce;

        public Person(String name, String url, String introduce) {
            this.name = name;
            this.url = url;
            this.introduce = introduce;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }
    }

    public static class Interviewer {
        private String name;
        private String url;
        private String introduce;

        public Interviewer(String name, String url, String introduce) {
            this.name = name;
            this.url = url;
            this.introduce = introduce;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }
    }

    public static class InterviewPlan {
        private int minute;
        private String plan;

        public InterviewPlan(int minute, String plan) {
            this.minute = minute;
            this.plan = plan;
        }

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
        private String url;
        private String name;

        public ImageObject(String url, String name) {
            this.url = url;
            this.name = name;
        }

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

    public static class Interview {
        private long seq;
        private List<String> apps;
        private List<InterviewPlan> plans;
        private Date openDate;
        private Date closeDate;
        private Date interviewDate;
        private String location;
        private int totalCount;
        private Person interviewer;

        public Interview(long seq, List<String> apps, List<InterviewPlan> interviewPlanList, Date interviewDate, Date openDate, Date closeDate, String location, int totalCount, Person interviewer) {
            this.seq = seq;
            this.apps = apps;
            this.plans = interviewPlanList;
            this.openDate = openDate;
            this.closeDate = closeDate;
            this.interviewDate = interviewDate;
            this.location = location;
            this.totalCount = totalCount;
            this.interviewer = interviewer;
        }

        public long getSeq() {
            return seq;
        }

        public void setSeq(long seq) {
            this.seq = seq;
        }

        public List<String> getApps() {
            return apps;
        }

        public void setApps(List<String> apps) {
            this.apps = apps;
        }

        public List<InterviewPlan> getPlans() {
            return plans;
        }

        public void setPlans(List<InterviewPlan> plans) {
            this.plans = plans;
        }

        public Date getOpenDate() {
            return openDate;
        }

        public void setOpenDate(Date openDate) {
            this.openDate = openDate;
        }

        public Date getCloseDate() {
            return closeDate;
        }

        public void setCloseDate(Date closeDate) {
            this.closeDate = closeDate;
        }

        public Date getInterviewDate() {
            return interviewDate;
        }

        public void setInterviewDate(Date interviewDate) {
            this.interviewDate = interviewDate;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public Person getInterviewer() {
            return interviewer;
        }

        public void setInterviewer(Person interviewer) {
            this.interviewer = interviewer;
        }
    }
}

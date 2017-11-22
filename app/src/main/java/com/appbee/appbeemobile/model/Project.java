package com.appbee.appbeemobile.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Project {

    private String projectId;
    private String name;
    private String introduce;
    private String description;
    private List<ImageObject> descriptionImages;
    private ImageObject image;
    private Owner owner;
    private String status;
    private boolean isCLab;
    @SerializedName("interviews")
    private Interview interview;

    // TODO: 삭제예정
    private List<ImageObject> images;
    private Interviewer interviewer;

    public Project(String projectId, String name, String introduce, String status) {
        this.projectId = projectId;
        this.name = name;
        this.introduce = introduce;
        this.status = status;
    }

    public Project(String projectId, String name, String introduce, List<ImageObject> images, String description, List<ImageObject> descriptionImages, String status, Interviewer interviewer, boolean isCLab, Interview interview) {
        this.projectId = projectId;
        this.name = name;
        this.introduce = introduce;
        this.images = images;
        this.description = description;
        this.descriptionImages = descriptionImages;
        this.status = status;
        this.interviewer = interviewer;
        this.isCLab = isCLab;
        this.interview = interview;
    }

    // for project test
    public Project(String projectId, String name, String introduce, String description, List<ImageObject> descriptionImages, ImageObject image, Owner owner, String status, boolean isCLab) {
        this.projectId = projectId;
        this.name = name;
        this.introduce = introduce;
        this.description = description;
        this.descriptionImages = descriptionImages;
        this.image = image;
        this.owner = owner;
        this.status = status;
        this.isCLab = isCLab;
    }

    // for interview test
    public Project(String projectId, String name, String introduce, String description, List<ImageObject> descriptionImages, ImageObject image, Owner owner, String status, boolean isCLab, Interview interview) {
        this.projectId = projectId;
        this.name = name;
        this.introduce = introduce;
        this.description = description;
        this.descriptionImages = descriptionImages;
        this.image = image;
        this.owner = owner;
        this.status = status;
        this.isCLab = isCLab;
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

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCLab() {
        return isCLab;
    }

    public void setCLab(boolean CLab) {
        isCLab = CLab;
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

    public Interviewer getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(Interviewer interviewer) {
        this.interviewer = interviewer;
    }

    public static class Owner {
        private String name;
        private String url;
        private String introduce;

        public Owner(String name, String url, String introduce) {
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
        private List<InterviewPlan> plans;
        private String startDate;
        private String endDate;
        private boolean dateNegotiable;
        private String openDate;
        private String closeDate;
        private String location;
        private boolean locationNegotiable;
        private String type;
        private int totalCount;
        private List<String> participants;

        public Interview(long seq, List<InterviewPlan> interviewPlanList, String startDate, String endDate, boolean dateNegotiable, String openDate, String closeDate, String location, boolean locationNegotiable, String type, int totalCount, List<String> participants) {
            this.seq = seq;
            this.plans = interviewPlanList;
            this.startDate = startDate;
            this.endDate = endDate;
            this.dateNegotiable = dateNegotiable;
            this.openDate = openDate;
            this.closeDate = closeDate;
            this.location = location;
            this.locationNegotiable = locationNegotiable;
            this.type = type;
            this.totalCount = totalCount;
            this.participants = participants;
        }

        public long getSeq() {
            return seq;
        }

        public void setSeq(long seq) {
            this.seq = seq;
        }

        public List<InterviewPlan> getPlans() {
            return plans;
        }

        public void setPlans(List<InterviewPlan> plans) {
            this.plans = plans;
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

        public boolean isDateNegotiable() {
            return dateNegotiable;
        }

        public void setDateNegotiable(boolean dateNegotiable) {
            this.dateNegotiable = dateNegotiable;
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

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public boolean isLocationNegotiable() {
            return locationNegotiable;
        }

        public void setLocationNegotiable(boolean locationNegotiable) {
            this.locationNegotiable = locationNegotiable;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public List<String> getParticipants() {
            return participants;
        }

        public void setParticipants(List<String> participants) {
            this.participants = participants;
        }
    }
}

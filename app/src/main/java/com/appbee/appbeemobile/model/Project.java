package com.appbee.appbeemobile.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @NonNull
    public List<ImageObject> getDescriptionImages() {
        return descriptionImages;
    }

    public void setDescriptionImages(List<ImageObject> descriptionImages) {
        this.descriptionImages = descriptionImages;
    }

    @NonNull
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

    @Override
    public String toString() {
        return "Project{" +
                "projectId='" + projectId + '\'' +
                ", name='" + name + '\'' +
                ", introduce='" + introduce + '\'' +
                ", image=" + image +
                ", description='" + description + '\'' +
                ", descriptionImages=" + descriptionImages +
                ", owner=" + owner +
                ", status='" + status + '\'' +
                ", interview=" + interview +
                '}';
    }

    public static class Person {
        private String name;
        private ImageObject image;
        private String introduce;

        public Person(String name, ImageObject image, String introduce) {
            this.name = name;
            this.image = image;
            this.introduce = introduce;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @NonNull
        public ImageObject getImage() {
            return image;
        }

        public void setImage(ImageObject image) {
            this.image = image;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", image=" + image +
                    ", introduce='" + introduce + '\'' +
                    '}';
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

        @Override
        public String toString() {
            return "ImageObject{" +
                    "url='" + url + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static class Interview {
        private long seq;
        private List<AppInfo> apps;
        private String introduce;
        private Date openDate;
        private Date closeDate;
        private Date interviewDate;
        private String location;
        private String locationDescription;
        private int totalCount;
        private List<String> timeSlots;
        private String selectedTimeSlot;
        private String emergencyPhone;
        private String type;

        public Interview(long seq, List<AppInfo> apps, String introduce, Date interviewDate, Date openDate, Date closeDate, String location, String locationDescription, int totalCount, List<String> timeSlots, String selectedTimeSlot, String emergencyPhone, String type) {
            this.seq = seq;
            this.apps = apps;
            this.introduce = introduce;
            this.openDate = openDate;
            this.closeDate = closeDate;
            this.interviewDate = interviewDate;
            this.location = location;
            this.locationDescription = locationDescription;
            this.totalCount = totalCount;
            this.timeSlots = timeSlots;
            this.selectedTimeSlot = selectedTimeSlot;
            this.emergencyPhone = emergencyPhone;
            this.type = type;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public long getSeq() {
            return seq;
        }

        public void setSeq(long seq) {
            this.seq = seq;
        }

        public List<AppInfo> getApps() {
            return apps;
        }

        public void setApps(List<AppInfo> apps) {
            this.apps = apps;
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

        public String getLocationDescription() {
            return locationDescription;
        }

        public void setLocationDescription(String locationDescription) {
            this.locationDescription = locationDescription;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public List<String> getTimeSlots() {
            return timeSlots;
        }

        public void setTimeSlots(List<String> timeSlots) {
            this.timeSlots = timeSlots;
        }

        public String getSelectedTimeSlot() {
            return selectedTimeSlot;
        }

        public void setSelectedTimeSlot(String selectedTimeSlot) {
            this.selectedTimeSlot = selectedTimeSlot;
        }

        public String getEmergencyPhone() {
            return emergencyPhone;
        }

        public void setEmergencyPhone(String emergencyPhone) {
            this.emergencyPhone = emergencyPhone;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Interview{" +
                    "seq=" + seq +
                    ", apps=" + apps +
                    ", introduce='" + introduce + '\'' +
                    ", openDate=" + openDate +
                    ", closeDate=" + closeDate +
                    ", interviewDate=" + interviewDate +
                    ", location='" + location + '\'' +
                    ", locationDescription='" + locationDescription + '\'' +
                    ", totalCount=" + totalCount +
                    ", timeSlots=" + timeSlots +
                    ", selectedTimeSlot='" + selectedTimeSlot + '\'' +
                    ", emergencyPhone='" + emergencyPhone + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}

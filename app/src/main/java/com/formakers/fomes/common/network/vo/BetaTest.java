package com.formakers.fomes.common.network.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.formakers.fomes.common.util.DateUtil;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class BetaTest implements Parcelable {
    @SerializedName("_id") String objectId;
    Integer id;

    String overviewImageUrl;
    String iconImageUrl;
    String title;
    String subTitle;

    List<String> tags;

    Date openDate;
    Date closeDate;
    Date currentDate;

    List<String> apps;

    String actionType;
    String action;

    String reward;

    String bugReportUrl;

    long requiredTime;
    String amount;

    boolean isOpened;
    boolean isCompleted;

    boolean isGroup;

    AfterService afterService;

    public static class AfterService implements Parcelable {
        String epilogue;
        String companySays;

        public AfterService() {}

        public AfterService(Parcel in) {
            readFromParcel(in);
        }

        public String getEpilogue() {
            return epilogue;
        }

        public AfterService setEpilogue(String epilogue) {
            this.epilogue = epilogue;
            return this;
        }

        public String getCompanySays() {
            return companySays;
        }

        public AfterService setCompanySays(String companySays) {
            this.companySays = companySays;
            return this;
        }

        @Override
        public String toString() {
            return "AfterService{" +
                    "epilogue='" + epilogue + '\'' +
                    ", companySays='" + companySays + '\'' +
                    '}';
        }

        /**
         * for Parcelable
         */

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(epilogue);
            dest.writeString(companySays);
        }

        private void readFromParcel(Parcel in) {
            epilogue = in.readString();
            companySays = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public AfterService createFromParcel(Parcel in) {
                return new AfterService(in);
            }

            public AfterService[] newArray(int size) {
                return new AfterService[size];
            }
        };
    }

    public BetaTest() {
    }

    public BetaTest(Parcel in) {
        readFromParcel(in);
    }

    public String getObjectId() {
        return objectId;
    }

    public BetaTest setObjectId(String objectId) {
        this.objectId = objectId;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public BetaTest setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getOverviewImageUrl() {
        return overviewImageUrl;
    }

    public BetaTest setOverviewImageUrl(String overviewImageUrl) {
        this.overviewImageUrl = overviewImageUrl;
        return this;
    }

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public BetaTest setIconImageUrl(String iconImageUrl) {
        this.iconImageUrl = iconImageUrl;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public BetaTest setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public BetaTest setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public BetaTest setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public BetaTest setOpenDate(Date openDate) {
        this.openDate = openDate;
        return this;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public BetaTest setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
        return this;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public BetaTest setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
        return this;
    }

    public List<String> getApps() {
        return apps;
    }

    public BetaTest setApps(List<String> apps) {
        this.apps = apps;
        return this;
    }

    public String getActionType() {
        return actionType;
    }

    public BetaTest setActionType(String actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getAction() {
        return action;
    }

    public BetaTest setAction(String action) {
        this.action = action;
        return this;
    }

    public String getReward() {
        return reward;
    }

    public BetaTest setReward(String reward) {
        this.reward = reward;
        return this;
    }

    public String getBugReportUrl() {
        return bugReportUrl;
    }

    public BetaTest setBugReportUrl(String bugReportUrl) {
        this.bugReportUrl = bugReportUrl;
        return this;
    }

    // convertType : DateUtil.CONVERT_TYPE_.*
    public float getRequiredTime(int convertType) {
        return DateUtil.convertDurationFromMilliseconds(convertType, requiredTime, 0);
    }

    public long getRequiredTime() {
        return requiredTime;
    }

    public BetaTest setRequiredTime(long requiredTime) {
        this.requiredTime = requiredTime;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    public BetaTest setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public BetaTest setOpened(boolean opened) {
        isOpened = opened;
        return this;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public BetaTest setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
        return this;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public BetaTest setGroup(boolean group) {
        isGroup = group;
        return this;
    }

    public AfterService getAfterService() {
        return afterService;
    }

    public BetaTest setAfterService(AfterService afterService) {
        this.afterService = afterService;
        return this;
    }

    public long getRemainDays() {
        return DateUtil.calculateDdays(getCurrentDate().getTime(), getCloseDate().getTime());
    }

    @Override
    public String toString() {
        return "BetaTest{" +
                "objectId='" + objectId + '\'' +
                ", id=" + id +
                ", overviewImageUrl='" + overviewImageUrl + '\'' +
                ", iconImageUrl='" + iconImageUrl + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", tags=" + tags +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", currentDate=" + currentDate +
                ", apps=" + apps +
                ", actionType='" + actionType + '\'' +
                ", action='" + action + '\'' +
                ", reward='" + reward + '\'' +
                ", bugReportUrl='" + bugReportUrl + '\'' +
                ", requiredTime=" + requiredTime +
                ", amount='" + amount + '\'' +
                ", isOpened=" + isOpened +
                ", isCompleted=" + isCompleted +
                ", isGroup=" + isGroup +
                ", afterService=" + afterService +
                '}';
    }

    /**
     * for Parcelable
     */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectId);
        dest.writeInt(id);
        dest.writeString(overviewImageUrl);
        dest.writeString(iconImageUrl);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeStringList(tags);
        dest.writeLong(openDate.getTime());
        dest.writeLong(closeDate.getTime());
        dest.writeLong(currentDate.getTime());
        dest.writeStringList(apps);
        dest.writeString(actionType);
        dest.writeString(action);
        dest.writeString(reward);
        dest.writeString(bugReportUrl);
        dest.writeLong(requiredTime);
        dest.writeString(amount);
        dest.writeInt(isOpened ? 1 : 0);
        dest.writeInt(isCompleted ? 1 : 0);
        dest.writeInt(isGroup ? 1 : 0);
        dest.writeParcelable(afterService, 0);
    }

    private void readFromParcel(Parcel in) {
        objectId = in.readString();
        id = in.readInt();
        overviewImageUrl = in.readString();
        iconImageUrl = in.readString();
        title = in.readString();
        subTitle = in.readString();
        in.readStringList(tags);
        openDate = new Date(in.readLong());
        closeDate = new Date(in.readLong());
        currentDate = new Date(in.readLong());
        in.readStringList(apps);
        actionType = in.readString();
        action = in.readString();
        reward = in.readString();
        bugReportUrl = in.readString();
        requiredTime = in.readLong();
        amount = in.readString();
        isOpened = (in.readInt() == 1);
        isCompleted = (in.readInt() == 1);
        isGroup = (in.readInt() == 1);
        afterService = in.readParcelable(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BetaTest createFromParcel(Parcel in) {
            return new BetaTest(in);
        }

        public BetaTest[] newArray(int size) {
            return new BetaTest[size];
        }
    };
}

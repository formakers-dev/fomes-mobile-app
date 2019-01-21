package com.formakers.fomes.common.network.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.formakers.fomes.common.util.DateUtil;

import java.util.Date;
import java.util.List;

public class BetaTest implements Parcelable {
    Integer id;

    String overviewImageUrl;
    String title;
    String subTitle;

    String type;
    List<String> typeTags;

    Date openDate;
    Date closeDate;
    Date currentDate;

    List<String> apps;

    String actionType;
    String action;

    String reward;

    long requiredTime;
    String amount;

    boolean isOpened;
    boolean isCompleted;

    public BetaTest() {
    }

    public BetaTest(Parcel in) {
        readFromParcel(in);
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

    public String getType() {
        return type;
    }

    public BetaTest setType(String type) {
        this.type = type;
        return this;
    }

    public List<String> getTypeTags() {
        return typeTags;
    }

    public BetaTest setTypeTags(List<String> typeTags) {
        this.typeTags = typeTags;
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

    public long getRemainDays() {
        return DateUtil.calculateDdays(getCurrentDate().getTime(), getCloseDate().getTime());
    }

    @Override
    public String toString() {
        return "BetaTest{" +
                "id=" + id +
                ", overviewImageUrl='" + overviewImageUrl + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", type='" + type + '\'' +
                ", typeTags=" + typeTags +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", currentDate=" + currentDate +
                ", apps=" + apps +
                ", actionType='" + actionType + '\'' +
                ", action='" + action + '\'' +
                ", reward='" + reward + '\'' +
                ", requiredTime=" + requiredTime +
                ", amount='" + amount + '\'' +
                ", isOpened=" + isOpened +
                ", isCompleted=" + isCompleted +
                '}';
    }

    /**
     * for Parcelable
     */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(overviewImageUrl);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(type);
        dest.writeStringList(typeTags);
        dest.writeLong(openDate.getTime());
        dest.writeLong(closeDate.getTime());
        dest.writeLong(currentDate.getTime());
        dest.writeStringList(apps);
        dest.writeString(actionType);
        dest.writeString(action);
        dest.writeString(reward);
        dest.writeLong(requiredTime);
        dest.writeString(amount);
        dest.writeInt(isOpened ? 1 : 0);
        dest.writeInt(isCompleted ? 1 : 0);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        overviewImageUrl = in.readString();
        title = in.readString();
        subTitle = in.readString();
        type = in.readString();
        in.readStringList(typeTags);
        openDate = new Date(in.readLong());
        closeDate = new Date(in.readLong());
        currentDate = new Date(in.readLong());
        in.readStringList(apps);
        actionType = in.readString();
        action = in.readString();
        reward = in.readString();
        requiredTime = in.readLong();
        amount = in.readString();
        isOpened = (in.readInt() == 1);
        isCompleted = (in.readInt() == 1);
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

package com.formakers.fomes.common.network.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.formakers.fomes.common.constant.FomesConstants;
import com.google.gson.annotations.SerializedName;

public class Mission implements Parcelable {
    @SerializedName("_id") String id;
    Integer order;
    String title;
    String description;
    String descriptionImageUrl;
    String guide;
    String type;
    String actionType;
    String action;

    boolean isCompleted;
    boolean isRepeatable;
    boolean isMandatory;
    boolean isRecheckable;

    // For view
    Boolean isLocked;
    boolean isLoading;

    // type - play
    String packageName;
    Long totalPlayTime = 0L; // app only

    public Mission() {
    }

    public Integer getOrder() {
        return order == null ? 0 : order;
    }

    public Mission setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public String getType() {
        return type != null ? type : FomesConstants.BetaTest.Mission.TYPE_DEFAULT;
    }

    public Mission setType(String type) {
        this.type = type;
        return this;
    }

    public String getActionType() {
        return actionType;
    }

    public Mission setActionType(String actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getAction() {
        return action;
    }

    public Mission setAction(String action) {
        this.action = action;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public Mission setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public Long getTotalPlayTime() {
        return totalPlayTime;
    }

    public Mission setTotalPlayTime(Long totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
        return this;
    }

    public boolean isEnabled() {
        return this.isRepeatable() || !this.isCompleted();
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public Mission setCompleted(boolean completed) {
        isCompleted = completed;
        return this;
    }

    public boolean isRepeatable() {
        return isRepeatable;
    }

    public Mission setRepeatable(boolean repeatable) {
        isRepeatable = repeatable;
        return this;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public Mission setMandatory(boolean mandatory) {
        isMandatory = mandatory;
        return this;
    }

    public boolean isRecheckable() {
        return isRecheckable;
    }

    public Mission setRecheckable(boolean recheckable) {
        isRecheckable = recheckable;
        return this;
    }

    public String getId() {
        return id;
    }

    public Mission setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Mission setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Mission setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescriptionImageUrl() {
        return descriptionImageUrl;
    }

    public Mission setDescriptionImageUrl(String descriptionImageUrl) {
        this.descriptionImageUrl = descriptionImageUrl;
        return this;
    }

    public String getGuide() {
        return guide;
    }

    public Mission setGuide(String guide) {
        this.guide = guide;
        return this;
    }

    public boolean isLocked() {
        return isLocked == null ? true : isLocked;
    }

    public Mission setLocked(boolean locked) {
        isLocked = locked;
        return this;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public Mission setLoading(Boolean loading) {
        isLoading = loading;
        return this;
    }

    // TODO : Presenter 로 옮길까????????? 리팩토링이니깐 좀 더 고민을 해보고 차차 결정하긔
    public boolean isBlockedNextMission() {
        if (isMandatory() && !isCompleted()) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id='" + id + '\'' +
                ", order=" + order +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", descriptionImageUrl='" + descriptionImageUrl + '\'' +
                ", guide='" + guide + '\'' +
                ", type='" + type + '\'' +
                ", actionType='" + actionType + '\'' +
                ", action='" + action + '\'' +
                ", isCompleted=" + isCompleted +
                ", isRepeatable=" + isRepeatable +
                ", isMandatory=" + isMandatory +
                ", isRecheckable=" + isRecheckable +
                ", isLocked=" + isLocked +
                ", packageName='" + packageName + '\'' +
                ", totalPlayTime=" + totalPlayTime +
                '}';
    }

    /**
     * for Parcelable
     */

    public Mission(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(order);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(descriptionImageUrl);
        dest.writeString(guide);
        dest.writeString(type);
        dest.writeString(actionType);
        dest.writeString(action);
        dest.writeInt(isCompleted ? 1 : 0);
        dest.writeInt(isRepeatable ? 1 : 0);
        dest.writeInt(isMandatory ? 1 : 0);
        dest.writeInt(isRecheckable ? 1 : 0);
        dest.writeInt(isLocked ? 1 : 0);
        dest.writeString(packageName);
        dest.writeLong(totalPlayTime);
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        order = in.readInt();
        title = in.readString();
        description = in.readString();
        descriptionImageUrl = in.readString();
        guide = in.readString();
        type = in.readString();
        actionType = in.readString();
        action = in.readString();
        isCompleted = in.readInt() == 1;
        isRepeatable = in.readInt() == 1;
        isMandatory = in.readInt() == 1;
        isRecheckable = in.readInt() == 1;
        isLocked = in.readInt() == 1;
        packageName = in.readString();
        totalPlayTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Mission> CREATOR = new Parcelable.Creator<Mission>() {
        public Mission createFromParcel(Parcel in) {
            return new Mission(in);
        }

        public Mission[] newArray(int size) {
            return new Mission[size];
        }
    };
}

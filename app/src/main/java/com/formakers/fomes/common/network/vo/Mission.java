package com.formakers.fomes.common.network.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.formakers.fomes.common.constant.FomesConstants;
import com.google.gson.annotations.SerializedName;

public class Mission implements Parcelable {
    @SerializedName("_id") String id;
    Integer order;
    String iconImageUrl;
    String title;
    String description;
    String descriptionImageUrl;
    MissionItem item;
    String guide;

    // For view
    Boolean isLocked;

    public static class MissionItem implements Parcelable {
        @SerializedName("_id") String id;
        Integer order;
        String type;
        String title;
        String actionType;
        String action;

        // type - play
        String packageName;
        Long totalPlayTime = 0L; // app only

        boolean isCompleted;
        boolean isRepeatable;
        boolean isMandatory;
        boolean isRecheckable;

        public MissionItem() {
        }

        public String getId() {
            return id;
        }

        public MissionItem setId(String id) {
            this.id = id;
            return this;
        }

        public Integer getOrder() {
            return order == null ? 0 : order;
        }

        public MissionItem setOrder(Integer order) {
            this.order = order;
            return this;
        }

        public String getType() {
            return type != null ? type : FomesConstants.BetaTest.Mission.TYPE_DEFAULT;
        }

        public MissionItem setType(String type) {
            this.type = type;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public MissionItem setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getActionType() {
            return actionType;
        }

        public MissionItem setActionType(String actionType) {
            this.actionType = actionType;
            return this;
        }

        public String getAction() {
            return action;
        }

        public MissionItem setAction(String action) {
            this.action = action;
            return this;
        }

        public String getPackageName() {
            return packageName;
        }

        public MissionItem setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Long getTotalPlayTime() {
            return totalPlayTime;
        }

        public MissionItem setTotalPlayTime(Long totalPlayTime) {
            this.totalPlayTime = totalPlayTime;
            return this;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        public MissionItem setCompleted(boolean completed) {
            isCompleted = completed;
            return this;
        }

        public boolean isRepeatable() {
            return isRepeatable;
        }

        public MissionItem setRepeatable(boolean repeatable) {
            isRepeatable = repeatable;
            return this;
        }

        public boolean isMandatory() {
            return isMandatory;
        }

        public MissionItem setMandatory(boolean mandatory) {
            isMandatory = mandatory;
            return this;
        }

        public boolean isRecheckable() {
            return isRecheckable;
        }

        public MissionItem setRecheckable(boolean recheckable) {
            isRecheckable = recheckable;
            return this;
        }

        @Override
        public String toString() {
            return "MissionItem{" +
                    "id='" + id + '\'' +
                    ", order=" + order +
                    ", type='" + type + '\'' +
                    ", title='" + title + '\'' +
                    ", actionType='" + actionType + '\'' +
                    ", action='" + action + '\'' +
                    ", packageName='" + packageName + '\'' +
                    ", totalPlayTime=" + totalPlayTime +
                    ", isCompleted=" + isCompleted +
                    ", isRepeatable=" + isRepeatable +
                    ", isMandatory=" + isMandatory +
                    ", isRecheckable=" + isRecheckable +
                    '}';
        }

        /**
         * for Parcelable
         */

        public MissionItem(Parcel in) {
            readFromParcel(in);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeInt(order);
            dest.writeString(type);
            dest.writeString(title);
            dest.writeString(actionType);
            dest.writeString(action);
            dest.writeString(packageName);
            dest.writeLong(totalPlayTime);
            dest.writeInt(isCompleted ? 1 : 0);
            dest.writeInt(isRepeatable ? 1 : 0);
            dest.writeInt(isMandatory ? 1 : 0);
            dest.writeInt(isRecheckable ? 1 : 0);
        }

        private void readFromParcel(Parcel in) {
            id = in.readString();
            order = in.readInt();
            type = in.readString();
            title = in.readString();
            actionType = in.readString();
            action = in.readString();
            packageName = in.readString();
            totalPlayTime = in.readLong();
            isCompleted = in.readInt() == 1;
            isRepeatable = in.readInt() == 1;
            isMandatory = in.readInt() == 1;
            isRecheckable = in.readInt() == 1;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator<MissionItem> CREATOR = new Parcelable.Creator<MissionItem>() {
            public MissionItem createFromParcel(Parcel in) {
                return new MissionItem(in);
            }

            public MissionItem[] newArray(int size) {
                return new MissionItem[size];
            }
        };
    }

    public String getId() {
        return id;
    }

    public Mission setId(String id) {
        this.id = id;
        return this;
    }

    public Integer getOrder() {
        return this.order == null ? 0 : this.order;
    }

    public Mission setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public String getIconImageUrl() {
        return iconImageUrl;
    }

    public Mission setIconImageUrl(String iconImageUrl) {
        this.iconImageUrl = iconImageUrl;
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

    public MissionItem getItem() {
        return item;
    }

    public Mission setItem(MissionItem item) {
        this.item = item;
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

    // TODO : Presenter 로 옮길까????????? 리팩토링이니깐 좀 더 고민을 해보고 차차 결정하긔
    public boolean isBlockedNextMission() {
        if (item.isMandatory() && !item.isCompleted()) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id='" + id + '\'' +
                ", order=" + order +
                ", iconImageUrl='" + iconImageUrl + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", descriptionImageUrl='" + descriptionImageUrl + '\'' +
                ", item=" + item +
                ", guide='" + guide + '\'' +
                ", isLocked=" + isLocked +
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
        dest.writeString(iconImageUrl);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(descriptionImageUrl);
        dest.writeParcelable(item, 0);
        dest.writeString(guide);
        dest.writeInt(isLocked ? 1 : 0);
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        order = in.readInt();
        iconImageUrl = in.readString();
        title = in.readString();
        description = in.readString();
        descriptionImageUrl = in.readString();
        item = in.readParcelable(null);
        guide = in.readString();
        isLocked = in.readInt() == 1;
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

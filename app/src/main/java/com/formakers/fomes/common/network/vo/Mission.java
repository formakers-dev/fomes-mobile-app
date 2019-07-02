package com.formakers.fomes.common.network.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Mission implements Parcelable {
    Integer order;
    String iconImageUrl;
    String title;
    String description;
    String descriptionImageUrl;
    List<MissionItem> items;
    String guide;

    public static class MissionItem implements Parcelable {
        String title;
        String actionType;
        String action;
        List<String> completedUserIds;

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

        public List<String> getCompletedUserIds() {
            return completedUserIds;
        }

        public MissionItem setCompletedUserIds(List<String> completedUserIds) {
            this.completedUserIds = completedUserIds;
            return this;
        }

        @Override
        public String toString() {
            return "MissionItem{" +
                    "title='" + title + '\'' +
                    ", actionType='" + actionType + '\'' +
                    ", action='" + action + '\'' +
                    ", completedUserIds=" + completedUserIds +
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
            dest.writeString(title);
            dest.writeString(actionType);
            dest.writeString(action);
            dest.writeStringList(completedUserIds);     // TODO : ??
        }

        private void readFromParcel(Parcel in) {
            title = in.readString();
            actionType = in.readString();
            action = in.readString();
            in.readStringList(completedUserIds);
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

    public Integer getOrder() {
        return order;
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

    public List<MissionItem> getItems() {
        return items;
    }

    public Mission setItems(List<MissionItem> items) {
        this.items = items;
        return this;
    }

    public String getGuide() {
        return guide;
    }

    public Mission setGuide(String guide) {
        this.guide = guide;
        return this;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "order=" + order +
                ", iconImageUrl='" + iconImageUrl + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", descriptionImageUrl='" + descriptionImageUrl + '\'' +
                ", items=" + items +
                ", guide='" + guide + '\'' +
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
        dest.writeInt(order);
        dest.writeString(iconImageUrl);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(descriptionImageUrl);
        dest.writeTypedList(items);
        dest.writeString(guide);
    }

    private void readFromParcel(Parcel in) {
        order = in.readInt();
        iconImageUrl = in.readString();
        title = in.readString();
        description = in.readString();
        descriptionImageUrl = in.readString();
        in.readTypedList(items, MissionItem.CREATOR);
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

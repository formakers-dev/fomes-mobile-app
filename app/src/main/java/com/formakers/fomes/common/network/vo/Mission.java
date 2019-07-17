package com.formakers.fomes.common.network.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Mission implements Parcelable {
    @SerializedName("_id") String id;
    Integer order;
    String iconImageUrl;
    String title;
    String description;
    String descriptionImageUrl;
    List<MissionItem> items;
    String guide;

    public static class MissionItem implements Parcelable {
        @SerializedName("_id") String id;
        Integer order;
        String type;
        String title;
        String actionType;
        String action;
        Boolean isCompleted;

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
            return type;
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

        public Boolean isCompleted() {
            return isCompleted;
        }

        public MissionItem setCompleted(Boolean completed) {
            isCompleted = completed;
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
                    ", isCompleted=" + isCompleted +
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
            dest.writeInt(isCompleted ? 1 : 0);     // TODO : ??
        }

        private void readFromParcel(Parcel in) {
            id = in.readString();
            order = in.readInt();
            type = in.readString();
            title = in.readString();
            actionType = in.readString();
            action = in.readString();
            isCompleted = in.readInt() == 1;
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
                "id='" + id + '\'' +
                ", order=" + order +
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
        dest.writeString(id);
        dest.writeInt(order);
        dest.writeString(iconImageUrl);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(descriptionImageUrl);
        dest.writeTypedList(items);
        dest.writeString(guide);
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        order = in.readInt();
        iconImageUrl = in.readString();
        title = in.readString();
        description = in.readString();
        descriptionImageUrl = in.readString();
        in.readTypedList(items, MissionItem.CREATOR);
        guide = in.readString();
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

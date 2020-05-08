package com.formakers.fomes.common.network.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class AwardRecord implements Parcelable {
    String userId;
    String type;

    Reward reward;

    public String getUserId() {
        return userId;
    }

    public AwardRecord setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getType() {
        return type;
    }

    public AwardRecord setType(String type) {
        this.type = type;
        return this;
    }

    public Reward getReward() {
        return reward;
    }

    public AwardRecord setRewards(Reward reward) {
        this.reward = reward;
        return this;
    }

    public static class Reward implements Parcelable {
        String description;
        Long price;

        public String getDescription() {
            return description;
        }

        public Reward setDescription(String description) {
            this.description = description;
            return this;
        }

        public Long getPrice() {
            return price == null ? 0L : price;
        }

        public Reward setOrder(Long price) {
            this.price = price;
            return this;
        }

        @Override
        public String toString() {
            return "Reward{" +
                    "description='" + description + '\'' +
                    ", price=" + price +
                    '}';
        }

        /**
         * for Parcelable
         */

        public Reward(Parcel in) {
            readFromParcel(in);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(description);
            dest.writeLong(price);
        }

        private void readFromParcel(Parcel in) {
            description = in.readString();
            price = in.readLong();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator<Reward> CREATOR = new Parcelable.Creator<Reward>() {
            public Reward createFromParcel(Parcel in) {
                return new Reward(in);
            }

            public Reward[] newArray(int size) {
                return new Reward[size];
            }
        };
    }

    @Override
    public String toString() {
        return "AwardRecord{" +
                "userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", reward=" + reward +
                '}';
    }

    public AwardRecord() {}

    /**
     * for Parcelable
     */

    public AwardRecord(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(type);
        dest.writeParcelable(reward, 0);
    }

    private void readFromParcel(Parcel in) {
        userId = in.readString();
        type = in.readString();
        reward = in.readParcelable(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AwardRecord> CREATOR = new Creator<AwardRecord>() {
        public AwardRecord createFromParcel(Parcel in) {
            return new AwardRecord(in);
        }

        public AwardRecord[] newArray(int size) {
            return new AwardRecord[size];
        }
    };
}

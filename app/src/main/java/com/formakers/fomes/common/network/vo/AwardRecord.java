package com.formakers.fomes.common.network.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.formakers.fomes.common.constant.FomesConstants;

public class AwardRecord implements Parcelable {
    String userId;
    String nickName;
    @Deprecated String type;
    Integer typeCode;

    Reward reward;

    public String getUserId() {
        return userId;
    }

    public AwardRecord setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public AwardRecord setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    @Deprecated
    public String getType() {
        return type;
    }

    @Deprecated
    public AwardRecord setType(String type) {
        this.type = type;
        return this;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public AwardRecord setTypeCode(Integer typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    public Reward getReward() {
        return reward;
    }

    public AwardRecord setRewards(Reward reward) {
        this.reward = reward;
        return this;
    }

    public String getCertificationTitle() {
        int typeCodeIntValue = (typeCode == null) ? 0 : typeCode.intValue();

        if (typeCodeIntValue >= FomesConstants.BetaTest.Award.TYPE_CODE_BEST) {
            return "수석 테스터";
        } else if (typeCodeIntValue >= FomesConstants.BetaTest.Award.TYPE_CODE_GOOD) {
            return "차석 테스터";
        } else if (typeCodeIntValue >= FomesConstants.BetaTest.Award.TYPE_CODE_NORMAL) {
            return "성실 테스터";
        }

        return "테스터";
    }

    public String getTitle() {
        int typeCodeIntValue = (typeCode == null) ? 0 : typeCode.intValue();

        if (typeCodeIntValue >= FomesConstants.BetaTest.Award.TYPE_CODE_BEST) {
            return "테스트 수석";
        } else if (typeCodeIntValue >= FomesConstants.BetaTest.Award.TYPE_CODE_GOOD) {
            return "테스트 차석";
        } else if (typeCodeIntValue == FomesConstants.BetaTest.Award.TYPE_CODE_NORMAL_BONUS) {
            return "성실 보너스";
        } else if (typeCodeIntValue >= FomesConstants.BetaTest.Award.TYPE_CODE_NORMAL) {
            return "테스트 성실상";
        } else if (typeCodeIntValue >= FomesConstants.BetaTest.Award.TYPE_CODE_PARTICIPATED) {
            return "참가상";
        }

        return "기타";
    }

    public static class Reward implements Parcelable {
        String description;
        Long price;

        public Reward() {
        }

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
                ", nickName='" + nickName + '\'' +
                ", type='" + type + '\'' +
                ", typeCode=" + typeCode +
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
        dest.writeString(nickName);
        dest.writeString(type);
        dest.writeInt(typeCode);
        dest.writeParcelable(reward, 0);
    }

    private void readFromParcel(Parcel in) {
        userId = in.readString();
        nickName = in.readString();
        type = in.readString();
        typeCode = in.readInt();
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

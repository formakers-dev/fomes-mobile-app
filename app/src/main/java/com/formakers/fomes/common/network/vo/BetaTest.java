package com.formakers.fomes.common.network.vo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.formakers.fomes.common.util.DateUtil;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BetaTest implements Parcelable {
    @SerializedName("_id") String id;

    String overviewImageUrl;
    String iconImageUrl;
    String title;
    String description;
    ProgressText progressText;

    List<String> tags = new ArrayList<>();

    Date openDate;
    Date closeDate;
    Date currentDate;

    BugReport bugReport;

    List<Mission> missions;

    Rewards rewards;

    boolean isCompleted;

    boolean isGroup;
    AfterService afterService;
    List<String> apps = new ArrayList<>();

    Integer completedItemCount;
    Integer totalItemCount;

    @Deprecated boolean isOpened;
    @Deprecated String subTitle;
    @Deprecated long requiredTime;
    @Deprecated String amount;
    @Deprecated String reward;
    @Deprecated String actionType;
    @Deprecated String action;

    public static class ProgressText implements Parcelable {
        String ready;
        String doing;
        String done;

        public String getReady() {
            return ready;
        }

        public ProgressText setReady(String ready) {
            this.ready = ready;
            return this;
        }

        public String getDoing() {
            return doing;
        }

        public ProgressText setDoing(String doing) {
            this.doing = doing;
            return this;
        }

        public String getDone() {
            return done;
        }

        public ProgressText setDone(String done) {
            this.done = done;
            return this;
        }

        @Override
        public String toString() {
            return "ProgressText{" +
                    "ready='" + ready + '\'' +
                    ", doing='" + doing + '\'' +
                    ", done='" + done + '\'' +
                    '}';
        }

        /**
         * for Parcelable
         */

        public ProgressText(Parcel in) {
            readFromParcel(in);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(ready);
            dest.writeString(doing);
            dest.writeString(done);
        }

        private void readFromParcel(Parcel in) {
            ready = in.readString();
            doing = in.readString();
            done = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator<ProgressText> CREATOR = new Parcelable.Creator<ProgressText>() {
            public ProgressText createFromParcel(Parcel in) {
                return new ProgressText(in);
            }

            public ProgressText[] newArray(int size) {
                return new ProgressText[size];
            }
        };
    }

    public static class AfterService implements Parcelable {
        String epilogue;
        String companySays;
        String awards;

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

        public String getAwards() {
            return awards;
        }

        public AfterService setAwards(String awards) {
            this.awards = awards;
            return this;
        }

        @Override
        public String toString() {
            return "AfterService{" +
                    "epilogue='" + epilogue + '\'' +
                    ", companySays='" + companySays + '\'' +
                    ", awards='" + awards + '\'' +
                    '}';
        }

        /**
         * for Parcelable
         */

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(epilogue);
            dest.writeString(companySays);
            dest.writeString(awards);
        }

        private void readFromParcel(Parcel in) {
            epilogue = in.readString();
            companySays = in.readString();
            awards = in.readString();
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

    public static class BugReport implements Parcelable {
        String url;
        List<String> completedUserIds;

        public String getUrl() {
            return url;
        }

        public BugReport setUrl(String url) {
            this.url = url;
            return this;
        }

        public List<String> getCompletedUserIds() {
            return completedUserIds;
        }

        public BugReport setCompletedUserIds(List<String> completedUserIds) {
            this.completedUserIds = completedUserIds;
            return this;
        }

        @Override
        public String toString() {
            return "BugReport{" +
                    "url='" + url + '\'' +
                    ", completedUserIds=" + completedUserIds +
                    '}';
        }

        /**
         * for Parcelable
         */

        public BugReport(Parcel in) {
            readFromParcel(in);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(url);
            dest.writeStringList(completedUserIds);
        }

        private void readFromParcel(Parcel in) {
            url = in.readString();
            in.readStringList(completedUserIds);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public BugReport createFromParcel(Parcel in) {
                return new BugReport(in);
            }

            public BugReport[] newArray(int size) {
                return new BugReport[size];
            }
        };
    }

    public static class Rewards implements Parcelable {
        Integer minimumDelay;
        List<RewardItem> list;

        public static class RewardItem implements Parcelable {
            Integer order;
            String iconImageUrl;
            String title;
            String content;
            List<String> userIds;

            public Integer getOrder() {
                return order;
            }

            public RewardItem setOrder(Integer order) {
                this.order = order;
                return this;
            }

            public String getIconImageUrl() {
                return iconImageUrl;
            }

            public RewardItem setIconImageUrl(String iconImageUrl) {
                this.iconImageUrl = iconImageUrl;
                return this;
            }

            public String getTitle() {
                return title;
            }

            public RewardItem setTitle(String title) {
                this.title = title;
                return this;
            }

            public String getContent() {
                return content;
            }

            public RewardItem setContent(String content) {
                this.content = content;
                return this;
            }

            public List<String> getUserIds() {
                return userIds;
            }

            public RewardItem setUserIds(List<String> userIds) {
                this.userIds = userIds;
                return this;
            }

            @Override
            public String toString() {
                return "RewardItem{" +
                        "order=" + order +
                        ", iconImageUrl='" + iconImageUrl + '\'' +
                        ", title='" + title + '\'' +
                        ", content='" + content + '\'' +
                        ", userIds=" + userIds +
                        '}';
            }

            /**
             * for Parcelable
             */

            public RewardItem(Parcel in) {
                readFromParcel(in);
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(order);
                dest.writeString(iconImageUrl);
                dest.writeString(title);
                dest.writeString(content);
                dest.writeStringList(userIds);
            }

            private void readFromParcel(Parcel in) {
                order = in.readInt();
                iconImageUrl = in.readString();
                title = in.readString();
                content = in.readString();
                in.readStringList(userIds);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Parcelable.Creator<RewardItem> CREATOR = new Parcelable.Creator<RewardItem>() {
                public RewardItem createFromParcel(Parcel in) {
                    return new RewardItem(in);
                }

                public RewardItem[] newArray(int size) {
                    return new RewardItem[size];
                }
            };
        }

        public Integer getMinimumDelay() {
            return minimumDelay;
        }

        public Rewards setMinimumDelay(Integer minimumDelay) {
            this.minimumDelay = minimumDelay;
            return this;
        }

        public List<RewardItem> getList() {
            return list;
        }

        public Rewards setList(List<RewardItem> list) {
            this.list = list;
            return this;
        }

        @Override
        public String toString() {
            return "Rewards{" +
                    "minimumDelay=" + minimumDelay +
                    ", list=" + list +
                    '}';
        }

        /**
         * for Parcelable
         */

        public Rewards(Parcel in) {
            readFromParcel(in);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(minimumDelay);
            dest.writeTypedList(list);
        }

        private void readFromParcel(Parcel in) {
            minimumDelay = in.readInt();
            in.readTypedList(list, RewardItem.CREATOR);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator<Rewards> CREATOR = new Parcelable.Creator<Rewards>() {
            public Rewards createFromParcel(Parcel in) {
                return new Rewards(in);
            }

            public Rewards[] newArray(int size) {
                return new Rewards[size];
            }
        };
    }

    public BetaTest() {
    }

    public BetaTest(Parcel in) {
        readFromParcel(in);
    }

    public String getId() {
        return id;
    }

    public BetaTest setId(String id) {
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

    public String getDisplayDescription() {
        // 태그가 있으면 태그를, 없으면 description을 보여주기
        if (getTags() != null && getTags().size() > 0) {
            List<String> hashTags = new ArrayList<>();
            for (String tag : getTags()) {
                hashTags.add("#" + tag);
            }
            return TextUtils.join("", hashTags);
        } else {
            return getDescription();
        }
    }

    public String getDescription() {
        return description;
    }

    public BetaTest setDescription(String description) {
        this.description = description;
        return this;
    }

    public ProgressText getProgressText() {
        return progressText;
    }

    public BetaTest setProgressText(ProgressText progressText) {
        this.progressText = progressText;
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

    public boolean isCompleted() {
        return getCompletedItemCount().equals(getTotalItemCount());
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

    public BugReport getBugReport() {
        return bugReport;
    }

    public BetaTest setBugReport(BugReport bugReport) {
        this.bugReport = bugReport;
        return this;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public BetaTest setMissions(List<Mission> missions) {
        this.missions = missions;
        return this;
    }

    public Rewards getRewards() {
        return rewards;
    }

    public BetaTest setRewards(Rewards rewards) {
        this.rewards = rewards;
        return this;
    }

    public Integer getCompletedItemCount() {
        return completedItemCount;
    }

    public BetaTest setCompletedItemCount(Integer completedItemCount) {
        this.completedItemCount = completedItemCount;
        return this;
    }

    public Integer getTotalItemCount() {
        return totalItemCount;
    }

    public BetaTest setTotalItemCount(Integer totalItemCount) {
        this.totalItemCount = totalItemCount;
        return this;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public BetaTest setOpened(boolean opened) {
        isOpened = opened;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public BetaTest setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    // convertType : DateUtil.CONVERT_TYPE_.*
    public float getRequiredTime(int convertType) {
        return DateUtil.convertDurationFromMilliseconds(convertType, requiredTime, 0);
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

    public String getReward() {
        return reward;
    }

    public BetaTest setReward(String reward) {
        this.reward = reward;
        return this;
    }

    @Deprecated
    public String getBugReportUrl() {
        if (this.bugReport != null) {
            return this.bugReport.getUrl();
        } else {
            return null;
        }
    }

    public long getRequiredTime() {
        return requiredTime;
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

    @Override
    public String toString() {
        return "BetaTest{" +
                "id='" + id + '\'' +
                ", overviewImageUrl='" + overviewImageUrl + '\'' +
                ", iconImageUrl='" + iconImageUrl + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", progressText=" + progressText +
                ", tags=" + tags +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", currentDate=" + currentDate +
                ", bugReport=" + bugReport +
                ", missions=" + missions +
                ", rewards=" + rewards +
                ", isCompleted=" + isCompleted +
                ", isGroup=" + isGroup +
                ", afterService=" + afterService +
                ", apps=" + apps +
                ", completedItemCount=" + completedItemCount +
                ", totalItemCount=" + totalItemCount +
                ", isOpened=" + isOpened +
                ", subTitle='" + subTitle + '\'' +
                ", requiredTime=" + requiredTime +
                ", amount='" + amount + '\'' +
                ", reward='" + reward + '\'' +
                ", actionType='" + actionType + '\'' +
                ", action='" + action + '\'' +
                '}';
    }

    /**
     * for Parcelable
     */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(overviewImageUrl);
        dest.writeString(iconImageUrl);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeParcelable(progressText, 0);
        dest.writeStringList(tags);
        dest.writeLong(openDate.getTime());
        dest.writeLong(closeDate.getTime());
        dest.writeLong(currentDate.getTime());
        dest.writeParcelable(bugReport, 0);
        dest.writeTypedList(missions);
        dest.writeParcelable(rewards, 0);
        dest.writeInt(isCompleted ? 1 : 0);
        dest.writeInt(isGroup ? 1 : 0);
        dest.writeParcelable(afterService, 0);
        dest.writeStringList(apps);
        dest.writeInt(completedItemCount);
        dest.writeInt(totalItemCount);
        dest.writeInt(isOpened ? 1 : 0);
        dest.writeString(subTitle);
        dest.writeLong(requiredTime);
        dest.writeString(amount);
        dest.writeString(reward);
        dest.writeString(actionType);
        dest.writeString(action);
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        overviewImageUrl = in.readString();
        iconImageUrl = in.readString();
        title = in.readString();
        description = in.readString();
        progressText = in.readParcelable(null);
        in.readStringList(tags);
        openDate = new Date(in.readLong());
        closeDate = new Date(in.readLong());
        currentDate = new Date(in.readLong());
        bugReport = in.readParcelable(null);
        in.readTypedList(missions, Mission.CREATOR);
        rewards = in.readParcelable(null);
        isCompleted = (in.readInt() == 1);
        isGroup = (in.readInt() == 1);
        afterService = in.readParcelable(null);
        in.readStringList(apps);
        completedItemCount = in.readInt();
        totalItemCount = in.readInt();
        isOpened = (in.readInt() == 1);
        subTitle = in.readString();
        requiredTime = in.readLong();
        amount = in.readString();
        reward = in.readString();
        actionType = in.readString();
        action = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<BetaTest> CREATOR = new Parcelable.Creator<BetaTest>() {
        public BetaTest createFromParcel(Parcel in) {
            return new BetaTest(in);
        }

        public BetaTest[] newArray(int size) {
            return new BetaTest[size];
        }
    };
}

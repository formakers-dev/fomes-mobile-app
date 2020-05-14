package com.formakers.fomes.common.network.vo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.StringRes;

import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.util.DateUtil;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BetaTest implements Parcelable {
    @SerializedName("_id") String id;

    String coverImageUrl;
    String iconImageUrl;
    String title;
    String description;
    String plan;
    String status;
    String purpose;
    ProgressText progressText;

    List<String> tags = new ArrayList<>();

    Date openDate;
    Date closeDate;
    Date currentDate;

    BugReport bugReport;

    List<Mission> missions;

    Rewards rewards;

    boolean isAttended;
    boolean isCompleted;
    boolean isRegisteredEpilogue;

    Epilogue epilogue;
    List<String> similarApps = new ArrayList<>();

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

    public static class Epilogue implements Parcelable {
        String deeplink;
        String companyImageUrl;
        String companyName;
        String companySays;
        String awards;

        public Epilogue() {}

        public Epilogue(Parcel in) {
            readFromParcel(in);
        }

        public String getDeeplink() {
            return deeplink;
        }

        public Epilogue setDeeplink(String deeplink) {
            this.deeplink = deeplink;
            return this;
        }

        public String getCompanyImageUrl() {
            return companyImageUrl;
        }

        public Epilogue setCompanyImageUrl(String companyImageUrl) {
            this.companyImageUrl = companyImageUrl;
            return this;
        }

        public String getCompanyName() {
            return companyName;
        }

        public Epilogue setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public String getCompanySays() {
            return companySays;
        }

        public Epilogue setCompanySays(String companySays) {
            this.companySays = companySays;
            return this;
        }

        public String getAwards() {
            return awards;
        }

        public Epilogue setAwards(String awards) {
            this.awards = awards;
            return this;
        }

        @Override
        public String toString() {
            return "Epilogue{" +
                    "deeplink='" + deeplink + '\'' +
                    ", companyImageUrl='" + companyImageUrl + '\'' +
                    ", companyName='" + companyName + '\'' +
                    ", companySays='" + companySays + '\'' +
                    ", awards='" + awards + '\'' +
                    '}';
        }

        /**
         * for Parcelable
         */

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(deeplink);
            dest.writeString(companyImageUrl);
            dest.writeString(companyName);
            dest.writeString(companySays);
            dest.writeString(awards);
        }

        private void readFromParcel(Parcel in) {
            deeplink = in.readString();
            companyImageUrl = in.readString();
            companyName = in.readString();
            companySays = in.readString();
            awards = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public Epilogue createFromParcel(Parcel in) {
                return new Epilogue(in);
            }

            public Epilogue[] newArray(int size) {
                return new Epilogue[size];
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
            String type;
            Integer count;

            public Integer getOrder() {
                return order == null ? 0 : order;
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

            public String getType() {
                return type;
            }

            public RewardItem setType(String type) {
                this.type = type;
                return this;
            }

            public Integer getCount() {
                return count;
            }

            public RewardItem setCount(Integer count) {
                this.count = count;
                return this;
            }

            @Override
            public String toString() {
                return "RewardItem{" +
                        "order=" + order +
                        ", iconImageUrl='" + iconImageUrl + '\'' +
                        ", title='" + title + '\'' +
                        ", content='" + content + '\'' +
                        ", type='" + type + '\'' +
                        ", count=" + count +
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
                dest.writeString(type);
                dest.writeInt(count);
            }

            private void readFromParcel(Parcel in) {
                order = in.readInt();
                iconImageUrl = in.readString();
                title = in.readString();
                content = in.readString();
                type = in.readString();
                count = in.readInt();
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

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public BetaTest setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
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
        String result = getTagsString();
        if (!TextUtils.isEmpty(result)) {
            return result;
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

    public String getPlan() {
        return plan;
    }

    public BetaTest setPlan(String plan) {
        this.plan = plan;
        return this;
    }

    public boolean isPremiumPlan() {
        return FomesConstants.BetaTest.Plan.STANDARD.equals(getPlan())
                || FomesConstants.BetaTest.Plan.SIMPLE.equals(getPlan());
    }

    public @StringRes int getPlanStringResId() {
        if (isPremiumPlan()) {
            return R.string.betatest_plan_premium;
        } else {
            return R.string.betatest_plan_lite;
        }
    }

    public String getStatus() {
        return status;
    }

    public BetaTest setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getPurpose() {
        return purpose;
    }

    public BetaTest setPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public ProgressText getProgressText() {
        return progressText;
    }

    public BetaTest setProgressText(ProgressText progressText) {
        this.progressText = progressText;
        return this;
    }

    public String getTagsString() {
        if (getTags() != null && getTags().size() > 0) {
            List<String> hashTags = new ArrayList<>();
            for (String tag : getTags()) {
                hashTags.add("#" + tag);
            }
            return TextUtils.join(" ", hashTags);
        } else {
            return "";
        }
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

    public List<String> getSimilarApps() {
        return similarApps;
    }

    public BetaTest setSimilarApps(List<String> similarApps) {
        this.similarApps = similarApps;
        return this;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public BetaTest setCompleted(boolean completed) {
        isCompleted = completed;
        return this;
    }

    public boolean isAttended() {
        return isAttended;
    }

    public BetaTest setAttended(boolean attended) {
        isAttended = attended;
        return this;
    }

    public boolean isRegisteredEpilogue() {
        return isRegisteredEpilogue;
    }

    public BetaTest setRegisteredEpilogue(boolean registeredEpilogue) {
        isRegisteredEpilogue = registeredEpilogue;
        return this;
    }

    public Epilogue getEpilogue() {
        return epilogue;
    }

    public BetaTest setEpilogue(Epilogue epilogue) {
        this.epilogue = epilogue;
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

    @Deprecated
    public String getBugReportUrl() {
        if (this.bugReport != null) {
            return this.bugReport.getUrl();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "BetaTest{" +
                "id='" + id + '\'' +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", iconImageUrl='" + iconImageUrl + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", plan='" + plan + '\'' +
                ", status='" + status + '\'' +
                ", purpose='" + purpose + '\'' +
                ", progressText=" + progressText +
                ", tags=" + tags +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", currentDate=" + currentDate +
                ", bugReport=" + bugReport +
                ", missions=" + missions +
                ", rewards=" + rewards +
                ", isAttended=" + isAttended +
                ", isCompleted=" + isCompleted +
                ", isRegisteredEpilogue=" + isRegisteredEpilogue +
                ", epilogue=" + epilogue +
                ", similarApps=" + similarApps +
                '}';
    }

    /**
     * for Parcelable
     */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(coverImageUrl);
        dest.writeString(iconImageUrl);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(plan);
        dest.writeString(status);
        dest.writeString(purpose);
        dest.writeParcelable(progressText, 0);
        dest.writeStringList(tags);
        dest.writeLong(openDate.getTime());
        dest.writeLong(closeDate.getTime());
        dest.writeLong(currentDate.getTime());
        dest.writeParcelable(bugReport, 0);
        dest.writeTypedList(missions);
        dest.writeParcelable(rewards, 0);
        dest.writeInt(isCompleted ? 1 : 0);
        dest.writeInt(isAttended ? 1 : 0);
        dest.writeInt(isRegisteredEpilogue ? 1 : 0);
        dest.writeParcelable(epilogue, 0);
        dest.writeStringList(similarApps);
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        coverImageUrl = in.readString();
        iconImageUrl = in.readString();
        title = in.readString();
        description = in.readString();
        plan = in.readString();
        status = in.readString();
        purpose = in.readString();
        progressText = in.readParcelable(null);
        in.readStringList(tags);
        openDate = new Date(in.readLong());
        closeDate = new Date(in.readLong());
        currentDate = new Date(in.readLong());
        bugReport = in.readParcelable(null);
        in.readTypedList(missions, Mission.CREATOR);
        rewards = in.readParcelable(null);
        isCompleted = (in.readInt() == 1);
        isAttended = (in.readInt() == 1);
        isRegisteredEpilogue = (in.readInt() == 1);
        epilogue = in.readParcelable(null);
        in.readStringList(similarApps);
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

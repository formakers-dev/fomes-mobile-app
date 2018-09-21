package com.formakers.fomes.common.network.vo;

import java.util.List;

public class RecentReport {
    List<Rank> totalUsedTimeRank;
    List<UsageGroup> usages;

    public RecentReport(List<Rank> totalUsedTimeRank, List<UsageGroup> usages) {
        this.totalUsedTimeRank = totalUsedTimeRank;
        this.usages = usages;
    }

    public List<Rank> getTotalUsedTimeRank() {
        return totalUsedTimeRank;
    }

    public void setTotalUsedTimeRank(List<Rank> totalUsedTimeRank) {
        this.totalUsedTimeRank = totalUsedTimeRank;
    }

    public List<UsageGroup> getUsages() {
        return usages;
    }

    public void setUsages(List<UsageGroup> usages) {
        this.usages = usages;
    }

    @Override
    public String toString() {
        return "RecentReport{" +
                "totalUsedTimeRank=" + totalUsedTimeRank +
                ", usages=" + usages +
                '}';
    }

    class Rank {
        String userId;
        Integer rank;
        Long content;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public Long getContent() {
            return content;
        }

        public void setContent(Long content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "Rank{" +
                    "userId='" + userId + '\'' +
                    ", rank=" + rank +
                    ", content=" + content +
                    '}';
        }
    }

    class UsageGroup {
        Integer groupType;
        List<Usage> appUsages;
        List<Usage> categoryUsages;
        List<Usage> developerUsages;

        public Integer getGroupType() {
            return groupType;
        }

        public void setGroupType(Integer groupType) {
            this.groupType = groupType;
        }

        public List<Usage> getAppUsages() {
            return appUsages;
        }

        public void setAppUsages(List<Usage> appUsages) {
            this.appUsages = appUsages;
        }

        public List<Usage> getCategoryUsages() {
            return categoryUsages;
        }

        public void setCategoryUsages(List<Usage> categoryUsages) {
            this.categoryUsages = categoryUsages;
        }

        public List<Usage> getDeveloperUsages() {
            return developerUsages;
        }

        public void setDeveloperUsages(List<Usage> developerUsages) {
            this.developerUsages = developerUsages;
        }

        @Override
        public String toString() {
            return "UsageGroup{" +
                    "groupType=" + groupType +
                    ", appUsages=" + appUsages +
                    ", categoryUsages=" + categoryUsages +
                    ", developerUsages=" + developerUsages +
                    '}';
        }
    }
}

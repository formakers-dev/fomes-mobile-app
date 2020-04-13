package com.formakers.fomes.common.network.vo;

import java.util.List;

public class RecentReport {
    List<Rank> totalUsedTimeRank;
    List<UsageGroup> usages;
    Long totalUserCount;

    public RecentReport() {
    }

    public List<Rank> getTotalUsedTimeRank() {
        return totalUsedTimeRank;
    }

    public RecentReport setTotalUsedTimeRank(List<Rank> totalUsedTimeRank) {
        this.totalUsedTimeRank = totalUsedTimeRank;
        return this;
    }

    public List<UsageGroup> getUsages() {
        return usages;
    }

    public RecentReport setUsages(List<UsageGroup> usages) {
        this.usages = usages;
        return this;
    }

    public long getTotalUserCount() {
        return totalUserCount;
    }

    public RecentReport setTotalUserCount(long totalUserCount) {
        this.totalUserCount = totalUserCount;
        return this;
    }

    @Override
    public String toString() {
        return "RecentReport{" +
                "totalUsedTimeRank=" + totalUsedTimeRank +
                ", usages=" + usages +
                ", totalUserCount=" + totalUserCount +
                '}';
    }
}

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
}

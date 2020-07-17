package com.formakers.fomes.betatest;

import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;

import java.util.List;

public interface FinishedBetaTestAwardPagerAdapterContract {
    interface View {
        void setPresenter(FinishedBetaTestDetailContract.Presenter presenter);
        void notifyDataSetChanged();
    }

    interface Model {
        void addAll(List<AwardRecord> awardRecords);
        void addAllFromRewardItems(List<BetaTest.Rewards.RewardItem> rewardItems);
        int getCount();
    }
}
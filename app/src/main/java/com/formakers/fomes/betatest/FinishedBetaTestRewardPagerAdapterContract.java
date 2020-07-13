package com.formakers.fomes.betatest;

import com.formakers.fomes.common.network.vo.BetaTest;

import java.util.List;

public interface FinishedBetaTestRewardPagerAdapterContract {
    interface View {
        void setPresenter(FinishedBetaTestDetailContract.Presenter presenter);
        void notifyDataSetChanged();
    }

    interface Model {
        void addAll(List<BetaTest.Rewards.RewardItem> rewardItems);
        int getCount();
    }
}
package com.formakers.fomes.betatest;


import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;

import rx.subscriptions.CompositeSubscription;

public interface FinishedBetaTestDetailContract {
    interface Presenter {
        void requestAwardRecordOfBest(String betaTestId);
        void requestEpilogue(String betaTestId);

        //Base
        AnalyticsModule.Analytics getAnalytics();
        ImageLoader getImageLoader();
    }

    interface View extends BaseView<Presenter> {
        void bindEpilogue(BetaTest.Epilogue epilogue);
        void bindAwards(AwardRecord awardRecord);
        void disableEpilogue();
        void showLoading();
        void hideLoading();

        // TODO : BaseView 로 이동 고려
        CompositeSubscription getCompositeSubscription();
    }
}

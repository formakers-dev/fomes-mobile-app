package com.formakers.fomes.main.contract;


import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.BetaTest;

public interface BetaTestDetailContract {
    interface Presenter {
        //Base
        AnalyticsModule.Analytics getAnalytics();

        void sendEventLog(String code, String ref);
        void unsubscribe();

        void load(String id);

        void requestCompleteMissionItem(String id);
    }

    interface View extends BaseView<Presenter> {
        void bind(BetaTest betaTest);
        void showLoading();
        void hideLoading();

        void unlockMissions();
    }
}

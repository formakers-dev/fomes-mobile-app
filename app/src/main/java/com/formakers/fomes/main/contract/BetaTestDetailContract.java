package com.formakers.fomes.main.contract;


import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.mvp.BaseView;

public interface BetaTestDetailContract {
    interface Presenter {
        //Base
        AnalyticsModule.Analytics getAnalytics();

        void sendEventLog(String code, String ref);
        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {

    }
}

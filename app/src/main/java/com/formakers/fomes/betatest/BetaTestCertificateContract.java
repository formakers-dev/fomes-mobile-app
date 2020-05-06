package com.formakers.fomes.betatest;


import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.BetaTest;

import rx.subscriptions.CompositeSubscription;

public interface BetaTestCertificateContract {
    interface Presenter {
        void requestBetaTestDetail(String betaTestId);
        void requestUserNickName();

        //Base
        AnalyticsModule.Analytics getAnalytics();
        ImageLoader getImageLoader();
    }

    interface View extends BaseView<Presenter> {
        void bindBetaTest(BetaTest betaTest);
        void bindUserNickName(String nickName);
        void showLoading();
        void hideLoading();

        // TODO : BaseView 로 이동 고려
        CompositeSubscription getCompositeSubscription();
    }
}

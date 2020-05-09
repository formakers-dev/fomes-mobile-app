package com.formakers.fomes.betatest;


import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;

import rx.subscriptions.CompositeSubscription;

public interface BetaTestCertificateContract {
    interface Presenter {
        void requestBetaTestCertificate(String betaTestId);
        void requestUserNickName();

        //Base
        AnalyticsModule.Analytics getAnalytics();
        ImageLoader getImageLoader();
    }

    interface View extends BaseView<Presenter> {
        void bindBetaTestCertificate(BetaTest betaTest, AwardRecord awardRecord);
        void bindUserNickName(String nickName);
        void showLoading();
        void hideLoading();
        void showErrorView();

        // TODO : BaseView 로 이동 고려
        CompositeSubscription getCompositeSubscription();
    }
}

package com.formakers.fomes.betatest;


import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.BetaTest;

import java.util.Date;
import java.util.List;

import rx.Single;

public interface BetaTestContract {
    interface Presenter {
        //Base
        AnalyticsModule.Analytics getAnalytics();
        ImageLoader getImageLoader();

        void setAdapterModel(BetaTestListAdapterContract.Model adapterModel);

        void initialize();
        Single<List<BetaTest>> loadToBetaTestList(Date sortingCriteriaDate);
        void requestBetaTestProgress(String betaTestId);

        BetaTest getBetaTestItem(int position);
        int getBetaTestPostitionById(String id);
        String getInterpretedUrl(String originalUrl);
        void sendEventLog(String code, String ref);
        void shareToKaKao(BetaTest betaTest);

        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        void setAdapterView(BetaTestListAdapterContract.View adapterView);
        void setUserNickName(String nickName);

        void showLoading();
        void hideLoading();
        void showEmptyView();

        void showBetaTestListView();

        void refreshBetaTestList();
        void refreshBetaTestProgress(int position);
    }
}

package com.formakers.fomes.betatest;


import android.content.Intent;
import android.net.Uri;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.BetaTest;

import java.util.List;

import rx.Single;

public interface FinishedBetaTestContract {
    interface Presenter {
        //Base
        AnalyticsModule.Analytics getAnalytics();
        ImageLoader getImageLoader();

        void sendEventLog(String code, String ref);

        void setAdapterModel(FinishedBetaTestListAdapterContract.Model adapterModel);

        void initialize();
        Single<List<BetaTest>> load();
        void applyCompletedFilter(boolean isNeedFilter);
        BetaTest getItem(int position);

        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        void setAdapterView(FinishedBetaTestListAdapterContract.View adapterView);

        boolean isNeedAppliedCompletedFilter();

        void showLoading();
        void hideLoading();

        void showEmptyView();
        void showListView();

        void refresh();

        void startActivity(Intent intent);
        void startWebViewActivity(String title, String url);
        void startByDeeplink(Uri deeplink);
    }
}

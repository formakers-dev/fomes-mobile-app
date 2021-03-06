package com.formakers.fomes.recommend;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.RecommendApp;

import rx.Completable;

public interface RecommendContract {
    interface Presenter {
        String CATEGORY_GAME = "GAME";

        //Base
        AnalyticsModule.Analytics getAnalytics();

        void setAdapterModel(RecommendListAdapterContract.Model adapterModel);
        void emitShowDetailEvent(RecommendApp recommendApp);
        void loadRecommendApps(String categoryId);
        void reloadRecommendApps(String categoryId);

        Completable requestSaveToWishList(String packageName);
        Completable requestRemoveFromWishList(String packageName);
        void updateWishedStatus(String packageName, boolean wishedByMe);
        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        void onShowDetailEvent(RecommendApp recommendApp);
        void showLoading();
        void hideLoading();
        void showRecommendList();
        void showErrorPage();
        void refreshRecommendList();
        boolean isEndOfRecommendList();
    }
}

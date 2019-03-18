package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.RecommendApp;

import rx.Completable;

public interface RecommendContract {
    interface Presenter {
        void setAdapterModel(RecommendListAdapterContract.Model adapterModel);
        void emitShowDetailEvent(RecommendApp recommendApp);
        void loadRecommendApps(String categoryId);

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
        boolean isNeedMoreRecommendItems();
    }
}

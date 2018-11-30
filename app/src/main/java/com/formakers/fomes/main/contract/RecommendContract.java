package com.formakers.fomes.main.contract;

import com.bumptech.glide.RequestManager;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.RecommendApp;

import java.util.List;

import rx.Completable;

public interface RecommendContract {
    interface Presenter {
        RequestManager getImageLoader();
        void setAdapterModel(RecommendListAdapterContract.Model adapterModel);
        void emitShowDetailEvent(RecommendApp recommendApp);
        void loadRecommendApps(String categoryId);
        void loadRecommendAppsMore(String categoryId);

        Completable emitSaveToWishList(String packageName);
        Completable emitRemoveFromWishList(String packageName);
        void emitRefreshWishedByMe(String packageName, boolean wishedByMe);
        void unsubscribe();
    }

    interface View extends BaseView<Presenter> {
        void onShowDetailEvent(RecommendApp recommendApp);
        void refreshWishedByMe(String packageName, boolean wishedByMe);
        void showLoading();
        void bindRecommendList(List<RecommendApp> recommendApps);
        void bindRecommendListMore(List<RecommendApp> recommendApps);
    }
}

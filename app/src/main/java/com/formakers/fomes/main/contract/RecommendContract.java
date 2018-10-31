package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.network.vo.RecommendApp;

import java.util.List;

import rx.Completable;
import rx.Observable;

public interface RecommendContract {
    interface Presenter {
        void setAdapterModel(RecommendListAdapterContract.Model adapterModel);
        void emitShowDetailEvent(RecommendApp recommendApp, int rank);
        Observable<List<RecommendApp>> loadSimilarAppsByDemographic();

        Completable emitSaveToWishList(String packageName);
        Completable emitRemoveFromWishList(String packageName);
        void emitRefreshWishedByMe(String packageName, boolean wishedByMe);
    }

    interface View extends BaseView<Presenter> {
        void onShowDetailEvent(RecommendApp recommendApp, int rank);
        void refreshWishedByMe(String packageName, boolean wishedByMe);
    }
}

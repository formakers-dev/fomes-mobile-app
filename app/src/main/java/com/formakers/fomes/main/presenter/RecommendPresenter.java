package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;

import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;

public class RecommendPresenter implements RecommendContract.Presenter {

    private RecommendListAdapterContract.Model adapterModel;
    private RecommendContract.View view;
    private RecommendService recommendService;
    private UserService userService;

    @Inject
    public RecommendPresenter(RecommendContract.View view, RecommendService recommendService, UserService userService) {
        this.view = view;
        this.recommendService = recommendService;
        this.userService = userService;
    }

    @Override
    public void setAdapterModel(RecommendListAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void emitShowDetailEvent(RecommendApp recommendApp) {
        this.view.onShowDetailEvent(recommendApp);
    }

    @Override
    public Observable<List<RecommendApp>> loadRecommendApps(String categoryId) {
        return recommendService.requestRecommendApps(categoryId, 1, 10);
    }

    @Override
    public Completable emitSaveToWishList(String packageName) {
        return userService.requestSaveAppToWishList(packageName);
    }

    @Override
    public Completable emitRemoveFromWishList(String packageName) {
        return userService.requestRemoveAppFromWishList(packageName);
    }

    @Override
    public void emitRefreshWishedByMe(String packageName, boolean wishedByMe) {
        this.view.refreshWishedByMe(packageName, wishedByMe);
    }
}

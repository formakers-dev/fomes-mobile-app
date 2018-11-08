package com.formakers.fomes.main.presenter;

import com.bumptech.glide.RequestManager;
import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

public class RecommendPresenter implements RecommendContract.Presenter {

    private RecommendListAdapterContract.Model adapterModel;
    private RecommendContract.View view;
    private RecommendService recommendService;
    private UserService userService;
    private RequestManager requestManager;

    @Inject
    public RecommendPresenter(RecommendContract.View view, RecommendService recommendService, UserService userService, RequestManager requestManager) {
        this.view = view;
        this.recommendService = recommendService;
        this.userService = userService;
        this.requestManager = requestManager;
    }

    @Override
    public RequestManager getImageLoader() {
        return requestManager;
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
    public void loadRecommendApps(String categoryId) {
        this.view.showLoading();

        recommendService.requestRecommendApps(categoryId, 1, 10)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendApps -> {
                    if (recommendApps.size() > 0) {
                        List<RecommendApp> duplicationRemovedRecommendApps = removeDuplicatedRecommendApps(recommendApps);
                        this.view.bindRecommendList(duplicationRemovedRecommendApps);
                    } else {
                        this.view.showEmptyRecommendList();
                    }
                });
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

    private List<RecommendApp> removeDuplicatedRecommendApps(List<RecommendApp> recommendApps) {
        final List<String> packageNames = new ArrayList<>();

        for (int i = 0; i < recommendApps.size(); ) {
            final String packageName = recommendApps.get(i).getAppInfo().getPackageName();

            if (packageNames.contains(packageName)) {
                recommendApps.remove(i);
            } else {
                packageNames.add(packageName);
                i++;
            }
        }

        return recommendApps;
    }
}

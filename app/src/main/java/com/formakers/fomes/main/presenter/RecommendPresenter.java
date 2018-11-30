package com.formakers.fomes.main.presenter;

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
import rx.subscriptions.CompositeSubscription;

public class RecommendPresenter implements RecommendContract.Presenter {

    private RecommendListAdapterContract.Model adapterModel;
    private RecommendContract.View view;
    private RecommendService recommendService;
    private UserService userService;
    private CompositeSubscription compositeSubscription;
    private int currentPage;

    @Inject
    public RecommendPresenter(RecommendContract.View view, RecommendService recommendService, UserService userService) {
        this.view = view;
        this.recommendService = recommendService;
        this.userService = userService;
        this.compositeSubscription = new CompositeSubscription();
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
        setCurrentPage(1);

        compositeSubscription.add(
                recommendService.requestRecommendApps(categoryId, getCurrentPage())
                        .doOnSubscribe(() -> this.view.showLoading())
                        .doOnTerminate(() -> this.view.hideLoading())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(recommendApps -> {
                            List<RecommendApp> duplicationRemovedRecommendApps = removeDuplicatedRecommendApps(recommendApps);
                            this.view.bindRecommendList(duplicationRemovedRecommendApps);
                        })
        );
    }

    @Override
    public void loadRecommendAppsMore(String categoryId) {
        if (getCurrentPage() > 0) {
            int nextPage = getCurrentPage() + 1;

            compositeSubscription.add(
                    recommendService.requestRecommendApps(categoryId, nextPage)
                            .doOnSubscribe(() -> this.view.showLoading())
                            .doOnTerminate(() -> this.view.hideLoading())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(recommendApps -> {
                                setCurrentPage(nextPage);

                                List<RecommendApp> duplicationRemovedRecommendApps = removeDuplicatedRecommendApps(recommendApps);
                                this.view.bindRecommendListMore(duplicationRemovedRecommendApps);
                            })
            );
        }
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

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}

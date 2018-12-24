package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;
import com.formakers.fomes.main.dagger.scope.RecommendFragmentScope;
import com.google.common.collect.Iterators;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

@RecommendFragmentScope
public class RecommendPresenter implements RecommendContract.Presenter {

    public final static String TAG = "RecommendPresenter";

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
        final int nextPage = getCurrentPage() + 1;

        compositeSubscription.add(
                recommendService.requestRecommendApps(categoryId, nextPage)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(() -> this.view.showLoading())
                        .doOnTerminate(() -> this.view.hideLoading())
                        .subscribe(recommendApps -> {
                            if (recommendApps.size() <= 0) {
                                throw new IllegalArgumentException("Empty List");
                            }

                            this.adapterModel.addAll(removeDuplicatedRecommendApps(recommendApps));
                            this.view.refreshRecommendList();
                        }, e -> {
                            Log.e(TAG, e.toString());
                            if (adapterModel.getItemCount() <= 0) {
                                this.view.showErrorPage();
                            }
                        }, () -> {
                            setCurrentPage(nextPage);
                            this.view.showRecommendList();
                        })
        );
    }

    @Override
    public Completable requestSaveToWishList(String packageName) {
        return userService.requestSaveAppToWishList(packageName);
    }

    @Override
    public Completable requestRemoveFromWishList(String packageName) {
        return userService.requestRemoveAppFromWishList(packageName);
    }

    @Override
    public void updateWishedStatus(String packageName, boolean wishedByMe) {
        this.adapterModel.updateWishedStatus(packageName, wishedByMe);
    }

    @Override
    public void unsubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    private List<RecommendApp> removeDuplicatedRecommendApps(List<RecommendApp> recommendApps) {
        List<RecommendApp> tempList = new ArrayList<>(adapterModel.getAllItems());
        int startIndex = tempList.size() <= 0 ? 0 : tempList.size();

        tempList.addAll(recommendApps);

        final List<String> packageNames = new ArrayList<>();

        for (int i = 0; i < tempList.size(); ) {
            final String packageName = tempList.get(i).getAppInfo().getPackageName();

            if (packageNames.contains(packageName)) {
                tempList.remove(i);
            } else {
                packageNames.add(packageName);
                i++;
            }
        }

        return tempList.subList(startIndex, tempList.size());
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}

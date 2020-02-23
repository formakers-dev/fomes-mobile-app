package com.formakers.fomes.recommend;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.model.NativeAppInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@RecommendDagger.Scope
public class RecommendPresenter implements RecommendContract.Presenter {

    public final static String TAG = "RecommendPresenter";

    private RecommendListAdapterContract.Model adapterModel;
    private RecommendContract.View view;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private RecommendService recommendService;
    private UserService userService;
    private AndroidNativeHelper androidNativeHelper;
    private AnalyticsModule.Analytics analytics;

    private int currentPage;
    private List<String> installedApps = new ArrayList<>();
    private boolean isAvaiableToLoad = true;

    @Inject
    public RecommendPresenter(RecommendContract.View view, RecommendService recommendService, UserService userService, AndroidNativeHelper androidNativeHelper, AnalyticsModule.Analytics analytics) {
        this.view = view;
        this.recommendService = recommendService;
        this.userService = userService;
        this.androidNativeHelper = androidNativeHelper;
        this.analytics = analytics;
    }

    @Override
    public AnalyticsModule.Analytics getAnalytics() {
        return analytics;
    }

    @Override
    public void setAdapterModel(RecommendListAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void emitShowDetailEvent(RecommendApp recommendApp) {
        this.view.onShowDetailEvent(recommendApp);
    }

    private void refreshInstalledApps() {
        for (RecommendApp app : adapterModel.getAllItems()) {
            app.getAppInfo().setInstalled(installedApps.contains(app.getAppInfo().getPackageName()));
        }
    }

    @Override
    public void loadRecommendApps(String categoryId) {
        final int nextPage = getCurrentPage() + 1;

        if (installedApps.isEmpty()) {
            compositeSubscription.add(
                androidNativeHelper.getInstalledLaunchableApps()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map(NativeAppInfo::getPackageName)
                    .doOnNext(packageName -> installedApps.add(packageName))
                    .doOnCompleted(() -> refreshInstalledApps())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnCompleted(() -> this.view.refreshRecommendList())
                    .subscribe(packageName -> { },  e -> Log.e(TAG, String.valueOf(e)))
            );
        }

        if (!this.isAvaiableToLoad) {
            return;
        }

        compositeSubscription.add(
            recommendService.requestRecommendApps(categoryId, nextPage)
                .observeOn(Schedulers.io())
                .filter(apps -> {
                    boolean isEmpty = apps == null || apps.size() <= 0;
                    this.isAvaiableToLoad = !isEmpty;
                    return this.isAvaiableToLoad;
                })
                .concatMapIterable(i -> i)
                .filter(app -> !adapterModel.contains(app.getAppInfo().getPackageName()))
                .map(app -> {
                    app.getAppInfo().setInstalled(installedApps.contains(app.getAppInfo().getPackageName()));
                    return app;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> this.view.showLoading())
                .doOnTerminate(() -> {
                    this.view.hideLoading();

                    if (adapterModel.getItemCount() <= 0) {
                        this.view.showErrorPage();
                    } else {
                        this.view.showRecommendList();
                    }
                })
                .subscribe(recommendApp -> adapterModel.add(recommendApp),
                    e -> Log.e(TAG, e.toString()),
                    () -> {
                        setCurrentPage(nextPage);
                        this.view.refreshRecommendList();

                        if (this.view.isEndOfRecommendList()) {
                            this.loadRecommendApps("GAME");
                    }
                } )
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}

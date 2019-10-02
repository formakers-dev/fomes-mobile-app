package com.formakers.fomes.recommend;

import com.formakers.fomes.common.network.AppService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.model.AppInfo;

import javax.inject.Inject;

import rx.Completable;
import rx.Single;

@AppInfoDetailDagger.Scope
public class AppInfoDetailPresenter implements AppInfoDetailContract.Presenter {

    private final UserService userService;
    private final AppService appService;

    @Inject
    public AppInfoDetailPresenter(UserService userService, AppService appService) {
        this.userService = userService;
        this.appService = appService;
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
    public Single<AppInfo> requestAppInfo(String packageName) {
        return appService.requestAppInfo(packageName);
    }

}

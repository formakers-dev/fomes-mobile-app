package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.main.contract.AppInfoDetailContract;

import javax.inject.Inject;

import rx.Completable;

public class AppInfoDetailPresenter implements AppInfoDetailContract.Presenter {

    private final UserService userService;

    @Inject
    public AppInfoDetailPresenter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Completable requestSaveToWishList(String packageName) {
        return userService.requestSaveAppToWishList(packageName);
    }

    @Override
    public Completable requestRemoveFromWishList(String packageName) {
        return userService.requestRemoveAppFromWishList(packageName);
    }

}

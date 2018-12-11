package com.formakers.fomes.main.contract;

import com.formakers.fomes.common.mvp.BaseView;

import rx.Completable;

public interface AppInfoDetailContract {
    interface Presenter {
        Completable requestSaveToWishList(String packageName);
        Completable requestRemoveFromWishList(String packageName);
    }

    interface View extends BaseView<Presenter> {
    }
}

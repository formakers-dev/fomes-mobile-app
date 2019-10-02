package com.formakers.fomes.recommend;

import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.common.model.AppInfo;

import rx.Completable;
import rx.Single;

public interface AppInfoDetailContract {
    interface Presenter {
        Completable requestSaveToWishList(String packageName);
        Completable requestRemoveFromWishList(String packageName);

        Single<AppInfo> requestAppInfo(String packageName);
    }

    interface View extends BaseView<Presenter> {
    }
}
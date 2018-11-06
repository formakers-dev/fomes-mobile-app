package com.formakers.fomes.wishList.contract;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.mvp.BaseView;
import com.formakers.fomes.model.AppInfo;

import java.util.List;

import rx.Observable;

public interface WishListContract {
    interface Presenter {
        void emitShowEmptyList();
        void emitRemoveFromWishList(String packageName);
        Observable<List<AppInfo>> emitRequestWishList();
    }

    interface View extends BaseView<Presenter> {
        ApplicationComponent getApplicationComponent();
        void removeApp(String packageName);
        void showToast(String toastMessage);
        void showEmptyList();
    }
}

package com.formakers.fomes.wishList;

import androidx.annotation.StringRes;

import com.formakers.fomes.common.mvp.BaseView;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

public interface WishListContract {
    interface Presenter {
        void loadingWishList();
        void requestRemoveFromWishList(int position);

        String getItemPackageName(int position);
        List<String> getRemovedPackageNames();

        void setAdapterModel(WishListAdapterContract.Model adapterModel);
    }

    interface View extends BaseView<Presenter> {
        void refresh();
        void refresh(int position);

        void showWishList(boolean hasData);
        void showToast(String toastMessage);
        void showToast(@StringRes int toastMessageResId);
        void showLoadingBar();
        void hideLoadingBar();

        // TODO : BaseView 로 이동 고려
        CompositeSubscription getCompositeSubscription();
    }
}

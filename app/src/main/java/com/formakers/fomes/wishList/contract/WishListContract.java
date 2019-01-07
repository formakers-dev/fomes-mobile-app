package com.formakers.fomes.wishList.contract;

import android.support.annotation.StringRes;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.mvp.BaseView;

import java.util.List;

public interface WishListContract {
    interface Presenter {
        void loadingWishList();
        void requestRemoveFromWishList(int position);

        String getItemPackageName(int position);
        List<String> getRemovedPackageNames();
    }

    interface View extends BaseView<Presenter> {
        ApplicationComponent getApplicationComponent();

        void refresh();
        void refresh(int position);

        void showWishList(boolean hasData);
        void showToast(String toastMessage);
        void showToast(@StringRes int toastMessageResId);
        void showLoadingBar();
        void hideLoadingBar();
    }
}

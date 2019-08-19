package com.formakers.fomes.wishList.presenter;

import com.formakers.fomes.R;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.wishList.contract.WishListAdapterContract;
import com.formakers.fomes.wishList.contract.WishListContract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class WishListPresenter implements WishListContract.Presenter {

    @Inject UserService userService;

    private List<String> removedPackageNames = new ArrayList<>();

    private WishListContract.View view;
    private WishListAdapterContract.Model adapterModel;

    public WishListPresenter(WishListContract.View view, WishListAdapterContract.Model adapterModel) {
        this.view = view;
        this.adapterModel = adapterModel;

        // ???
        this.view.getApplicationComponent().inject(this);
    }

    public WishListPresenter(WishListContract.View view, WishListAdapterContract.Model adapterModel, UserService userService) {
        this.view = view;
        this.adapterModel = adapterModel;
        this.userService = userService;
    }

    @Override
    public void loadingWishList() {
        this.view.getCompositeSubscription().add(
                userService.requestWishList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(() -> view.showLoadingBar())
                        .doAfterTerminate(() -> view.hideLoadingBar())
                        .subscribe(appInfos -> {
                            adapterModel.addAll(appInfos);
                            view.refresh();
                            view.showWishList(appInfos.size() > 0);
                        }, e -> {
                            Log.e("Test", e.toString());
                            view.showWishList(false);
                        })
        );
    }

    @Override
    public void requestRemoveFromWishList(final int position) {
        final String packageName = adapterModel.getItem(position).getPackageName();

        this.view.getCompositeSubscription().add(
                userService.requestRemoveAppFromWishList(packageName)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            adapterModel.remove(position);
                            view.refresh(position);
                            view.showWishList(adapterModel.getItemCount() > 0);

                            removedPackageNames.add(packageName);
                        }, e -> view.showToast(R.string.wish_list_remove_fail))
        );
    }

    @Override
    public String getItemPackageName(int position) {
        return adapterModel.getItem(position).getPackageName();
    }

    @Override
    public List<String> getRemovedPackageNames() {
        return removedPackageNames;
    }
}

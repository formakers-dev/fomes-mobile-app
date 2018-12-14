package com.formakers.fomes.wishList.presenter;

import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.model.AppInfo;
import com.formakers.fomes.wishList.contract.WishListContract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WishListPresenter implements WishListContract.Presenter {

    private WishListContract.View view;
    @Inject UserService userService;

    private ArrayList<String> removedPackageNames;

    public WishListPresenter(WishListContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
        removedPackageNames = new ArrayList<>();
    }

    public WishListPresenter(WishListContract.View view, UserService userService) {
        this.view = view;
        this.userService = userService;
        removedPackageNames = new ArrayList<>();
    }

    @Override
    public void emitShowEmptyList() {
        this.view.showEmptyList();
    }

    @Override
    public void requestRemoveFromWishList(String packageName) {
        userService.requestRemoveAppFromWishList(packageName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    view.removeApp(packageName);
                    removedPackageNames.add(packageName);
                }, e -> view.showToast("위시리스트 삭제에 실패하였습니다."));
    }

    @Override
    public Observable<List<AppInfo>> requestWishList() {
        return userService.requestWishList();
    }

    @Override
    public ArrayList<String> getRemovedPackageNames() {
        return removedPackageNames;
    }
}

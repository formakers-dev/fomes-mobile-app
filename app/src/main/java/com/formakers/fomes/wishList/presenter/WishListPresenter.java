package com.formakers.fomes.wishList.presenter;

import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.model.AppInfo;
import com.formakers.fomes.wishList.contract.WishListContract;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WishListPresenter implements WishListContract.Presenter {

    @Inject UserService userService;

    private WishListContract.View view;

    public WishListPresenter(WishListContract.View view) {
        this.view = view;
        this.view.getApplicationComponent().inject(this);
    }

    @Override
    public void emitRemoveFromWishList(String packageName) {
        userService.requestRemoveAppFromWishList(packageName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> view.removeApp(packageName), e -> view.showToast("위시리스트 삭제에 실패하였습니다."));
    }

    @Override
    public Observable<List<AppInfo>> emitRequestWishList() {
        return userService.requestWishList();
    }
}

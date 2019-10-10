package com.formakers.fomes.wishList;

import com.formakers.fomes.R;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@WishListDagger.Scope
public class WishListPresenter implements WishListContract.Presenter {

    private UserService userService;

    private List<String> removedPackageNames = new ArrayList<>();
    private ImageLoader imageLoader;

    private WishListContract.View view;
    private WishListAdapterContract.Model adapterModel;

    @Inject
    public WishListPresenter(WishListContract.View view, UserService userService, ImageLoader imageLoader) {
        this.view = view;
        this.userService = userService;
        this.imageLoader = imageLoader;
    }

    @Override
    public ImageLoader getImageLoader() {
        return this.imageLoader;
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

    @Override
    public void setAdapterModel(WishListAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }
}

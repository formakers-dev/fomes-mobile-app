package com.formakers.fomes.more;

import android.util.Pair;

import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@MenuListDagger.Scope
public class MenuListPresenter implements MenuListContract.Presenter {

    public static final String TAG = "MenuListPresenter";

    private Single<String> userEmail;
    private Single<String> userNickName;

    private MenuListContract.View view;

    @Inject
    public MenuListPresenter(MenuListContract.View view,
                             @Named("userEmail") Single<String> userEmail,
                             @Named("userNickName") Single<String> userNickName) {
        this.view = view;
        this.userEmail = userEmail;
        this.userNickName = userNickName;
    }

    @Override
    public void bindUserInfo() {
        userEmail.zipWith(userNickName, Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(emailNickNamePair -> this.view.setUserInfo(emailNickNamePair.first, emailNickNamePair.second),
                        e -> Log.e(TAG, String.valueOf(e)));
    }
}

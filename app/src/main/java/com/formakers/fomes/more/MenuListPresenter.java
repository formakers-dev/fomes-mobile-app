package com.formakers.fomes.more;

import android.util.Pair;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.PointService;
import com.formakers.fomes.common.util.Log;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

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
    private BetaTestService betaTestService;
    private PointService pointService;
    private FirebaseRemoteConfig remoteConfig;

    private MenuListContract.View view;

    @Inject
    public MenuListPresenter(MenuListContract.View view,
                             @Named("userEmail") Single<String> userEmail,
                             @Named("userNickName") Single<String> userNickName,
                             BetaTestService betaTestService,
                             PointService pointService,
                             FirebaseRemoteConfig remoteConfig) {
        this.view = view;
        this.userEmail = userEmail;
        this.userNickName = userNickName;
        this.betaTestService = betaTestService;
        this.pointService = pointService;
        this.remoteConfig = remoteConfig;
    }

    @Override
    public void bindUserInfo() {
        userEmail.zipWith(userNickName, Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(emailNickNamePair -> this.view.setUserInfo(emailNickNamePair.first, emailNickNamePair.second),
                        e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public void bindCompletedBetaTestsCount() {
        betaTestService.getCompletedBetaTestsCount()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> this.view.setCompletedBetaTestsCount(count), e -> Log.e(TAG, String.valueOf(e)));
    }

    @Override
    public void bindAvailablePoint() {
        this.pointService.getAvailablePoint()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> this.view.showAvailablePointLoading())
                .doAfterTerminate(() -> this.view.hideAvailablePointLoading())
                .subscribe(point -> {
                    this.view.setAvailablePoint(point);
                }, e -> Log.e(TAG, String.valueOf(e)));
    }

    //TODO : 광고 피처 제거시 조건부 로직 삭제 필요
    @Override
    public boolean isActivatedAdvertising() {
        if (this.remoteConfig.getBoolean(FomesConstants.RemoteConfig.FEATURE_ADVERTISING)) {
            return true;
        }

        String email = userEmail.toBlocking().value();

        return "sryu99@gmail.com".equals(email) || "yenarue@gmail.com".equals(email);
    }
}

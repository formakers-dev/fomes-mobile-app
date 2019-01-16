package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.BetaTestAPI;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.helper.SharedPreferencesHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class BetaTestService extends AbstractService {

    private static final String TAG = "BetaTestService";

    private final BetaTestAPI betaTestAPI;
    private final SharedPreferencesHelper sharedPreferencesHelper;

    @Inject
    public BetaTestService(BetaTestAPI betaTestAPI, SharedPreferencesHelper sharedPreferencesHelper) {
        this.betaTestAPI = betaTestAPI;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public Single<List<BetaTest>> getBetaTestList() {
        return Observable.defer(() -> betaTestAPI.getBetaTests(sharedPreferencesHelper.getAccessToken()))
        .subscribeOn(Schedulers.io())
        .toSingle();
    }
}

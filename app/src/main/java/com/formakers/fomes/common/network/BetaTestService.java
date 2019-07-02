package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.BetaTestAPI;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.helper.APIHelper;
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
    private final APIHelper apiHelper;

    @Inject
    public BetaTestService(BetaTestAPI betaTestAPI, SharedPreferencesHelper sharedPreferencesHelper, APIHelper apiHelper) {
        this.betaTestAPI = betaTestAPI;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        this.apiHelper = apiHelper;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public Single<List<BetaTest>> getBetaTestList() {
        return Observable.defer(() -> betaTestAPI.getBetaTests(sharedPreferencesHelper.getAccessToken()))
                .subscribeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toSingle();
    }

    public Single<List<BetaTest>> getFinishedBetaTestList() {
        return Observable.defer(() -> betaTestAPI.getFinishedBetaTests(sharedPreferencesHelper.getAccessToken()))
                .subscribeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toSingle();
    }

    public Single<BetaTest> getDetailBetaTest(String id) {
        return Observable.defer(() -> betaTestAPI.getDetailBetaTest(sharedPreferencesHelper.getAccessToken(), id))
                .subscribeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toSingle();
    }

    public Single<Void> postCompleteBetaTest(String id) {
        return Observable.defer(() -> betaTestAPI.postCompleteBetaTest(sharedPreferencesHelper.getAccessToken(), id))
                .subscribeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toSingle();
    }
}

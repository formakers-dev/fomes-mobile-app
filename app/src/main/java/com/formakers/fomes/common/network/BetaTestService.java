package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.BetaTestAPI;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.util.DateUtil;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.helper.APIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;

import java.util.Date;
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
                .map(response -> {
                    // Date 헤더: 메시지가 만들어진 날짜와 시간
                    Date currentDate = DateUtil.convertHTTPHeaderDateToDate(response.headers().get("Date"));
                    List<BetaTest> betaTests = response.body();

                    Log.d(TAG, "getBetaTestList current date: " + currentDate);
                    if (betaTests != null) {
                        for(BetaTest betaTest : betaTests) {
                            betaTest.setCurrentDate(currentDate);
                        }
                    }

                    return betaTests;
                })
                .compose(apiHelper.refreshExpiredToken())
                .toSingle();
    }

    public Single<List<BetaTest>> getFinishedBetaTestList() {
        return Observable.defer(() -> betaTestAPI.getFinishedBetaTests(sharedPreferencesHelper.getAccessToken()))
                .subscribeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toSingle();
    }
}

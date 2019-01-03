package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.BetaTestRequestAPI;
import com.formakers.fomes.common.network.vo.BetaTestRequest;
import com.formakers.fomes.helper.SharedPreferencesHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class RequestService extends AbstractService {

    private static final String TAG = "RequestService";

    private final BetaTestRequestAPI betaTestRequestAPI;
    private final SharedPreferencesHelper sharedPreferencesHelper;

    @Inject
    public RequestService(BetaTestRequestAPI betaTestRequestAPI, SharedPreferencesHelper sharedPreferencesHelper) {
        this.betaTestRequestAPI = betaTestRequestAPI;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public Single<List<BetaTestRequest>> getFeedbackRequest() {
        return Observable.defer(() -> betaTestRequestAPI.getRequests(sharedPreferencesHelper.getAccessToken()))
        .subscribeOn(Schedulers.io())
        .toSingle();
    }
}

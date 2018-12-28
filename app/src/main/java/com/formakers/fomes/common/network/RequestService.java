package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.RequestAPI;
import com.formakers.fomes.common.network.vo.FeedbackRequest;
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

    private final RequestAPI requestAPI;
    private final SharedPreferencesHelper sharedPreferencesHelper;

    @Inject
    public RequestService(RequestAPI requestAPI, SharedPreferencesHelper sharedPreferencesHelper) {
        this.requestAPI = requestAPI;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public Single<List<FeedbackRequest>> getFeedbackRequest() {
        return Observable.defer(() -> requestAPI.getRequests(sharedPreferencesHelper.getAccessToken()))
        .subscribeOn(Schedulers.io())
        .toSingle();
    }
}

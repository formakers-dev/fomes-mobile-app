package com.formakers.fomes.common.network;

import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.model.FomesPoint;
import com.formakers.fomes.common.network.api.PointAPI;
import com.formakers.fomes.common.network.helper.APIHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

@Singleton
public class PointService extends AbstractService {

    private static final String TAG = "PointService";

    private final PointAPI pointAPI;
    private final SharedPreferencesHelper sharedPreferencesHelper;
    private final APIHelper apiHelper;

    @Inject
    public PointService(PointAPI pointAPI, SharedPreferencesHelper sharedPreferencesHelper, APIHelper apiHelper) {
        this.pointAPI = pointAPI;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        this.apiHelper = apiHelper;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public Single<Long> getAvailablePoint() {
        return Observable.defer(() -> pointAPI.getAvailablePoint(sharedPreferencesHelper.getAccessToken()))
                .subscribeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toSingle()
                .map(pointResponseVO -> pointResponseVO.point);
    }

    public Observable<FomesPoint> getPointHistory() {
        return Observable.defer(() -> pointAPI.getPointHistory(sharedPreferencesHelper.getAccessToken()))
                .subscribeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .flatMap(Observable::from);
    }

    public Completable requestWithdraw(FomesPoint point) {
        return Observable.defer(() -> pointAPI.putPointWithdraw(sharedPreferencesHelper.getAccessToken(), point))
                .subscribeOn(Schedulers.io())
                .compose(apiHelper.refreshExpiredToken())
                .toCompletable();
    }
}

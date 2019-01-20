package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.EventLogAPI;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.helper.SharedPreferencesHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

@Singleton
public class EventLogService extends AbstractService {

    private static final String TAG = "EventLogService";

    private final EventLogAPI eventLogAPI;
    private final SharedPreferencesHelper sharedPreferencesHelper;

    @Inject
    public EventLogService(EventLogAPI eventLogAPI, SharedPreferencesHelper sharedPreferencesHelper) {
        this.eventLogAPI = eventLogAPI;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public Completable sendEventLog(EventLog eventLog) {
        return Observable.defer(() -> eventLogAPI.postEventLog(sharedPreferencesHelper.getAccessToken(), eventLog))
                .doOnError(this::logError)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .toCompletable();
    }
}

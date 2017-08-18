package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.util.AppBeeConstants;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observer;
import rx.schedulers.Schedulers;

public class AppService {

    private final AppAPI appAPI;

    @Inject
    public AppService(AppAPI appAPI) {
        this.appAPI = appAPI;
    }

    public void getInfos(List<String> packageNames, AppInfosServiceCallback appInfosServiceCallback) {
        appAPI.getInfo(packageNames).subscribeOn(Schedulers.io()).subscribe(
                new Observer<Response<List<AppInfo>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
//                        appInfosServiceCallback.onFail(String.valueOf(((HttpException) e).code()));
                    }

                    @Override
                    public void onNext(Response<List<AppInfo>> listResponse) {
                        if(listResponse.isSuccessful()) {
                            appInfosServiceCallback.onSuccess(listResponse.body());
                        } else {
                            appInfosServiceCallback.onFail(AppBeeConstants.API_RESPONSE_CODE.FORBIDDEN);
                        }
                    }
                }
        );
    }

    public interface AppInfosServiceCallback {
        void onSuccess(List<AppInfo> result);
        void onFail(String resultCode);
    }
}
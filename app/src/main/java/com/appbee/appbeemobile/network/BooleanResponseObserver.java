package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.util.AppBeeConstants;

import retrofit2.Response;
import rx.Observer;

public class BooleanResponseObserver implements Observer<Response<Boolean>> {
    protected ServiceCallback serviceCallback;

    public BooleanResponseObserver(ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        serviceCallback.onFail(AppBeeConstants.API_RESPONSE_CODE.FORBIDDEN);
    }

    @Override
    public void onNext(Response<Boolean> booleanResponse) {
        if(booleanResponse.isSuccessful()){
            serviceCallback.onSuccess();
        }else {
            serviceCallback.onFail(String.valueOf(booleanResponse.code()));
        }
    }
}

package com.formakers.fomes.helper;

import com.formakers.fomes.common.util.Log;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Single;

@Singleton
public class FomesUrlHelper {

    private static final String TAG = "FomesUrlHelper";
    private Single<String> userEmail;

    @Inject
    public FomesUrlHelper(@Named("userEmail") Single<String> userEmail) {
        this.userEmail = userEmail;
    }

    public String interpretUrlParams(String originalUrl) {
        Log.v(TAG, "original=" + originalUrl);
        String interpretedUrl = originalUrl.replace("{email}", this.userEmail.toBlocking().value());
        return interpretedUrl;
    }

}

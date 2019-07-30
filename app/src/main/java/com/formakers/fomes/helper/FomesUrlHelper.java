package com.formakers.fomes.helper;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Single;

@Singleton
public class FomesUrlHelper {

    private Single<String> userEmail;

    @Inject
    public FomesUrlHelper(@Named("userEmail") Single<String> userEmail) {
        this.userEmail = userEmail;
    }

    public String interpretUrlParams(String originalUrl) {
        String interpretedUrl = originalUrl.replace("{email}", this.userEmail.toBlocking().value());
        return interpretedUrl;
    }

}

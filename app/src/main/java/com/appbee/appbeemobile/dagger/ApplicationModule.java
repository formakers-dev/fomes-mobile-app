package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.network.HTTPService;
import com.appbee.appbeemobile.network.RetrofitCreator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final AppBeeApplication application;

    public ApplicationModule(AppBeeApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    HTTPService provideHTTPService() {
        return RetrofitCreator.createRetrofit().create(HTTPService.class);
    }
}

package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.manager.GoogleSignInAPIManager;
import com.appbee.appbeemobile.network.HTTPService;
import com.appbee.appbeemobile.network.UserAPI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ApplicationModule {
    private final AppBeeApplication application;

    public ApplicationModule(AppBeeApplication application) {
        this.application = application;
    }

    @Singleton
    @Provides HTTPService provideHTTPService(Retrofit retrofit) {
        return retrofit.create(HTTPService.class);
    }

    @Singleton
    @Provides UserAPI provideUserAPI(Retrofit retrofit) {
        return retrofit.create(UserAPI.class);
    }

    @Singleton
    @Provides
    GoogleSignInAPIManager provideSignInManger() {
        return new GoogleSignInAPIManager();
    }
}

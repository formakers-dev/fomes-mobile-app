package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;
import com.appbee.appbeemobile.network.StatAPI;
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
    @Provides
    StatAPI provideHTTPService(Retrofit retrofit) {
        return retrofit.create(StatAPI.class);
    }

    @Singleton
    @Provides UserAPI provideUserAPI(Retrofit retrofit) {
        return retrofit.create(UserAPI.class);
    }

    @Singleton
    @Provides
    GoogleSignInAPIHelper provideSignInManger() {
        return new GoogleSignInAPIHelper();
    }
}

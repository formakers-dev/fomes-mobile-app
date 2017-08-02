package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.network.AppBeeAccountService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.network.StatAPI;
import com.appbee.appbeemobile.network.UserAPI;
import com.appbee.appbeemobile.helper.LocalStorageHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import static org.mockito.Mockito.mock;

@Module
public class TestApplicationModule {
    private final TestAppBeeApplication application;

    public TestApplicationModule(TestAppBeeApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    AppUsageDataHelper provideAppUsageDataHelper() {
        return mock(AppUsageDataHelper.class);
    }

    @Provides
    @Singleton
    AppBeeAndroidNativeHelper provideAppBeeAndroidNativeHelper() {
        return mock(AppBeeAndroidNativeHelper.class);
    }

    @Provides
    @Singleton
    AppStatService provideUserAppsService() {
        return mock(AppStatService.class);
    }

    @Provides
    @Singleton
    StatAPI provideStatAPI() {
        return mock(StatAPI.class);
    }

    @Singleton
    @Provides
    UserAPI provideUserAPI() {
        return mock(UserAPI.class);
    }

    @Singleton
    @Provides
    AppBeeAccountService provideAppBeeAccountService() {
        return mock(AppBeeAccountService.class);
    }

    @Singleton
    @Provides
    GoogleSignInAPIHelper provideGoogleSignInAPIHelper() {
        return mock(GoogleSignInAPIHelper.class);
    }

    @Singleton
    @Provides
    LocalStorageHelper provideLocalStorageHelper() {
        return mock(LocalStorageHelper.class);
    }
}

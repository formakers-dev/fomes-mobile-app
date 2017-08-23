package com.appbee.appbeemobile.dagger;

import android.content.Context;

import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.AppAPI;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.StatAPI;
import com.appbee.appbeemobile.network.UserAPI;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

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

    @Singleton
    @Provides
    Context context() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    AppUsageDataHelper appUsageDataHelper() {
        return mock(AppUsageDataHelper.class);
    }

    @Provides
    @Singleton
    AppBeeAndroidNativeHelper appBeeAndroidNativeHelper() {
        return mock(AppBeeAndroidNativeHelper.class);
    }

    @Provides
    @Singleton
    AppStatService appStatService() {
        return mock(AppStatService.class);
    }

    @Provides
    @Singleton
    StatAPI statAPI() {
        return mock(StatAPI.class);
    }

    @Singleton
    @Provides
    UserAPI userAPI() {
        return mock(UserAPI.class);
    }


    @Singleton
    @Provides
    AppAPI appAPI() {
        return mock(AppAPI.class);
    }

    @Singleton
    @Provides
    AppService appService() {
        return mock(AppService.class);
    }

    @Singleton
    @Provides
    UserService userService() {
        return mock(UserService.class);
    }

    @Singleton
    @Provides
    LocalStorageHelper localStorageHelper() {
        return mock(LocalStorageHelper.class);
    }

    @Singleton
    @Provides
    AppRepositoryHelper appRepositoryHelper() {
        return mock(AppRepositoryHelper.class);
    }
}

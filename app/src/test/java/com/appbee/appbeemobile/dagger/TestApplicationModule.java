package com.appbee.appbeemobile.dagger;

import android.content.Context;

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
    AppBeeAccountService appBeeAccountService() {
        return mock(AppBeeAccountService.class);
    }

    @Singleton
    @Provides
    GoogleSignInAPIHelper googleSignInAPIHelper() {
        return mock(GoogleSignInAPIHelper.class);
    }

    @Singleton
    @Provides
    LocalStorageHelper localStorageHelper() {
        return mock(LocalStorageHelper.class);
    }
}

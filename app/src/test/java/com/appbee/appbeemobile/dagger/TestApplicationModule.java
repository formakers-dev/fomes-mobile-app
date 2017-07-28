package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.network.AppBeeAccountService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.manager.GoogleSignInAPIManager;
import com.appbee.appbeemobile.manager.SystemServiceBridge;
import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.network.StatAPI;
import com.appbee.appbeemobile.network.UserAPI;

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
    StatManager provideStatManager() {
        return mock(StatManager.class);
    }

    @Provides
    @Singleton
    SystemServiceBridge provideSystemServiceBridge() {
        return mock(SystemServiceBridge.class);
    }

    @Provides
    @Singleton
    AppStatService provideUserAppsService() {
        return mock(AppStatService.class);
    }

    @Provides
    @Singleton
    StatAPI provideHTTPService() {
        return mock(StatAPI.class);
    }

    @Singleton
    @Provides
    UserAPI provideUserAPI() {
        return mock(UserAPI.class);
    }

    @Singleton
    @Provides
    AppBeeAccountService provideAppBeeAccountManager() {
        return mock(AppBeeAccountService.class);
    }

    @Singleton
    @Provides
    GoogleSignInAPIManager provideGoogleSignInAPIManger() {
        return mock(GoogleSignInAPIManager.class);
    }
}

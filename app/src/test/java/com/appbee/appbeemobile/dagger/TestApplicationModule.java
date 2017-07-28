package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.manager.AppStatServiceManager;
import com.appbee.appbeemobile.manager.SystemServiceBridge;
import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.network.HTTPService;

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
    AppStatServiceManager provideUserAppsService() {
        return mock(AppStatServiceManager.class);
    }

    @Provides
    @Singleton
    HTTPService provideHTTPService() {
        return mock(HTTPService.class);
    }
}

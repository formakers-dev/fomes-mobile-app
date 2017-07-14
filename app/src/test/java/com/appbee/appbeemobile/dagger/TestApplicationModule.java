package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.TestAppBeeApplication;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestApplicationModule {
    private final TestAppBeeApplication application;

    public TestApplicationModule(TestAppBeeApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    StatManager provideStatManager() {
        return Mockito.mock(StatManager.class);
    }
}

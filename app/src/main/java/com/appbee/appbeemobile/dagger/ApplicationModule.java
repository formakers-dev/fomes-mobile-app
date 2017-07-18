package com.appbee.appbeemobile.dagger;

import android.app.Application;

import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.manager.UsingPackageManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    StatManager provideStatManager() {
        return new StatManager(application.getApplicationContext());
    }

    @Provides
    @Singleton
    UsingPackageManager provideUsingPackageManager() {
        return new UsingPackageManager(application.getApplicationContext());
    }
}

package com.appbee.appbeemobile.dagger;

import android.app.Application;
import android.content.Context;

import com.appbee.appbeemobile.manager.SystemServiceBridge;
import com.appbee.appbeemobile.manager.StatManager;

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
    StatManager provideStatManager(Context context, SystemServiceBridge systemServiceBridge) {
        return new StatManager(application.getApplicationContext(), systemServiceBridge);
    }

    @Provides
    @Singleton
    SystemServiceBridge provideSystemServiceBridge() {
        return new SystemServiceBridge(application.getApplicationContext());
    }
}

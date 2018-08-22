package com.formakers.fomes.dagger;

import android.content.Context;

import com.formakers.fomes.AppBeeApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final AppBeeApplication application;

    public ApplicationModule(AppBeeApplication application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Context context() {
        return application.getApplicationContext();
    }
}

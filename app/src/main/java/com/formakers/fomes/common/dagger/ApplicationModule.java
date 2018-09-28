package com.formakers.fomes.common.dagger;

import android.content.Context;

import com.formakers.fomes.FomesApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final FomesApplication application;

    public ApplicationModule(FomesApplication application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Context context() {
        return application.getApplicationContext();
    }
}

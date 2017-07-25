package com.appbee.appbeemobile.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestContextModule {

    private final Context context;

    public TestContextModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    Context context() {
        return context;
    }
}

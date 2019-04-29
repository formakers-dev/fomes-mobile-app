package com.formakers.fomes.common.dagger;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = { ApplicationModule.class })
public class AnalyticsModule {
    @Singleton
    @Provides
    public FirebaseAnalytics firebaseAnalytics(Context context) {
        return FirebaseAnalytics.getInstance(context);
    }
}

package com.appbee.appbeemobile.dagger;

import android.content.Context;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.helper.GoogleSignInAPIHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

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

    @Singleton
    @Provides
    GoogleSignInAPIHelper googleSignInAPIHelper() {
        return new GoogleSignInAPIHelper();
    }

    @Singleton
    @Provides
    Realm realm(Context context) {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .name("appbeeDB")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        return Realm.getDefaultInstance();
    }
}

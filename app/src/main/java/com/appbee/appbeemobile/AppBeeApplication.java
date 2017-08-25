package com.appbee.appbeemobile;

import android.app.Application;
import android.graphics.Typeface;

import com.appbee.appbeemobile.dagger.ApplicationComponent;
import com.appbee.appbeemobile.dagger.ApplicationModule;
import com.appbee.appbeemobile.dagger.DaggerApplicationComponent;
import com.appbee.appbeemobile.dagger.NetworkModule;
import com.tsengvn.typekit.Typekit;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppBeeApplication extends Application {
    protected ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        initRealm();
        initFont();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    protected void initFont() {
        Typekit.getInstance()
                .addNormal(Typeface.SANS_SERIF)
                .addBold(Typeface.DEFAULT_BOLD)
                .add("BMJUA", Typekit.createFromAsset(this, "BMJUA.otf"));
    }

    protected void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .name("appbeeDB")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }
}

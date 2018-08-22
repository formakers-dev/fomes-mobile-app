package com.formakers.fomes;

import android.app.Application;

import com.formakers.fomes.dagger.ApplicationComponent;
import com.formakers.fomes.dagger.ApplicationModule;
import com.formakers.fomes.dagger.DaggerApplicationComponent;
import com.formakers.fomes.dagger.NetworkModule;
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
                .addNormal(Typekit.createFromAsset(this, "NotoSans-Regular.otf"))
                .addBold(Typekit.createFromAsset(this, "NotoSans-Bold.otf"))
                .add("medium", Typekit.createFromAsset(this, "NotoSans-Medium.otf"))
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

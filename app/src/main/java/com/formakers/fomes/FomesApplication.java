package com.formakers.fomes;

import android.app.Activity;
import android.app.Application;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.dagger.ApplicationModule;
import com.formakers.fomes.common.dagger.DaggerApplicationComponent;
import com.tsengvn.typekit.Typekit;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FomesApplication extends Application {
    protected ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        initRealm();
        initFont();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    protected void initFont() {
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NotoSans-Regular.otf"))
                .addBold(Typekit.createFromAsset(this, "NotoSans-Bold.otf"))
                .add(getString(R.string.font_medium), Typekit.createFromAsset(this, "NotoSans-Medium.otf"))
                .add(getString(R.string.font_bmjua), Typekit.createFromAsset(this, "BMJUA.otf"));
    }

    protected void initRealm() {
        Realm.init(this);
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder();

        if (BuildConfig.DEBUG) {
            builder.directory(getExternalFilesDir(null));
        }

        RealmConfiguration config = builder
                .name("appbeeDB")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }

    public static FomesApplication get(Activity activity) {
        return (FomesApplication) activity.getApplication();
    }
}

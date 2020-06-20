package com.formakers.fomes;

import android.app.Activity;
import android.app.Application;

import com.formakers.fomes.common.dagger.ApplicationComponent;
import com.formakers.fomes.common.dagger.ApplicationModule;
import com.formakers.fomes.common.dagger.DaggerApplicationComponent;
import com.formakers.fomes.common.repository.model.UserRealmObject;
import com.formakers.fomes.common.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FomesApplication extends Application {

    public static final String TAG = "FomesApplication";

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
//        Typekit.getInstance()
//                .addNormal(Typekit.createFromAsset(this, "font/noto_sans_regular.otf"))
//                .addBold(Typekit.createFromAsset(this, "font/noto_sans_bold.otf"))
//                .add(getString(R.string.font_medium), Typekit.createFromAsset(this, "font/noto_sans_medium.otf"));
    }

    protected void initRealm() {
        Realm.init(this);
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder();

        if (BuildConfig.DEBUG) {
            builder.directory(getExternalFilesDir(null));
        }

        RealmConfiguration config = builder
                .name("appbeeDB")
                .schemaVersion(1)
                .migration((realm, oldVersion, newVersion) -> {
                    UserRealmObject.migration(realm.getSchema(), oldVersion, newVersion);
                })
                .build();

        Realm.setDefaultConfiguration(config);

        Log.i(TAG, "Realm Schema Version=" + Realm.getDefaultInstance().getVersion());
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }

    public static FomesApplication get(Activity activity) {
        return (FomesApplication) activity.getApplication();
    }
}

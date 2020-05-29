package com.formakers.fomes.common.dagger;

import android.content.Context;
import android.widget.Toast;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.util.Log;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private static final String TAG = "ApplicationModule";
    private final FomesApplication application;

    public ApplicationModule(FomesApplication application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Context context() {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    FirebaseRemoteConfig firebaseRemoteConfig(Context context) {
        Log.i(TAG, "firebaseRemoteConfig");
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();

        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_default);

        remoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean updated = task.getResult();
                Log.d("FirebaseRemoteConfig", "Config params updated: " + updated);
                if (BuildConfig.DEBUG) {
                    Log.v("FirebaseRemoteConfig", "Config params updated: " + remoteConfig.getAll());
                    Toast.makeText(context, "[Firebase Remote Config] Fetch and activate succeeded", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, "[Firebase Remote Config] Fetch and activate failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return remoteConfig;
    }
}

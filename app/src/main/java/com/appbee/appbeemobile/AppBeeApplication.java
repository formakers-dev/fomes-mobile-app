package com.appbee.appbeemobile;

import android.app.Application;

import com.appbee.appbeemobile.dagger.ApplicationComponent;
import com.appbee.appbeemobile.dagger.ApplicationModule;
import com.appbee.appbeemobile.dagger.DaggerApplicationComponent;
import com.appbee.appbeemobile.dagger.NetworkModule;

public class AppBeeApplication extends Application {
    protected ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }
}

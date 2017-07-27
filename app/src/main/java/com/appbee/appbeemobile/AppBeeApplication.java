package com.appbee.appbeemobile;

import android.app.Application;

import com.appbee.appbeemobile.dagger.ApplicationComponent;
import com.appbee.appbeemobile.dagger.ApplicationModule;
import com.appbee.appbeemobile.dagger.ContextModule;
import com.appbee.appbeemobile.dagger.DaggerApplicationComponent;

public class AppBeeApplication extends Application {
    protected ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .contextModule(new ContextModule(getApplicationContext()))
                .build();

        applicationComponent.inject(this);
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }
}

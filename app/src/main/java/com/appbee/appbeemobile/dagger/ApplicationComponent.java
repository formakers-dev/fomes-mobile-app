package com.appbee.appbeemobile.dagger;

import android.app.Application;

import com.appbee.appbeemobile.activity.MainActivity;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, ContextModule.class })
public interface ApplicationComponent {
    void inject(Application appBeeApplication);
    void inject(MainActivity mainActivity);
}

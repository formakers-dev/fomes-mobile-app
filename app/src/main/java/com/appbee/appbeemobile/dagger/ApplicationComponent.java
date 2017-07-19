package com.appbee.appbeemobile.dagger;

import android.app.Application;

import com.appbee.appbeemobile.activity.MainActivity;
import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.receiver.ScreenOffReceiver;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(Application appBeeApplication);
    void inject(MainActivity mainActivity);
    void inject(StatManager statManager);
    void inject(ScreenOffReceiver screenOffReceiver);
}

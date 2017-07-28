package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.LoginActivity;
import com.appbee.appbeemobile.activity.MainActivity;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, ContextModule.class, NetworkModule.class })
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);
    void inject(LoginActivity loginActivity);
}

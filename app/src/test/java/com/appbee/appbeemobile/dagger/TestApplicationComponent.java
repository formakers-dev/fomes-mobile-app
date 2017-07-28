package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.LoginActivityTest;
import com.appbee.appbeemobile.activity.MainActivityTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { TestApplicationModule.class, TestContextModule.class })
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(MainActivityTest mainActivity);
    void inject(LoginActivityTest loginActivity);
}

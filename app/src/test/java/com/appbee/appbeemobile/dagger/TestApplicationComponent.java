package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.MainActivityTest;
import com.appbee.appbeemobile.manager.StatManagerTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TestApplicationModule.class)
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(MainActivityTest mainActivity);
    void inject(StatManagerTest statManagerTest);
}

package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.MainActivityTest;
import com.appbee.appbeemobile.manager.StatManagerTest;
import com.appbee.appbeemobile.receiver.ScreenOffReceiverTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { TestApplicationModule.class, TestContextModule.class })
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(MainActivityTest mainActivity);
}

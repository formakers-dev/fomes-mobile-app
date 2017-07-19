package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.MainActivityTest;
import com.appbee.appbeemobile.manager.StatManagerTest;
import com.appbee.appbeemobile.receiver.ScreenOffReceiverTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TestApplicationModule.class)
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(MainActivityTest mainActivity);
    void inject(StatManagerTest statManagerTest);
    void inject(ScreenOffReceiverTest screenOffReceiverTest);
}

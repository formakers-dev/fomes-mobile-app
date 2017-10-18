package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.LoginActivityTest;
import com.appbee.appbeemobile.activity.MainActivityTest;
import com.appbee.appbeemobile.activity.StartActivityTest;
import com.appbee.appbeemobile.service.PowerConnectedServiceTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestApplicationModule.class})
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(MainActivityTest mainActivity);

    void inject(StartActivityTest startActivityTest);

    void inject(PowerConnectedServiceTest powerConnectedServiceTest);

    void inject(LoginActivityTest loginActivityTest);
}

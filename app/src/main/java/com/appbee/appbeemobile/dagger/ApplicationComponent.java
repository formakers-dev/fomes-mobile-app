package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.LoadingActivity;
import com.appbee.appbeemobile.activity.LoginActivity;
import com.appbee.appbeemobile.activity.PermissionGuideActivity;
import com.appbee.appbeemobile.receiver.PowerConnectedReceiver;
import com.appbee.appbeemobile.service.InstanceIDService;
import com.appbee.appbeemobile.service.PowerConnectedService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, NetworkModule.class })
public interface ApplicationComponent {
    void inject(LoadingActivity loadingActivity);
    void inject(PowerConnectedReceiver powerConnectedReceiver);
    void inject(PermissionGuideActivity permissionGuideActivity);
    void inject(PowerConnectedService powerConnectedService);
    void inject(LoginActivity loginActivity);
    void inject(InstanceIDService instanceIDService);
}

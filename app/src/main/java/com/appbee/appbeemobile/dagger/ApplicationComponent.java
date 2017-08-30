package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.AnalysisResultActivity;
import com.appbee.appbeemobile.activity.MainActivity;
import com.appbee.appbeemobile.activity.StartActivity;
import com.appbee.appbeemobile.fragment.ShareFragment;
import com.appbee.appbeemobile.receiver.PowerConnectedReceiver;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, NetworkModule.class })
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);
    void inject(AnalysisResultActivity analysisResultActivity);
    void inject(ShareFragment shareFragment);
    void inject(PowerConnectedReceiver powerConnectedReceiver);
    void inject(StartActivity startActivity);
}

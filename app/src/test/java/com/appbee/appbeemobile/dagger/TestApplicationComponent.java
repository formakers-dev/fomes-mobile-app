package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.AnalysisResultActivityTest;
import com.appbee.appbeemobile.activity.MainActivityTest;
import com.appbee.appbeemobile.activity.StartActivityTest;
import com.appbee.appbeemobile.fragment.ShareFragmentTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { TestApplicationModule.class })
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(MainActivityTest mainActivity);
    void inject(AnalysisResultActivityTest analysisResultActivityTest);
    void inject(ShareFragmentTest shareFragmentTest);
    void inject(StartActivityTest startActivityTest);
}

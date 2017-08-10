package com.appbee.appbeemobile;

import com.appbee.appbeemobile.dagger.DaggerTestApplicationComponent;
import com.appbee.appbeemobile.dagger.TestApplicationComponent;
import com.appbee.appbeemobile.dagger.TestApplicationModule;

public class TestAppBeeApplication extends AppBeeApplication {
    private TestApplicationComponent testApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        testApplicationComponent = DaggerTestApplicationComponent.builder()
                .testApplicationModule(new TestApplicationModule(this))
                .build();
    }

    public TestApplicationComponent getComponent() {
        return testApplicationComponent;
    }
}

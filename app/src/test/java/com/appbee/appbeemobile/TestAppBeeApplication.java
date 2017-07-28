package com.appbee.appbeemobile;

import com.appbee.appbeemobile.dagger.ApplicationComponent;
import com.appbee.appbeemobile.dagger.DaggerTestApplicationComponent;
import com.appbee.appbeemobile.dagger.TestApplicationComponent;
import com.appbee.appbeemobile.dagger.TestApplicationModule;
import com.appbee.appbeemobile.dagger.TestContextModule;

public class TestAppBeeApplication extends AppBeeApplication {
    private TestApplicationComponent testApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        testApplicationComponent = DaggerTestApplicationComponent.builder()
                .testApplicationModule(new TestApplicationModule(this))
                .testContextModule(new TestContextModule(this))
                .build();
    }

    public TestApplicationComponent getComponent() {
        return testApplicationComponent;
    }
}

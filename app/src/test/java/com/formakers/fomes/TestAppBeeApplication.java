package com.formakers.fomes;

import com.formakers.fomes.dagger.DaggerTestApplicationComponent;
import com.formakers.fomes.dagger.TestApplicationComponent;
import com.formakers.fomes.dagger.TestApplicationModule;

public class TestAppBeeApplication extends AppBeeApplication {
    private TestApplicationComponent testApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        testApplicationComponent = DaggerTestApplicationComponent.builder()
                .testApplicationModule(new TestApplicationModule(this))
                .build();
    }

    @Override
    protected void initRealm() {

    }

    @Override
    protected void initFont() {

    }

    public TestApplicationComponent getComponent() {
        return testApplicationComponent;
    }
}
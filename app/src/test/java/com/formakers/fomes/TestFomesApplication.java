package com.formakers.fomes;

import com.formakers.fomes.common.dagger.DaggerTestApplicationComponent;
import com.formakers.fomes.common.dagger.TestApplicationComponent;
import com.formakers.fomes.common.dagger.TestApplicationModule;

public class TestFomesApplication extends FomesApplication {
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

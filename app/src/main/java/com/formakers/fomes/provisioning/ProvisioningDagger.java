package com.formakers.fomes.provisioning;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class ProvisioningDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private ProvisioningContract.View view;

        public Module(ProvisioningContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        ProvisioningContract.Presenter presenter(ProvisioningPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        ProvisioningContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(ProvisioningActivity activity);
    }
}

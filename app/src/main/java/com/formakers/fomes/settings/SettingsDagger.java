package com.formakers.fomes.settings;

import com.formakers.fomes.common.dagger.ApplicationComponent;

public class SettingsDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private SettingsContract.View view;

        public Module(SettingsContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        SettingsContract.Presenter presenter(SettingsPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        SettingsContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(SettingsActivity activity);
    }
}

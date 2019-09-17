package com.formakers.fomes.analysis.dagger;

import com.formakers.fomes.analysis.contract.RecentAnalysisReportContract;
import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenter;
import com.formakers.fomes.analysis.view.RecentAnalysisReportFragment;
import com.formakers.fomes.common.dagger.ApplicationComponent;

public class RecentAnalysisReportDagger {

    @javax.inject.Scope public @interface Scope { }

    @dagger.Module
    public static class Module {
        private RecentAnalysisReportContract.View view;

        public Module(RecentAnalysisReportContract.View view) {
            this.view = view;
        }

        @Scope
        @dagger.Provides
        RecentAnalysisReportContract.Presenter presenter(RecentAnalysisReportPresenter presenter) {
            return presenter;
        }

        @Scope
        @dagger.Provides
        RecentAnalysisReportContract.View view() {
            return this.view;
        }
    }

    @Scope
    @dagger.Component(modules = Module.class, dependencies = ApplicationComponent.class)
    public interface Component {
        void inject(RecentAnalysisReportFragment fragment);
    }
}

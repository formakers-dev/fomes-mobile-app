package com.formakers.fomes.dagger;

import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenter;
import com.formakers.fomes.common.job.SendDataJobService;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.main.presenter.MainPresenter;
import com.formakers.fomes.provisioning.presenter.LoginPresenter;
import com.formakers.fomes.provisioning.presenter.ProvisioningPresenter;
import com.formakers.fomes.service.MessagingTokenService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, NetworkModule.class, GlideModule.class })
public interface ApplicationComponent {
    void inject(MessagingTokenService messagingTokenService);
    void inject(SendDataJobService sendDataJobService);

    // fomes
    void inject(LoginPresenter loginPresenter);
    void inject(ProvisioningPresenter provisioningPresenter);
    void inject(RecentAnalysisReportPresenter reportPresenter);
    void inject(MainPresenter presenter);

    void inject(FomesBaseActivity activity);
    void inject(BaseActivity activity);
}

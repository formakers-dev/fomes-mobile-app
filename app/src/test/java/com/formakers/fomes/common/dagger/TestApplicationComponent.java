package com.formakers.fomes.common.dagger;

import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenterTest;
import com.formakers.fomes.common.LocalBroadcastReceiverTest;
import com.formakers.fomes.common.job.SendDataJobServiceTest;
import com.formakers.fomes.common.noti.MessagingServiceTest;
import com.formakers.fomes.common.view.custom.FomesAlertDialogTest;
import com.formakers.fomes.main.presenter.MainPresenterTest;
import com.formakers.fomes.main.view.MainActivityTest;
import com.formakers.fomes.provisioning.presenter.LoginPresenterTest;
import com.formakers.fomes.provisioning.view.ProvisioningActivityTest;
import com.formakers.fomes.provisioning.view.RecentAnalysisReportActivityTest;
import com.formakers.fomes.settings.SettingsActivityTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestApplicationModule.class})
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(FomesAlertDialogTest fomesAlertDialogTest);
    void inject(SendDataJobServiceTest sendDataJobServiceTest);
    void inject(LocalBroadcastReceiverTest localBroadcastReceiverTest);
    void inject(MessagingServiceTest messagingServiceTest);

    // Fomes
    void inject(LoginPresenterTest loginPresenterTest);
    void inject(RecentAnalysisReportPresenterTest presenterTest);
    void inject(MainPresenterTest presenterTest);

    void inject(RecentAnalysisReportActivityTest activityTest);
    void inject(MainActivityTest activityTest);
    void inject(SettingsActivityTest activityTest);
    void inject(ProvisioningActivityTest activityTest);
}

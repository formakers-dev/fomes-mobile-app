package com.formakers.fomes.dagger;

import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenterTest;
import com.formakers.fomes.appbee.activity.CancelInterviewActivityTest;
import com.formakers.fomes.appbee.activity.CodeVerificationActivityTest;
import com.formakers.fomes.appbee.activity.InterviewDetailActivityTest;
import com.formakers.fomes.appbee.activity.LoadingActivityTest;
import com.formakers.fomes.appbee.activity.MyInterviewActivityTest;
import com.formakers.fomes.appbee.activity.PermissionGuideActivityTest;
import com.formakers.fomes.appbee.activity.ProjectDetailActivityTest;
import com.formakers.fomes.appbee.adapter.DescriptionImageAdapterTest;
import com.formakers.fomes.appbee.adapter.RegisteredInterviewListAdapterTest;
import com.formakers.fomes.appbee.fragment.AppUsageAnalysisFragmentTest;
import com.formakers.fomes.appbee.fragment.InterviewListFragmentTest;
import com.formakers.fomes.appbee.fragment.ProjectListFragmentTest;
import com.formakers.fomes.common.job.SendDataJobServiceTest;
import com.formakers.fomes.appbee.custom.AppBeeAlertDialogTest;
import com.formakers.fomes.main.presenter.MainPresenterTest;
import com.formakers.fomes.main.view.MainActivityTest;
import com.formakers.fomes.provisioning.presenter.LoginPresenterTest;
import com.formakers.fomes.provisioning.view.LoginActivityTest;
import com.formakers.fomes.provisioning.view.ProvisioningActivityTest;
import com.formakers.fomes.provisioning.view.RecentAnalysisReportActivityTest;
import com.formakers.fomes.service.MessagingTokenServiceTest;
import com.formakers.fomes.settings.SettingsActivityTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestApplicationModule.class})
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(LoadingActivityTest mainActivity);
    void inject(PermissionGuideActivityTest startActivityTest);
    void inject(ProjectDetailActivityTest projectDetailActivityTest);
    void inject(com.formakers.fomes.appbee.activity.LoginActivityTest loginActivityTest);
    void inject(com.formakers.fomes.appbee.activity.MainActivityTest mainActivityTest);
    void inject(InterviewDetailActivityTest interviewDetailActivityTest);
    void inject(MyInterviewActivityTest myInterviewActivityTest);
    void inject(CancelInterviewActivityTest cancelInterviewActivityTest);
    void inject(InterviewListFragmentTest interviewListFragmentTest);
    void inject(ProjectListFragmentTest projectListFragmentTest);
    void inject(AppUsageAnalysisFragmentTest onboardingAnalysisFragmentTest);
    void inject(CodeVerificationActivityTest codeVerificationActivityTest);
    void inject(RegisteredInterviewListAdapterTest registeredInterviewListAdapterTest);
    void inject(AppBeeAlertDialogTest appBeeAlertDialogTest);
    void inject(SendDataJobServiceTest sendDataJobServiceTest);
    void inject(DescriptionImageAdapterTest descriptionImageAdapterTest);
    void inject(MessagingTokenServiceTest instanceIDServiceTest);

    // Fomes
    void inject(LoginPresenterTest loginPresenterTest);
    void inject(RecentAnalysisReportPresenterTest presenterTest);
    void inject(MainPresenterTest presenterTest);

    void inject(RecentAnalysisReportActivityTest activityTest);
    void inject(MainActivityTest activityTest);
    void inject(SettingsActivityTest activityTest);
    void inject(LoginActivityTest activityTest);
    void inject(ProvisioningActivityTest activityTest);
}

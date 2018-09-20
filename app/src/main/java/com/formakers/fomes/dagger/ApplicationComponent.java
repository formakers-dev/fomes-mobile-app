package com.formakers.fomes.dagger;

import com.formakers.fomes.activity.CancelInterviewActivity;
import com.formakers.fomes.activity.CodeVerificationActivity;
import com.formakers.fomes.activity.InterviewDetailActivity;
import com.formakers.fomes.activity.LoadingActivity;
import com.formakers.fomes.activity.MyInterviewActivity;
import com.formakers.fomes.activity.PermissionGuideActivity;
import com.formakers.fomes.activity.ProjectDetailActivity;
import com.formakers.fomes.analysis.presenter.CurrentAnalysisReportPresenter;
import com.formakers.fomes.analysis.view.RecentAnalysisReportActivity;
import com.formakers.fomes.common.job.SendDataJobService;
import com.formakers.fomes.fragment.AppUsageAnalysisFragment;
import com.formakers.fomes.fragment.InterviewListFragment;
import com.formakers.fomes.fragment.ProjectListFragment;
import com.formakers.fomes.fragment.ReportMostUsedFragment;
import com.formakers.fomes.main.view.MainActivity;
import com.formakers.fomes.settings.SettingsActivity;
import com.formakers.fomes.provisioning.presenter.LoginPresenter;
import com.formakers.fomes.provisioning.presenter.ProvisioningPresenter;
import com.formakers.fomes.service.MessagingTokenService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, NetworkModule.class })
public interface ApplicationComponent {
    void inject(LoadingActivity loadingActivity);
    void inject(PermissionGuideActivity permissionGuideActivity);
    void inject(com.formakers.fomes.activity.LoginActivity loginActivity);
    void inject(com.formakers.fomes.activity.MainActivity mainActivity);
    void inject(ProjectDetailActivity projectDetailActivity);
    void inject(InterviewDetailActivity interviewDetailActivity);
    void inject(InterviewListFragment interviewListFragment);
    void inject(ProjectListFragment projectListFragment);
    void inject(AppUsageAnalysisFragment appUsageAnalysisFragment);
    void inject(ReportMostUsedFragment categoryAnalysisFragment);
    void inject(MyInterviewActivity myInterviewActivity);
    void inject(CodeVerificationActivity CodeVerificationActivity);
    void inject(CancelInterviewActivity cancelInterviewActivity);
    void inject(MessagingTokenService messagingTokenService);
    void inject(SendDataJobService sendDataJobService);

    // fomes
    void inject(LoginPresenter loginPresenter);
    void inject(ProvisioningPresenter provisioningPresenter);
    void inject(CurrentAnalysisReportPresenter reportPresenter);

    void inject(RecentAnalysisReportActivity activity);
    void inject(MainActivity activity);
    void inject(SettingsActivity activity);
}

package com.formakers.fomes.dagger;

import com.formakers.fomes.analysis.presenter.RecentAnalysisReportPresenter;
import com.formakers.fomes.appbee.activity.CancelInterviewActivity;
import com.formakers.fomes.appbee.activity.CodeVerificationActivity;
import com.formakers.fomes.appbee.activity.InterviewDetailActivity;
import com.formakers.fomes.appbee.activity.LoadingActivity;
import com.formakers.fomes.appbee.activity.MyInterviewActivity;
import com.formakers.fomes.appbee.activity.PermissionGuideActivity;
import com.formakers.fomes.appbee.activity.ProjectDetailActivity;
import com.formakers.fomes.appbee.fragment.AppUsageAnalysisFragment;
import com.formakers.fomes.appbee.fragment.InterviewListFragment;
import com.formakers.fomes.appbee.fragment.ProjectListFragment;
import com.formakers.fomes.appbee.fragment.ReportMostUsedFragment;
import com.formakers.fomes.common.job.SendDataJobService;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.common.view.FomesBaseActivity;
import com.formakers.fomes.provisioning.presenter.LoginPresenter;
import com.formakers.fomes.provisioning.presenter.ProvisioningPresenter;
import com.formakers.fomes.service.MessagingTokenService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, NetworkModule.class, GlideModule.class })
public interface ApplicationComponent {
    void inject(LoadingActivity loadingActivity);
    void inject(PermissionGuideActivity permissionGuideActivity);
    void inject(com.formakers.fomes.appbee.activity.LoginActivity loginActivity);
    void inject(com.formakers.fomes.appbee.activity.MainActivity mainActivity);
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
    void inject(RecentAnalysisReportPresenter reportPresenter);

    void inject(FomesBaseActivity activity);
    void inject(BaseActivity activity);
}

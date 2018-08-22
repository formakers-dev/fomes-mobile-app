package com.formakers.fomes.dagger;

import com.formakers.fomes.activity.CancelInterviewActivity;
import com.formakers.fomes.activity.CodeVerificationActivity;
import com.formakers.fomes.activity.InterviewDetailActivity;
import com.formakers.fomes.activity.LoadingActivity;
import com.formakers.fomes.activity.LoginActivity;
import com.formakers.fomes.activity.MainActivity;
import com.formakers.fomes.activity.MyInterviewActivity;
import com.formakers.fomes.activity.PermissionGuideActivity;
import com.formakers.fomes.activity.ProjectDetailActivity;
import com.formakers.fomes.fragment.AppUsageAnalysisFragment;
import com.formakers.fomes.fragment.InterviewListFragment;
import com.formakers.fomes.fragment.ProjectListFragment;
import com.formakers.fomes.receiver.PowerConnectedReceiver;
import com.formakers.fomes.service.MessagingTokenService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, NetworkModule.class })
public interface ApplicationComponent {
    void inject(LoadingActivity loadingActivity);
    void inject(PowerConnectedReceiver powerConnectedReceiver);
    void inject(PermissionGuideActivity permissionGuideActivity);
    void inject(LoginActivity loginActivity);
    void inject(MainActivity mainActivity);
    void inject(ProjectDetailActivity projectDetailActivity);
    void inject(InterviewDetailActivity interviewDetailActivity);
    void inject(InterviewListFragment interviewListFragment);
    void inject(ProjectListFragment projectListFragment);
    void inject(AppUsageAnalysisFragment appUsageAnalysisFragment);
    void inject(MyInterviewActivity myInterviewActivity);
    void inject(CodeVerificationActivity CodeVerificationActivity);
    void inject(CancelInterviewActivity cancelInterviewActivity);
    void inject(MessagingTokenService messagingTokenService);
}

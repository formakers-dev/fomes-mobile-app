package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.CancelInterviewActivity;
import com.appbee.appbeemobile.activity.CodeVerificationActivity;
import com.appbee.appbeemobile.activity.InterviewDetailActivity;
import com.appbee.appbeemobile.activity.LoadingActivity;
import com.appbee.appbeemobile.activity.LoginActivity;
import com.appbee.appbeemobile.activity.MainActivity;
import com.appbee.appbeemobile.activity.MyInterviewActivity;
import com.appbee.appbeemobile.activity.PermissionGuideActivity;
import com.appbee.appbeemobile.activity.ProjectDetailActivity;
import com.appbee.appbeemobile.fragment.AppUsageAnalysisFragment;
import com.appbee.appbeemobile.fragment.InterviewListFragment;
import com.appbee.appbeemobile.fragment.ProjectListFragment;
import com.appbee.appbeemobile.receiver.PowerConnectedReceiver;
import com.appbee.appbeemobile.service.MessagingTokenService;

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

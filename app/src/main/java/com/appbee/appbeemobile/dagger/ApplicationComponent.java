package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.CodeVerificationActivity;
import com.appbee.appbeemobile.activity.MyInterviewActivity;
import com.appbee.appbeemobile.activity.ProjectDetailActivity;
import com.appbee.appbeemobile.activity.InterviewDetailActivity;
import com.appbee.appbeemobile.activity.LoadingActivity;
import com.appbee.appbeemobile.activity.LoginActivity;
import com.appbee.appbeemobile.activity.MainActivity;
import com.appbee.appbeemobile.activity.PermissionGuideActivity;
import com.appbee.appbeemobile.fragment.InterviewListFragment;
import com.appbee.appbeemobile.fragment.OnboardingAnalysisFragment;
import com.appbee.appbeemobile.fragment.ProjectListFragment;
import com.appbee.appbeemobile.receiver.PowerConnectedReceiver;
import com.appbee.appbeemobile.service.InstanceIDService;
import com.appbee.appbeemobile.service.PowerConnectedService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class, NetworkModule.class })
public interface ApplicationComponent {
    void inject(LoadingActivity loadingActivity);
    void inject(PowerConnectedReceiver powerConnectedReceiver);
    void inject(PermissionGuideActivity permissionGuideActivity);
    void inject(PowerConnectedService powerConnectedService);
    void inject(LoginActivity loginActivity);
    void inject(InstanceIDService instanceIDService);
    void inject(MainActivity mainActivity);
    void inject(ProjectDetailActivity projectDetailActivity);
    void inject(InterviewDetailActivity interviewDetailActivity);
    void inject(InterviewListFragment interviewListFragment);
    void inject(ProjectListFragment projectListFragment);
    void inject(OnboardingAnalysisFragment onboardingAnalysisFragment);
    void inject(MyInterviewActivity myInterviewActivity);
    void inject(CodeVerificationActivity CodeVerificationActivity);
}

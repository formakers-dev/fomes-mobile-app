package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.CancelInterviewActivity;
import com.appbee.appbeemobile.activity.CancelInterviewActivityTest;
import com.appbee.appbeemobile.activity.CodeVerificationActivityTest;
import com.appbee.appbeemobile.activity.InterviewDetailActivityTest;
import com.appbee.appbeemobile.activity.MyInterviewActivityTest;
import com.appbee.appbeemobile.activity.ProjectDetailActivityTest;
import com.appbee.appbeemobile.activity.LoadingActivityTest;
import com.appbee.appbeemobile.activity.LoginActivityTest;
import com.appbee.appbeemobile.activity.MainActivityTest;
import com.appbee.appbeemobile.activity.PermissionGuideActivityTest;
import com.appbee.appbeemobile.adapter.RegisteredInterviewListAdapterTest;
import com.appbee.appbeemobile.fragment.InterviewListFragmentTest;
import com.appbee.appbeemobile.fragment.OnboardingAnalysisFragmentTest;
import com.appbee.appbeemobile.fragment.ProjectListFragmentTest;
import com.appbee.appbeemobile.service.PowerConnectedServiceTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestApplicationModule.class})
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(LoadingActivityTest mainActivity);
    void inject(PermissionGuideActivityTest startActivityTest);
    void inject(PowerConnectedServiceTest powerConnectedServiceTest);
    void inject(LoginActivityTest loginActivityTest);
    void inject(MainActivityTest mainActivityTest);
    void inject(ProjectDetailActivityTest projectDetailActivityTest);
    void inject(InterviewDetailActivityTest interviewDetailActivityTest);
    void inject(InterviewListFragmentTest interviewListFragmentTest);
    void inject(ProjectListFragmentTest projectListFragmentTest);
    void inject(OnboardingAnalysisFragmentTest onboardingAnalysisFragmentTest);
    void inject(CodeVerificationActivityTest codeVerificationActivityTest);
    void inject(MyInterviewActivityTest myInterviewActivityTest);
    void inject(RegisteredInterviewListAdapterTest registeredInterviewListAdapterTest);
    void inject(CancelInterviewActivityTest cancelInterviewActivityTest);
}

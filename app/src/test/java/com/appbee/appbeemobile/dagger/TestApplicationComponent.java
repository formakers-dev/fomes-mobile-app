package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.CancelInterviewActivityTest;
import com.appbee.appbeemobile.activity.CodeVerificationActivityTest;
import com.appbee.appbeemobile.activity.InterviewDetailActivityTest;
import com.appbee.appbeemobile.activity.LoadingActivityTest;
import com.appbee.appbeemobile.activity.LoginActivityTest;
import com.appbee.appbeemobile.activity.MainActivityTest;
import com.appbee.appbeemobile.activity.MyInterviewActivityTest;
import com.appbee.appbeemobile.activity.PermissionGuideActivityTest;
import com.appbee.appbeemobile.activity.ProjectDetailActivityTest;
import com.appbee.appbeemobile.adapter.DescriptionImageAdapterTest;
import com.appbee.appbeemobile.adapter.RegisteredInterviewListAdapterTest;
import com.appbee.appbeemobile.custom.AppBeeAlertDialogTest;
import com.appbee.appbeemobile.fragment.AppUsageAnalysisFragmentTest;
import com.appbee.appbeemobile.fragment.InterviewListFragmentTest;
import com.appbee.appbeemobile.fragment.ProjectListFragmentTest;
import com.appbee.appbeemobile.receiver.PowerConnectedReceiverTest;
import com.appbee.appbeemobile.service.MessagingTokenServiceTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestApplicationModule.class})
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(LoadingActivityTest mainActivity);
    void inject(PermissionGuideActivityTest startActivityTest);
    void inject(ProjectDetailActivityTest projectDetailActivityTest);
    void inject(LoginActivityTest loginActivityTest);
    void inject(MainActivityTest mainActivityTest);
    void inject(InterviewDetailActivityTest interviewDetailActivityTest);
    void inject(MyInterviewActivityTest myInterviewActivityTest);
    void inject(CancelInterviewActivityTest cancelInterviewActivityTest);
    void inject(InterviewListFragmentTest interviewListFragmentTest);
    void inject(ProjectListFragmentTest projectListFragmentTest);
    void inject(AppUsageAnalysisFragmentTest onboardingAnalysisFragmentTest);
    void inject(CodeVerificationActivityTest codeVerificationActivityTest);
    void inject(RegisteredInterviewListAdapterTest registeredInterviewListAdapterTest);
    void inject(AppBeeAlertDialogTest appBeeAlertDialogTest);
    void inject(PowerConnectedReceiverTest powerConnectedReceiverTest);
    void inject(DescriptionImageAdapterTest descriptionImageAdapterTest);
    void inject(MessagingTokenServiceTest instanceIDServiceTest);
}

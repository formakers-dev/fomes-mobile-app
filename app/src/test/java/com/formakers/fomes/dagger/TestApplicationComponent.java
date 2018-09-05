package com.formakers.fomes.dagger;

import com.formakers.fomes.activity.CancelInterviewActivityTest;
import com.formakers.fomes.activity.CodeVerificationActivityTest;
import com.formakers.fomes.activity.InterviewDetailActivityTest;
import com.formakers.fomes.activity.LoadingActivityTest;
import com.formakers.fomes.activity.MainActivityTest;
import com.formakers.fomes.activity.MyInterviewActivityTest;
import com.formakers.fomes.activity.PermissionGuideActivityTest;
import com.formakers.fomes.activity.ProjectDetailActivityTest;
import com.formakers.fomes.adapter.DescriptionImageAdapterTest;
import com.formakers.fomes.adapter.RegisteredInterviewListAdapterTest;
import com.formakers.fomes.common.job.SendDataJobServiceTest;
import com.formakers.fomes.custom.AppBeeAlertDialogTest;
import com.formakers.fomes.fragment.AppUsageAnalysisFragmentTest;
import com.formakers.fomes.fragment.InterviewListFragmentTest;
import com.formakers.fomes.fragment.ProjectListFragmentTest;
import com.formakers.fomes.provisioning.LoginActivityTest;
import com.formakers.fomes.service.MessagingTokenServiceTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestApplicationModule.class})
public interface TestApplicationComponent extends ApplicationComponent {
    void inject(LoadingActivityTest mainActivity);
    void inject(PermissionGuideActivityTest startActivityTest);
    void inject(ProjectDetailActivityTest projectDetailActivityTest);
    void inject(com.formakers.fomes.activity.LoginActivityTest loginActivityTest);
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
    void inject(SendDataJobServiceTest sendDataJobServiceTest);
    void inject(DescriptionImageAdapterTest descriptionImageAdapterTest);
    void inject(MessagingTokenServiceTest instanceIDServiceTest);

    // Fomes
    void inject(LoginActivityTest loginActivityTest);
}

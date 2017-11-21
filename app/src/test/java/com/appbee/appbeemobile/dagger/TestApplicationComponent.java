package com.appbee.appbeemobile.dagger;

import com.appbee.appbeemobile.activity.DetailActivityTest;
import com.appbee.appbeemobile.activity.LoadingActivityTest;
import com.appbee.appbeemobile.activity.LoginActivityTest;
import com.appbee.appbeemobile.activity.MainActivityTest;
import com.appbee.appbeemobile.activity.PermissionGuideActivityTest;
import com.appbee.appbeemobile.adapter.InterviewListAdapterTest;
import com.appbee.appbeemobile.fragment.InterviewListFragmentTest;
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
    void inject(InterviewListAdapterTest recommendationAppsAdapterTest);
    void inject(DetailActivityTest detailActivityTest);
    void inject(InterviewListFragmentTest interviewListFragmentTest);
    void inject(ProjectListFragmentTest projectListFragmentTest);
}

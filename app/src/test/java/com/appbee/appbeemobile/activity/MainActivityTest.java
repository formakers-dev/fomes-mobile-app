package com.appbee.appbeemobile.activity;

import android.view.View;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.adapter.ProjectListAdapter;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    private MainActivity subject;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

    @Inject
    ProjectService mockProjectService;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockLocalStorageHelper.getEmail()).thenReturn("anyEmail@gmail.com");

        subject = Robolectric.buildActivity(MainActivity.class).create().postCreate(null).get();
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void onPostCreate시_메뉴에_사용자의_아이디가_포맷에_맞게_보여진다() throws Exception {
        assertThat(subject.userIdTextView.getText()).isEqualTo("anyEmail");
    }

    @Test
    public void onPostCreate시_3페이지를_갖는_배너가_나타난다() throws Exception {
        assertThat(subject.titleBannerViewPager.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.titleBannerViewPager.getAdapter().getCount()).isEqualTo(3);
    }

    @Test
    public void onPostCreate시_2개의_탭과_페이저가_나타난다() throws Exception {
        assertThat(subject.contentsViewPager.getAdapter().getCount()).isEqualTo(2);
        assertThat(subject.tabLayout.getTabCount()).isEqualTo(2);
        assertThat(subject.tabLayout.getTabAt(0).getText()).isEqualTo("체험하기");
        assertThat(subject.tabLayout.getTabAt(1).getText()).isEqualTo("둘러보기");
    }

    @Test
    public void onPostCreate시_당신의참여를기다리는프로젝트를_표시하기위한_Adapter를_매핑한다() throws Exception {
        assertThat(subject.projectListRecyclerView.getAdapter().getClass().getSimpleName()).contains(ProjectListAdapter.class.getSimpleName());
    }

    @Test
    public void onResume시_프로젝트목록정보를_갱신한다() throws Exception {
        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(new Project("projectId4", "리얼포토", "증강현실로 한장의 사진에 담는 나만의 추억", "temporary"));
        mockProjectList.add(new Project("projectId5", "엔빵", "모임별로 엔빵해", "temporary"));
        mockProjectList.add(new Project("projectId6", "겜돌이", "게임하자", "temporary"));
        when(mockProjectService.getAllProjects()).thenReturn(Observable.just(mockProjectList));

        subject.onResume();

        verify(mockProjectService).getAllProjects();
        ProjectListAdapter projectListAdapter = (ProjectListAdapter) subject.projectListRecyclerView.getAdapter();
        assertThat(projectListAdapter.getItemCount()).isEqualTo(4);
        assertThat(projectListAdapter.getItem(0)).isEqualTo(mockProjectList.get(0));
        assertThat(projectListAdapter.getItem(1)).isEqualTo(mockProjectList.get(1));
        assertThat(projectListAdapter.getItem(2)).isEqualTo(mockProjectList.get(2));
    }
}
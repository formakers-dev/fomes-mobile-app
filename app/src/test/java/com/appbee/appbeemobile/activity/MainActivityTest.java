package com.appbee.appbeemobile.activity;

import android.view.View;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.adapter.ClabAppsAdapter;
import com.appbee.appbeemobile.adapter.RecommendationAppsAdapter;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
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

    @Inject
    RecommendationAppsAdapter mockRecommendationAppsAdapter;

    @Inject
    ClabAppsAdapter mockClabAppsAdapter;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        when(mockLocalStorageHelper.getEmail()).thenReturn("anyEmail");

        List<Project> mockRecommendationProjectList = new ArrayList<>();
        mockRecommendationProjectList.add(new Project("projectId1", "유어커스텀", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("image_path_1"), Collections.singletonList("지그재그"), 0));
        mockRecommendationProjectList.add(new Project("projectId2", "유어커스텀2", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("image_path_2"), Collections.singletonList("지그재그2"), 0));
        mockRecommendationProjectList.add(new Project("projectId3", "유어커스텀3", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("image_path_3"), Collections.singletonList("지그재그3"), 0));

        when(mockProjectService.getAllProjects()).thenReturn(Observable.just(mockRecommendationProjectList));

        List<Project> mockClabProjectList = new ArrayList<>();
        mockClabProjectList.add(new Project("projectId4", "리얼포토", "증강현실로 한장의 사진에 담는 나만의 추억", Collections.singletonList("image_path_1"), Lists.newArrayList("Foodie", "Viva video"), 0));
        mockClabProjectList.add(new Project("projectId5", "엔빵", "모임별로 엔빵해", Collections.singletonList("image_path_2"), Lists.newArrayList("카카오뱅크", "토스"), 0));
        mockClabProjectList.add(new Project("projectId6", "겜돌이", "게임하자", Collections.singletonList("image_path_3"), Lists.newArrayList("클래시로얄", "리니지"), 0));
        when(mockProjectService.getClabProjects()).thenReturn(Observable.just(mockClabProjectList));

        subject = Robolectric.buildActivity(MainActivity.class).create().postCreate(null).get();
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void onPostCreate시_3페이지를_갖는_배너가_나타난다() throws Exception {
        assertThat(subject.titleBannerViewPager.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.titleBannerViewPager.getAdapter().getCount()).isEqualTo(3);
    }

    @Test
    public void onPostCreate시_당신의참여를기다리는프로젝트를_조회하여_표시한다() throws Exception {
        verify(mockProjectService).getAllProjects();
        assertThat(subject.recommendationAppsRecyclerview.getAdapter().getClass().getSimpleName()).contains(RecommendationAppsAdapter.class.getSimpleName());

        ArgumentCaptor<List<Project>> projectListCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockRecommendationAppsAdapter).setProjectList(projectListCaptor.capture());

        List<Project> resultProjectList = projectListCaptor.getValue();

        assertThat(resultProjectList.size()).isEqualTo(3);
        Project project = resultProjectList.get(0);
        assertThat(project.getProjectId()).isEqualTo("projectId1");
        assertThat(project.getName()).isEqualTo("유어커스텀");
        assertThat(project.getIntroduce()).isEqualTo("[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!");
        assertThat(project.getImages().get(0)).isEqualTo("image_path_1");
        assertThat(project.getApps().get(0)).isEqualTo("지그재그");
        assertThat(project.getStatus()).isEqualTo(0);
    }

    @Test
    public void onPostCreate시_취향저격Clab프로젝트둘러보기를_조회하여_표시한다() throws Exception {
        verify(mockProjectService).getClabProjects();
        assertThat(subject.clabAppsRecyclerview.getAdapter().getClass().getSimpleName()).contains(ClabAppsAdapter.class.getSimpleName());

        ArgumentCaptor<List<Project>> projectListCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockClabAppsAdapter).setProjectList(projectListCaptor.capture());
        List<Project> resultProjectList = projectListCaptor.getValue();
        assertThat(resultProjectList.size()).isEqualTo(3);
        Project project = resultProjectList.get(0);

        assertThat(project.getProjectId()).isEqualTo("projectId4");
        assertThat(project.getName()).isEqualTo("리얼포토");
        assertThat(project.getIntroduce()).isEqualTo("증강현실로 한장의 사진에 담는 나만의 추억");
        assertThat(project.getImages().get(0)).isEqualTo("image_path_1");
        assertThat(project.getApps().get(0)).isEqualTo("Foodie");
        assertThat(project.getStatus()).isEqualTo(0);
    }
}
package com.appbee.appbeemobile.activity;

import android.view.View;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.adapter.CommonRecyclerViewAdapter;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

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
    CommonRecyclerViewAdapter mockCommonRecyclerViewAdapter;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        when(mockLocalStorageHelper.getEmail()).thenReturn("anyEmail");

        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(new Project("projectId1", "유어커스텀", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("image_path_1"), Collections.singletonList("지그재그"), 0));
        mockProjectList.add(new Project("projectId2", "유어커스텀2", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("image_path_2"), Collections.singletonList("지그재그2"), 0));
        mockProjectList.add(new Project("projectId3", "유어커스텀3", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", Collections.singletonList("image_path_3"), Collections.singletonList("지그재그3"), 0));

        when(mockProjectService.getAllProjects()).thenReturn(Observable.just(mockProjectList));

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
    public void onPostCreate시_당신을위한추천앱을_조회하여_표시한다() throws Exception {
        verify(mockProjectService).getAllProjects();

        ArgumentCaptor<List<Project>> projectListCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockCommonRecyclerViewAdapter).setProjectList(projectListCaptor.capture());

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
    public void onPostCreate시_이런앱은어때요_영역이_나타난다() throws Exception {
        assertThat(subject.introducingAppsTitle.getText()).isEqualTo("이런 앱은 어때요*");
        assertThat(subject.introducingAppsSubtitle.getText()).isEqualTo("당신의 응원을 기다리고 있는 프로젝트 입니다.");
    }
}
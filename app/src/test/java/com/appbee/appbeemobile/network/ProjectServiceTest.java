package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.Project;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProjectServiceTest {

    private ProjectService subject;

    @Mock
    private ProjectAPI mockProjectAPI;

    @Mock
    private LocalStorageHelper mockLocalStorageHelper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new ProjectService(mockProjectAPI, mockLocalStorageHelper);
        when(mockLocalStorageHelper.getAccessToken()).thenReturn("TEST_TOKEN");

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void getAllProjects호출시_프로젝트_리스트를_리턴한다() throws Exception {
        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(new Project("projectId1", "유어커스텀", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", "temporary"));
        mockProjectList.add(new Project("projectId2", "유어커스텀2", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!",  "temporary"));
        mockProjectList.add(new Project("projectId3", "유어커스텀3", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!",  "temporary"));

        when(mockProjectAPI.getAllProjects(anyString())).thenReturn(Observable.just(mockProjectList));

        subject.getAllProjects().subscribe(projectList -> {
            assertThat(projectList).isNotNull();
            assertThat(projectList.size()).isEqualTo(3);
            Project project = projectList.get(0);
            assertThat(project.getProjectId()).isEqualTo("projectId1");
            assertThat(project.getName()).isEqualTo("유어커스텀");
            assertThat(project.getIntroduce()).isEqualTo("[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!");
            assertThat(project.getStatus()).isEqualTo("temporary");
        });
    }

    @Test
    public void getProject호출시_요청한_프로젝트ID에_해당하는_프로젝트_정보를_리턴한다() throws Exception {
        Project mockProject = new Project("projectId123", "유어커스텀", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", "temporary");

        when(mockProjectAPI.getProject(anyString(), anyString())).thenReturn(Observable.just(mockProject));

        subject.getProject("projectId123").subscribe(project -> {
            assertThat(project).isNotNull();
            assertThat(project.getProjectId()).isEqualTo("projectId123");
            assertThat(project.getName()).isEqualTo("유어커스텀");
            assertThat(project.getIntroduce()).isEqualTo("[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!");
            assertThat(project.getStatus()).isEqualTo("temporary");
        });
    }

    @Test
    public void postParticipate호출시_인터뷰참여API를_호출한다() throws Exception {
        when(mockProjectAPI.postParticipate(anyString(), anyString())).thenReturn(Observable.just(true));

        subject.postParticipate("projectId").subscribe(result -> assertThat(result).isTrue());
    }
}
package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.Project;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static com.appbee.appbeemobile.model.Project.*;
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

    @Test
    public void getAllInterview호출시_인터뷰조회API를_호출한다() throws Exception {
        List<Project> mockProjectList = new ArrayList<>();

        List<Project.InterviewPlan> mockPlans = new ArrayList<>();
        mockPlans.add(new Project.InterviewPlan(10, "인트로"));
        mockPlans.add(new Project.InterviewPlan(60, "인터뷰"));

        List<String> participantList = Lists.newArrayList("google1234", "google2345");
        Project.Interview mockInterview = new Project.Interview(1L, mockPlans, "20171101", "20171105", true, "20171110", "20171115", "서울대", true, "offline", 5, participantList);

        List<ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new ImageObject("www.image.com", "테스트이미지"));

        List<ImageObject> descriptionImageList = new ArrayList<>();
        descriptionImageList.add(new ImageObject("www.image.com", "테스트이미지"));

        mockProjectList.add(new Project("testProjectId", "테스트프로젝트", "프로젝트소개", imageObjectList, "이것은테스트프로젝트입니다.", descriptionImageList, "registered", null, true, mockInterview));

        when(mockProjectAPI.getAllInterviews(anyString())).thenReturn(Observable.just(mockProjectList));

        subject.getAllInterviews().subscribe(interviewList -> {
            assertThat(interviewList.size()).isEqualTo(1);

            Project project = interviewList.get(0);

            assertThat(project.getImages().size()).isEqualTo(1);
            assertThat(project.getImages().get(0).getUrl()).isEqualTo("www.image.com");
            assertThat(project.getImages().get(0).getName()).isEqualTo("테스트이미지");
            assertThat(project.getName()).isEqualTo("테스트프로젝트");
            assertThat(project.getDescription()).isEqualTo("이것은테스트프로젝트입니다.");
            assertThat(project.getInterview().getSeq()).isEqualTo(1L);
            assertThat(project.getInterview().getLocation()).isEqualTo("서울대");
            assertThat(project.getInterview().getTotalCount()).isEqualTo(5);


        });

    }
}
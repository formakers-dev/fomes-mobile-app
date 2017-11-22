package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.Project;
import com.google.common.collect.Lists;
import com.ibm.icu.impl.CalendarAstronomer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

    @Test
    public void getAllInterview호출시_인터뷰조회API를_호출한다() throws Exception {
        List<Project> mockProjectList = new ArrayList<>();

        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Project.Person owner = new Project.Person("프로젝트 담당자", "www.projectOwnerImage.com", "프로젝트 담당자 소개입니다");

        List<Project.InterviewPlan> interviewPlanList = new ArrayList<>();
        interviewPlanList.add(new Project.InterviewPlan(10, "인트로"));
        interviewPlanList.add(new Project.InterviewPlan(60, "인터부"));
        Project.Person interviewer = new Project.Person("인터뷰어", "www.interviewerImage.com", "인터뷰어 소개입니다");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 1, 4);
        Date interviewDate = calendar.getTime();
        calendar.set(2017, 1, 2);
        Date openDate = calendar.getTime();
        calendar.set(2017, 1, 3);
        Date closeDate = calendar.getTime();

        Project.Interview interview = new Project.Interview(1L, Arrays.asList("네이버웹툰"), interviewPlanList, interviewDate, openDate, closeDate, "우면사업장", 5, interviewer);

        Project mockProject = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered", true, interview);

        mockProjectList.add(mockProject);

        when(mockProjectAPI.getAllInterviews(anyString())).thenReturn(Observable.just(mockProjectList));

        subject.getAllInterviews().subscribe(interviewList -> {
            assertThat(interviewList.size()).isEqualTo(1);

            Project project = interviewList.get(0);

            assertThat(project.getName()).isEqualTo("릴루미노");
            assertThat(project.getDescription()).isEqualTo("안녕하세요 릴루미노팀입니다.");
            assertThat(project.getInterview().getSeq()).isEqualTo(1L);
            assertThat(project.getInterview().getLocation()).isEqualTo("우면사업장");
            assertThat(project.getInterview().getTotalCount()).isEqualTo(5);
        });

    }
}
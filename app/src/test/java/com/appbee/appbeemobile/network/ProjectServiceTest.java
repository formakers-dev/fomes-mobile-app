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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Project.Person owner = new Project.Person("프로젝트 담당자", "www.projectOwnerImage.com", "프로젝트 담당자 소개입니다");

        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered"));
        mockProjectList.add(new Project("projectId2", "릴루미노2", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)2", imageObject, "안녕하세요 릴루미노팀입니다.2", imageObjectList, owner, "registered"));

        when(mockProjectAPI.getAllProjects(anyString())).thenReturn(Observable.just(mockProjectList));
        List<Project> projectList = subject.getAllProjects().toBlocking().single();

        assertThat(projectList).isNotNull();
        assertThat(projectList.size()).isEqualTo(2);
        Project project = projectList.get(0);
        assertThat(project.getProjectId()).isEqualTo("projectId");
        assertThat(project.getName()).isEqualTo("릴루미노");
        assertThat(project.getIntroduce()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)");
        assertThat(project.getStatus()).isEqualTo("registered");

        Project project2 = projectList.get(1);
        assertThat(project2.getProjectId()).isEqualTo("projectId2");
        assertThat(project2.getName()).isEqualTo("릴루미노2");
        assertThat(project2.getIntroduce()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)2");
        assertThat(project2.getStatus()).isEqualTo("registered");
    }

    @Test
    public void getProject호출시_요청한_프로젝트ID에_해당하는_프로젝트_정보를_리턴한다() throws Exception {
        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Project.Person owner = new Project.Person("프로젝트 담당자", "www.projectOwnerImage.com", "프로젝트 담당자 소개입니다");

        Project project = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered");

        when(mockProjectAPI.getProject(anyString(), eq("projectId"))).thenReturn(Observable.just(project));

        Project result = subject.getProject("projectId").toBlocking().single();
        assertThat(result).isNotNull();
        assertThat(result.getProjectId()).isEqualTo("projectId");
        assertThat(result.getName()).isEqualTo("릴루미노");
        assertThat(result.getIntroduce()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)");
        assertThat(result.getStatus()).isEqualTo("registered");
    }

    @Test
    public void postParticipate호출시_인터뷰참여API를_호출한다() throws Exception {
        when(mockProjectAPI.postParticipate(anyString(), anyString(), anyLong(), anyString())).thenReturn(Observable.just(true));

        subject.postParticipate("projectId", 1L, "").subscribe(result -> assertThat(result).isTrue());
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

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 1, 4);
        Date interviewDate = calendar.getTime();
        calendar.set(2017, 1, 2);
        Date openDate = calendar.getTime();
        calendar.set(2017, 1, 3);
        Date closeDate = calendar.getTime();

        Project.Interview interview = new Project.Interview(1L, Arrays.asList("네이버웹툰"), interviewPlanList, interviewDate, openDate, closeDate, "우면사업장", 5);

        Project mockProject = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered", interview);

        mockProjectList.add(mockProject);

        when(mockProjectAPI.getAllInterviews(anyString())).thenReturn(Observable.just(mockProjectList));

        List<Project> interviewList = subject.getAllInterviews().toBlocking().single();

        assertThat(interviewList.size()).isEqualTo(1);

        Project project = interviewList.get(0);

        assertThat(project.getName()).isEqualTo("릴루미노");
        assertThat(project.getDescription()).isEqualTo("안녕하세요 릴루미노팀입니다.");
        assertThat(project.getInterview().getSeq()).isEqualTo(1L);
        assertThat(project.getInterview().getLocation()).isEqualTo("우면사업장");
        assertThat(project.getInterview().getTotalCount()).isEqualTo(5);

    }

    @Test
    public void getInterview호출시_단건인터뷰조회API를_호출한다() throws Exception {
        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Project.Person owner = new Project.Person("프로젝트 담당자", "www.projectOwnerImage.com", "프로젝트 담당자 소개입니다");

        List<Project.InterviewPlan> interviewPlanList = new ArrayList<>();
        interviewPlanList.add(new Project.InterviewPlan(10, "인트로"));
        interviewPlanList.add(new Project.InterviewPlan(60, "인터뷰"));

        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 2, 4);   // 1월
        Date interviewDate = calendar.getTime();
        calendar.set(2018, 2, 2);   // 1월
        Date openDate = calendar.getTime();
        calendar.set(2018, 2, 3);   // 1월
        Date closeDate = calendar.getTime();

        Project.Interview interview = new Project.Interview(1L, Arrays.asList("네이버웹툰"), interviewPlanList, interviewDate, openDate, closeDate, "우면사업장", 5);

        Project project = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered", interview);

        when(mockProjectAPI.getInterview(anyString(), eq("projectId"), eq(1L))).thenReturn(Observable.just(project));

        subject.getInterview("projectId", 1L).subscribe(result -> {
            assertThat(result.getProjectId()).isEqualTo("projectId");
            assertThat(result.getInterview().getSeq()).isEqualTo(1L);
            assertThat(result.getImage().getUrl()).isEqualTo("www.imageUrl.com");
            assertThat(result.getInterview().getApps().size()).isEqualTo(1);
            assertThat(result.getInterview().getApps().get(0)).isEqualTo("네이버웹툰");
            assertThat(result.getName()).isEqualTo("릴루미노");
            assertThat(result.getIntroduce()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)");
            assertThat(result.getInterview().getLocation()).isEqualTo("우면사업장");
            assertThat(result.getInterview().getInterviewDate()).isEqualTo(interviewDate);
            assertThat(result.getInterview().getCloseDate()).isEqualTo(closeDate);
            assertThat(result.getDescriptionImages().size()).isEqualTo(3);
            assertThat(result.getDescriptionImages().get(0).getUrl()).isEqualTo("www.imageUrl.com1");
            assertThat(result.getDescriptionImages().get(1).getUrl()).isEqualTo("www.imageUrl.com2");
            assertThat(result.getDescriptionImages().get(2).getUrl()).isEqualTo("www.imageUrl.com3");
            assertThat(result.getDescription()).isEqualTo("안녕하세요 릴루미노팀입니다.");
            assertThat(result.getInterview().getPlans().size()).isEqualTo(2);
            assertThat(result.getInterview().getPlans().get(0).getMinute()).isEqualTo(10);
            assertThat(result.getInterview().getPlans().get(0).getPlan()).isEqualTo("인트로");
            assertThat(result.getInterview().getPlans().get(1).getMinute()).isEqualTo(60);
            assertThat(result.getInterview().getPlans().get(1).getPlan()).isEqualTo("인터뷰");
        });

    }
}
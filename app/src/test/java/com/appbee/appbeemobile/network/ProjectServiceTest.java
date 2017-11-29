package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.AppInfo;
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
import java.util.Collections;
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
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        MockitoAnnotations.initMocks(this);
        subject = new ProjectService(mockProjectAPI, mockLocalStorageHelper);
        when(mockLocalStorageHelper.getAccessToken()).thenReturn("TEST_TOKEN");
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

        Project.Person owner = new Project.Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered"));
        mockProjectList.add(new Project("projectId2", "릴루미노2", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)2", imageObject, "안녕하세요 릴루미노팀입니다.2", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered"));

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

        Project.Person owner = new Project.Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        Project project = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered");

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

        Project.Person owner = new Project.Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 1, 4);
        Date interviewDate = calendar.getTime();
        calendar.set(2017, 1, 2);
        Date openDate = calendar.getTime();
        calendar.set(2017, 1, 3);
        Date closeDate = calendar.getTime();

        Project.Interview interview = new Project.Interview(1L, Collections.singletonList(new AppInfo("com.naver.webtoon", "네이버웹툰")), "인터뷰소개", interviewDate, openDate, closeDate, "우면사업장", "오시는길입니다", 5, Arrays.asList("time8", "time9", "time10"), "", "", "오프라인 인터뷰");

        Project mockProject = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered", interview);

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

        Project.Person owner = new Project.Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 2, 4);   // 1월
        Date interviewDate = calendar.getTime();
        calendar.set(2018, 2, 2);   // 1월
        Date openDate = calendar.getTime();
        calendar.set(2018, 2, 3);   // 1월
        Date closeDate = calendar.getTime();

        Project.Interview interview = new Project.Interview(1L, Collections.singletonList(new AppInfo("com.naver.webtoon", "네이버웹툰")), "인터뷰소개", interviewDate, openDate, closeDate, "우면사업장", "오시는길입니다", 5, Arrays.asList("time8", "time9", "time10"), "", "", "오프라인 인터뷰");

        Project project = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered", interview);

        when(mockProjectAPI.getInterview(anyString(), eq("projectId"), eq(1L))).thenReturn(Observable.just(project));

        subject.getInterview("projectId", 1L).subscribe(result -> {
            assertThat(result.getProjectId()).isEqualTo("projectId");
            assertThat(result.getInterview().getSeq()).isEqualTo(1L);
            assertThat(result.getImage().getUrl()).isEqualTo("www.imageUrl.com");
            assertThat(result.getInterview().getApps().size()).isEqualTo(1);
            assertThat(result.getInterview().getApps().get(0).getPackageName()).isEqualTo("com.naver.webtoon");
            assertThat(result.getInterview().getApps().get(0).getAppName()).isEqualTo("네이버웹툰");
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
        });
    }

    @Test
    public void getRegisteredInterviews호출시_사용자가_신청한_인터뷰조회_API를_호출한다() throws Exception {
        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Project.Person owner = new Project.Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 2, 4);   // 1월
        Date interviewDate = calendar.getTime();
        calendar.set(2018, 2, 2);   // 1월
        Date openDate = calendar.getTime();
        calendar.set(2018, 2, 3);   // 1월
        Date closeDate = calendar.getTime();

        Project.Interview interview = new Project.Interview(1L, Collections.singletonList(new AppInfo("com.naver.webtoon", "네이버웹툰")), "인터뷰소개", interviewDate, openDate, closeDate, "우면사업장", "오시는길입니다", 5, Arrays.asList("time8", "time9", "time10"), "time9", "010-9999-8888", "오프라인 테스트");

        Project project = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered", interview);

        when(mockProjectAPI.getRegisteredInterviews(anyString())).thenReturn(Observable.just(Collections.singletonList(project)));

        List<Project> projectList = subject.getRegisteredInterviews().toBlocking().single();

        assertThat(projectList.size()).isEqualTo(1);

        assertThat(projectList.get(0).getName()).isEqualTo("릴루미노");
        assertThat(projectList.get(0).getInterview().getLocation()).isEqualTo("우면사업장");
        assertThat(projectList.get(0).getInterview().getLocationDescription()).isEqualTo("오시는길입니다");
        assertThat(projectList.get(0).getInterview().getInterviewDate()).isEqualTo(interviewDate);
        assertThat(projectList.get(0).getInterview().getOpenDate()).isEqualTo(openDate);
        assertThat(projectList.get(0).getInterview().getCloseDate()).isEqualTo(closeDate);
        assertThat(projectList.get(0).getInterview().getSelectedTimeSlot()).isEqualTo("time9");
        assertThat(projectList.get(0).getInterview().getEmergencyPhone()).isEqualTo("010-9999-8888");
        assertThat(projectList.get(0).getInterview().getIntroduce()).isEqualTo("인터뷰소개");
    }

    @Test
    public void postCancelParticipate를호출시요청한인터뷰취소API를호출한다() throws Exception {
        when(mockProjectAPI.postCancleParticipate(anyString(), anyString(), anyLong(), anyString())).thenReturn(Observable.just(true));

        Boolean result = subject.postCancelParticipate("projectId", 1L, "time9").toBlocking().single();
        assertThat(result).isTrue();
    }
}
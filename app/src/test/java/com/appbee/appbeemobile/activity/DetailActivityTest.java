package com.appbee.appbeemobile.activity;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DetailActivityTest {
    private DetailActivity subject;

    @Inject
    ProjectService mockProjectService;

    @Before
    public void setUp() throws Exception {

        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        Intent intent = new Intent();
        intent.putExtra("EXTRA_PROJECT_ID", "projectId");

        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("http://www.imageUrl.com", "imageFileNmae"));
        List<Project.InterviewPlan> interviewPlenList = new ArrayList<>();
        interviewPlenList.add(new Project.InterviewPlan(10, "인트로"));
        interviewPlenList.add(new Project.InterviewPlan(60, "인터뷰"));
        Project project = new Project("projectId1", "유어커스텀", "[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!", imageObjectList, Collections.singletonList("지그재그"), "안녕하세요. 지그재그 인터뷰어입니다.하하하.", "지그재그앱은 지그재그입니다", "오프라인인터뷰", true, "서울대", "20171101", "20171105", "20171110", "20171115", interviewPlenList, 0);

        when(mockProjectService.getProject(anyString())).thenReturn(rx.Observable.just(project));

        ActivityController<DetailActivity> activityController = Robolectric.buildActivity(DetailActivity.class, intent);
        subject = activityController.create().get();
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void onCreate시_전달받은_projectID에_해당하는_project정보를_조회한다() throws Exception {
        assertThat(subject.getIntent().getStringExtra("EXTRA_PROJECT_ID")).isEqualTo("projectId");
        verify(mockProjectService).getProject(anyString());

        assertThat(subject.project).isNotNull();
        assertThat(subject.project.getProjectId()).isEqualTo("projectId1");
        assertThat(subject.project.getName()).isEqualTo("유어커스텀");
        assertThat(subject.project.getIntroduce()).isEqualTo("[쇼핑] 장농 속 잠든 옷, 커스텀으로 재탄생!");
        assertThat(subject.project.getImages().get(0).getUrl()).isEqualTo("http://www.imageUrl.com");
        assertThat(subject.project.getImages().get(0).getName()).isEqualTo("imageFileNmae");
        assertThat(subject.project.getApps().get(0)).isEqualTo("지그재그");
        assertThat(subject.project.getInterviewerIntroduce()).isEqualTo("안녕하세요. 지그재그 인터뷰어입니다.하하하.");
        assertThat(subject.project.getDescription()).isEqualTo("지그재그앱은 지그재그입니다");
        assertThat(subject.project.getInterviewType()).isEqualTo("오프라인인터뷰");
        assertThat(subject.project.isInterviewNegotiable()).isEqualTo(true);
        assertThat(subject.project.getLocation()).isEqualTo("서울대");
        assertThat(subject.project.getOpenDate()).isEqualTo("20171101");
        assertThat(subject.project.getCloseDate()).isEqualTo("20171105");
        assertThat(subject.project.getStartDate()).isEqualTo("20171110");
        assertThat(subject.project.getEndDate()).isEqualTo("20171115");
        assertThat(subject.project.getPlans().get(0).getMinute()).isEqualTo(10);
        assertThat(subject.project.getPlans().get(0).getPlan()).isEqualTo("인트로");
        assertThat(subject.project.getStatus()).isEqualTo(0);
    }

    @Test
    public void onCreate시_조회된_project_정보를_화면에_보여준다() throws Exception {

    }
}
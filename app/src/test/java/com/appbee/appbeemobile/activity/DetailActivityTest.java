package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.view.View;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DetailActivityTest {
    private DetailActivity subject;

    @Inject
    ProjectService mockProjectService;
    private ActivityController<DetailActivity> activityController;

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
        List<Project.InterviewPlan> interviewPlanList = new ArrayList<>();
        interviewPlanList.add(new Project.InterviewPlan(10, "인트로"));
        interviewPlanList.add(new Project.InterviewPlan(60, "인터뷰"));

        Project.Interview interview = new Project.Interview(interviewPlanList, "20171101", "20171105", true, "20171110", "20171115", "서울대", true, "offline");
        Project.Interviewer interviewer = new Project.Interviewer("이호영", "www.person.com", "-17년 삼성전자 C-lab과제 툰스토리 팀\n-Create Leader");

        Project project = new Project("projectId1",
                "유어커스텀",
                "증강현실로 한장의 사진에 담는 나만의 추억",
                imageObjectList,
                Lists.newArrayList("Foodie", "Viva video"),
                "지그재그앱은 지그재그입니다",
                null,
                "temporary",
                interviewer,
                true,
                interview);

        when(mockProjectService.getProject(anyString())).thenReturn(rx.Observable.just(project));

        activityController = Robolectric.buildActivity(DetailActivity.class, intent);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    @Ignore
    public void onCreate시_전달받은_projectID에_해당하는_project정보를_조회한다() throws Exception {
        subject = activityController.create().postCreate(null).get();

        assertThat(subject.getIntent().getStringExtra("EXTRA_PROJECT_ID")).isEqualTo("projectId");
        verify(mockProjectService).getProject(eq("projectId"));
    }

    @Test
    public void onPostCreate시_조회된_project_헤더정보를_화면에_보여준다() throws Exception {
        subject = activityController.create().postCreate(null).get();

        assertThat(subject.representationImageView.getTag(R.string.tag_key_image_url)).isEqualTo("http://www.imageUrl.com");
        assertThat(subject.clabBadgeImageView.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void onPostCreate시_조회된_project_제목정보를_화면에_보여준다() throws Exception {
        subject = activityController.create().postCreate(null).get();

        assertThat(subject.projectIntroduceTextView.getText()).isEqualTo("증강현실로 한장의 사진에 담는 나만의 추억");
        assertThat(subject.projectNameTextView.getText()).isEqualTo("유어커스텀");
        assertThat(subject.appsDescriptionTextView.getText()).isEqualTo("[Foodie][Viva video] 앱을 사용하시는 당신과 찰떡궁합!");
    }

    @Test
    public void onPostCreate시_조회된_interviewer정보를_화면에_보여준다() throws Exception {
        subject = activityController.create().postCreate(null).get();
        assertThat(subject.interviewerPhotoImgaeView.getTag(R.string.tag_key_image_url)).isEqualTo(null);
        assertThat(subject.interviewerNameTextView.getText()).isEqualTo("이호영");
        assertThat(subject.interviewerIntroduceTextView.getText()).contains("17년 삼성전자 C-lab과제 툰스토리 팀");
    }
}
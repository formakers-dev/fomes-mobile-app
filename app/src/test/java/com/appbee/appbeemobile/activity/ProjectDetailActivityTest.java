package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.adapter.ImagePagerAdapter;
import com.appbee.appbeemobile.helper.TimeHelper;
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
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProjectDetailActivityTest {
    private ProjectDetailActivity subject;

    @Inject
    ProjectService mockProjectService;

    @Inject
    TimeHelper mockTimeHelper;

    private ActivityController<ProjectDetailActivity> activityController;

    private Intent intent = new Intent();

    @Before
    public void setUp() throws Exception {

        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        intent.putExtra("EXTRA_PROJECT_ID", "projectId1");

        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("http://www.imageUrl.com", "imageFileName"));
        List<Project.InterviewPlan> plans = new ArrayList<>();
        plans.add(new Project.InterviewPlan(10, "인트로"));
        plans.add(new Project.InterviewPlan(60, "인터뷰"));
        List<Project.ImageObject> descriptionImageObjectList = new ArrayList<>();
        descriptionImageObjectList.add(new Project.ImageObject("http://www.descriptionImage.com", "descriptionImage"));

        List<String> participantList = Lists.newArrayList("google1234", "google2345");
        Project.Interview interview = new Project.Interview(1L, plans, "20171101", "20171105", true, "20171110", "20171115", "서울대", true, "offline", 5, participantList);
        Project.Interviewer interviewer = new Project.Interviewer("이호영", "www.person.com", "-17년 삼성전자 C-lab과제 툰스토리 팀\n-Create Leader");

        Project project = new Project("projectId1",
                "유어커스텀",
                "증강현실로 한장의 사진에 담는 나만의 추억",
                imageObjectList,
                "지그재그앱은 지그재그입니다",
                descriptionImageObjectList,
                "temporary",
                interviewer,
                true,
                interview);

        when(mockProjectService.getProject(anyString())).thenReturn(rx.Observable.just(project));
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03

        activityController = Robolectric.buildActivity(ProjectDetailActivity.class, intent);
        subject = activityController.create().postCreate(null).get();
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    @Ignore
    public void onPostCreate시_전달받은_projectID에_해당하는_project정보를_조회한다() throws Exception {
        assertThat(subject.getIntent().getStringExtra("EXTRA_PROJECT_ID")).isEqualTo("projectId");
        verify(mockProjectService).getProject(eq("projectId"));
    }

    @Test
    public void onPostCreate시_조회된_project_헤더정보를_화면에_보여준다() throws Exception {
        assertThat(subject.representationImageView.getTag(R.string.tag_key_image_url)).isEqualTo("http://www.imageUrl.com");
        assertThat(subject.clabBadgeImageView.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void onPostCreate시_조회된_project_제목정보를_화면에_보여준다() throws Exception {
        assertThat(subject.projectIntroduceTextView.getText()).isEqualTo("증강현실로 한장의 사진에 담는 나만의 추억");
        assertThat(subject.projectNameTextView.getText()).isEqualTo("유어커스텀");

        assertThat(subject.interviewTypeSummaryTextView.getText()).isEqualTo("offline");
        assertThat(subject.availableInterviewerCountTextView.getText()).isEqualTo("3");
        assertThat(subject.dDayTextView.getText()).isEqualTo("D-12"); // 11/3 ~ 11/15
    }

    @Test
    public void onPostCreate시_오늘날짜가_마감일이후인경우_D_Day는_0이_표시된다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1512097383000L);   //2017-12-01 03:03:03

        subject = Robolectric.buildActivity(ProjectDetailActivity.class, intent).create().postCreate(null).get();

        assertThat(subject.dDayTextView.getText()).isEqualTo("D-0");
    }

    @Test
    public void onPostCreate시_조회된_interviewer정보를_화면에_보여준다() throws Exception {
        assertThat(subject.interviewerPhotoImageView.getTag(R.string.tag_key_image_url)).isEqualTo(null);
        assertThat(subject.interviewerNameTextView.getText()).isEqualTo("이호영");
        assertThat(subject.interviewerIntroduceTextView.getText()).contains("17년 삼성전자 C-lab과제 툰스토리 팀");
    }

    @Test
    public void onPostCreate시_조회된_project_설명정보를_화면에_보여준다() throws Exception {
        assertThat(subject.projectDescriptionTextView.getText()).contains("지그재그앱은 지그재그입니다");
        assertThat(subject.descriptionImageViewPager.getAdapter().getClass().getSimpleName()).contains(ImagePagerAdapter.class.getSimpleName());
    }

    @Test
    public void onPostCreate시_조회된_인터뷰진행정보를_화면에_보여준다() throws Exception {
        assertThat(subject.interviewIntroduceTextView.getText()).contains("유어커스텀");
        assertThat(subject.typeInterviewInfoView.getText()).contains("offline");
        assertThat(subject.locationInterviewInfoView.getText()).contains("서울대");
        assertThat(subject.timeInterviewInfoView.getText()).contains("70");
        assertThat(subject.dateInterviewInfoView.getText()).contains("11월 01일~11월 05일");
    }

    @Test
    public void onPostCreate시_조회된_인터뷰신청현황을_화면에_보여준다() throws Exception {
        assertThat(subject.participationStatus.getText()).contains("2/5");
        assertThat(subject.closeDate.getText()).contains("~17.11.15");
    }

    @Test
    public void onPostCreate시_조회된_인터뷰일정을_화면에_보여준다() throws Exception {
        assertThat(subject.interviewPlanLayout.getChildCount()).isEqualTo(3);
        assertThat(((TextView) subject.interviewPlanLayout.getChildAt(1).findViewById(R.id.minute)).getText()).isEqualTo("10");
        assertThat(((TextView) subject.interviewPlanLayout.getChildAt(1).findViewById(R.id.plan)).getText()).isEqualTo("인트로");
        assertThat(((TextView) subject.interviewPlanLayout.getChildAt(2).findViewById(R.id.minute)).getText()).isEqualTo("60");
        assertThat(((TextView) subject.interviewPlanLayout.getChildAt(2).findViewById(R.id.plan)).getText()).isEqualTo("인터뷰");
    }

    @Test
    public void onPostCreate시_신청버튼에_인터뷰Summary정보를_출력한다() throws Exception {
        assertThat(subject.interviewSummaryTextView.getText()).isEqualTo("서울대 / 11.01~11.05 / 70분 / 3만원 리워드");
    }

    @Test
    public void BackButton클릭시_이전화면으로_복귀한다() throws Exception {
        subject.findViewById(R.id.back_button).performClick();
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void submitButton클릭시_인터뷰참여신청API를_호출한다() throws Exception {
        when(mockProjectService.postParticipate(anyString())).thenReturn(Observable.just(true));

        subject.findViewById(R.id.submit_button).performClick();

        verify(mockProjectService).postParticipate("projectId1");
    }

    @Test
    public void 인터뷰참여신청성공시_인터뷰참여완료팝업을_표시한다() throws Exception {
        when(mockProjectService.postParticipate(anyString())).thenReturn(Observable.just(true));

        subject.findViewById(R.id.submit_button).performClick();

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("인터뷰참가신청완료!!");
    }

    @Test
    public void 인터뷰참여신청실패시_인터뷰참여실패팝업을_표시한다() throws Exception {
        when(mockProjectService.postParticipate(anyString())).thenReturn(Observable.error(new HttpException(Response.error(406, ResponseBody.create(null, "")))));

        subject.findViewById(R.id.submit_button).performClick();

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("406");
    }
}
package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.adapter.ImagePagerAdapter;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.model.Project.Interview;
import com.appbee.appbeemobile.model.Project.InterviewPlan;
import com.appbee.appbeemobile.model.Project.Person;
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
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class InterviewDetailActivityTest {
    private InterviewDetailActivity subject;

    @Inject
    ProjectService mockProjectService;

    @Inject
    TimeHelper mockTimeHelper;

    private ActivityController<InterviewDetailActivity> activityController;

    private Intent intent = new Intent();

    @Before
    public void setUp() throws Exception {

        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        intent.putExtra("EXTRA_INTERVIEW_SEQ", 1L);
        intent.putExtra("EXTRA_PROJECT_ID", "projectId");

        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Person owner = new Person("프로젝트 담당자", "www.projectOwnerImage.com", "프로젝트 담당자 소개입니다");

        List<InterviewPlan> interviewPlanList = new ArrayList<>();
        interviewPlanList.add(new InterviewPlan(10, "인트로"));
        interviewPlanList.add(new InterviewPlan(60, "인터뷰"));
        Person interviewer = new Person("인터뷰어", "www.interviewerImage.com", "인터뷰어 소개입니다");

        // month 1월
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 2, 4);
        Date interviewDate = calendar.getTime();
        calendar.set(2018, 2, 2);
        Date openDate = calendar.getTime();
        calendar.set(2018, 2, 3);
        Date closeDate = calendar.getTime();

        Interview interview = new Interview(1L, Arrays.asList("네이버웹툰"), interviewPlanList, interviewDate, openDate, closeDate, "우면사업장", 5, interviewer);

        Project project = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered", true, interview);

        when(mockProjectService.getInterview(anyString(), anyLong())).thenReturn(rx.Observable.just(project));
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03

        activityController = Robolectric.buildActivity(InterviewDetailActivity.class, intent);
        subject = activityController.create().postCreate(null).get();
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void onPostCreate시_조회된_인터뷰_제목_정보를_화면에_보여준다() throws Exception {
        assertThat(subject.representationImageView.getTag(R.string.tag_key_image_url)).isEqualTo("www.imageUrl.com");
        assertThat(subject.appsDescriptionTextView.getText()).isEqualTo("'네이버웹툰'앱을 사랑하는 당신을 위한");
        assertThat(subject.projectNameTextView.getText()).isEqualTo("릴루미노");
        assertThat(subject.projectIntroduceTextView.getText()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)");
    }

    @Test
    public void onPostCreate시_조회된_인터뷰요약_정보를_화면에_보여준다() throws Exception {
        assertThat(subject.locationTextView.getText()).isEqualTo("우면사업장");
        assertThat(subject.dateTextView.getText()).isEqualTo("03/04 (일)");
        assertThat(subject.timeTextView.getText()).isEqualTo("약 70분");
        assertThat(subject.dDayTextView.getText()).isEqualTo("D-120");
    }

    @Test
    public void onPostCreate시_오늘날짜가_마감일이후인경우_D_Day는_0이_표시된다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1520035200000L);   //2017-12-01 03:03:03

        subject = Robolectric.buildActivity(InterviewDetailActivity.class, intent).create().postCreate(null).get();

        assertThat(subject.dDayTextView.getText()).isEqualTo("D-0");
    }

    @Test
    public void onPostCreate시_조회된_project_설명정보를_화면에_보여준다() throws Exception {
        assertThat(subject.projectDescriptionTextView.getText()).contains("안녕하세요 릴루미노팀입니다.");
        assertThat(subject.descriptionImageViewPager.getAdapter().getClass().getSimpleName()).contains(ImagePagerAdapter.class.getSimpleName());
    }

    @Test
    public void onPostCreate시_조회된_interviewer정보를_화면에_보여준다() throws Exception {
        assertThat(subject.interviewerPhotoImageView.getTag(R.string.tag_key_image_url)).isEqualTo(null);
        assertThat(subject.interviewerNameTextView.getText()).isEqualTo("인터뷰어");
        assertThat(subject.interviewerIntroduceTextView.getText()).contains("인터뷰어 소개입니다");
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
    public void BackButton클릭시_이전화면으로_복귀한다() throws Exception {
        subject.findViewById(R.id.back_button).performClick();
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void submitButton클릭시_인터뷰참여신청API를_호출한다() throws Exception {
        when(mockProjectService.postParticipate(anyString(), anyLong())).thenReturn(Observable.just(true));

        subject.findViewById(R.id.submit_button).performClick();

        verify(mockProjectService).postParticipate(anyString(), anyLong());
    }

    @Test
    public void 인터뷰참여신청성공시_인터뷰참여완료팝업을_표시한다() throws Exception {
        when(mockProjectService.postParticipate(anyString(), anyLong())).thenReturn(Observable.just(true));

        subject.findViewById(R.id.submit_button).performClick();

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("인터뷰참가신청완료!!");
    }

    @Test
    public void 인터뷰참여신청실패시_인터뷰참여실패팝업을_표시한다() throws Exception {
        when(mockProjectService.postParticipate(anyString(), anyLong())).thenReturn(Observable.error(new HttpException(Response.error(406, ResponseBody.create(null, "")))));

        subject.findViewById(R.id.submit_button).performClick();

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("406");
    }

}
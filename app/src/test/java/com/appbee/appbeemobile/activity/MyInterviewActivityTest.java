package com.appbee.appbeemobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.AppBeeConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MyInterviewActivityTest extends ActivityTest {

    @Inject
    ProjectService projectService;

    private MyInterviewActivity subject;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        subject = Robolectric.buildActivity(MyInterviewActivity.class).create().get();

        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(mock(Project.class));
        mockProjectList.add(mock(Project.class));

        when(projectService.getRegisteredInterviews()).thenReturn(rx.Observable.just(mockProjectList));
    }

    @Test
    public void onPostCreate호출시_신청한인터뷰목록조회API를_호출한다() throws Exception {
        subject.onPostCreate(null);

        verify(projectService).getRegisteredInterviews();
    }

    @Test
    public void 신청한인터뷰목록조회API호출시_한건이상의목록을_리턴받은경우_interviewRecyclerview에_결과표시한다() throws Exception {
        subject.onPostCreate(null);

        assertThat(subject.interviewRecyclerView.getAdapter().getItemCount()).isEqualTo(2);
        assertThat(subject.findViewById(R.id.not_found_registered_interview_text).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.findViewById(R.id.interview_recycler_view).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void home클릭시_메인화면으로_이동한다() throws Exception {
        MenuItem homeMenuItem = mock(MenuItem.class);
        when(homeMenuItem.getItemId()).thenReturn(android.R.id.home);

        subject.onOptionsItemSelected(homeMenuItem);

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void onClickInterviewDetail호출시_해당_인터뷰_상세조회_페이지로_이동한다() throws Exception {
        subject.onItemClickListener.onClickInterviewDetail("12345", 1L);

        Intent nextStartedIntent = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedIntent.getComponent().getClassName()).isEqualTo(InterviewDetailActivity.class.getName());
        assertThat(nextStartedIntent.getStringExtra(AppBeeConstants.EXTRA.PROJECT_ID)).isEqualTo("12345");
        assertThat(nextStartedIntent.getLongExtra(AppBeeConstants.EXTRA.INTERVIEW_SEQ, 0L)).isEqualTo(1L);
    }

    @Test
    public void onClickCancelInterview호출시_인터뷰취소요청페이지로_이동한다() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 11, 4);
        Date interviewDate = calendar.getTime();

        subject.onItemClickListener.onClickCancelInterview("12345", 11L, "time8", "WHOn", "확정", interviewDate, "우면사업장");

        ShadowActivity.IntentForResult nextStartedIntentForResult = shadowOf(subject).getNextStartedActivityForResult();
        assertThat(nextStartedIntentForResult.requestCode).isEqualTo(1001);
        assertThat(nextStartedIntentForResult.intent.getComponent().getClassName()).isEqualTo(CancelInterviewActivity.class.getName());
        assertThat(nextStartedIntentForResult.intent.getStringExtra(AppBeeConstants.EXTRA.PROJECT_ID)).isEqualTo("12345");
        assertThat(nextStartedIntentForResult.intent.getLongExtra(AppBeeConstants.EXTRA.INTERVIEW_SEQ, 0L)).isEqualTo(11L);
        assertThat(nextStartedIntentForResult.intent.getStringExtra(AppBeeConstants.EXTRA.PROJECT_NAME)).isEqualTo("WHOn");
        assertThat(nextStartedIntentForResult.intent.getStringExtra(AppBeeConstants.EXTRA.INTERVIEW_STATUS)).isEqualTo("확정");
        assertThat(nextStartedIntentForResult.intent.getSerializableExtra(AppBeeConstants.EXTRA.INTERVIEW_DATE)).isEqualTo(interviewDate);
        assertThat(nextStartedIntentForResult.intent.getStringExtra(AppBeeConstants.EXTRA.LOCATION)).isEqualTo("우면사업장");
    }

    @Test
    public void 다가오는인터뷰가_null인경우_인터뷰목록표시없이_유저인터뷰가_없다는_텍스트를_표시한다() throws Exception {
        when(projectService.getRegisteredInterviews()).thenReturn(Observable.just(null));

        subject.onPostCreate(null);

        assertThat(subject.findViewById(R.id.not_found_registered_interview_text).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.interview_recycler_view).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void 다가오는인터뷰가_없는경우_인터뷰목록표시없이_유저인터뷰가_없다는_텍스트를_표시한다() throws Exception {
        when(projectService.getRegisteredInterviews()).thenReturn(Observable.just(new ArrayList<>()));

        subject.onPostCreate(null);

        assertThat(subject.findViewById(R.id.not_found_registered_interview_text).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.interview_recycler_view).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void 인터뷰취소완료시_다가오는인터뷰목록을_다시_로드한다() throws Exception {
        subject.onActivityResult(1001, Activity.RESULT_OK, null);

        verify(projectService).getRegisteredInterviews();
    }
}
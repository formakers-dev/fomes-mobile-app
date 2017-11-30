package com.appbee.appbeemobile.activity;

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
        assertThat(subject.findViewById(R.id.not_found_interview_text).getVisibility()).isEqualTo(View.GONE);
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
    public void onSelectProject호출시_해당프로젝트_상세조회_페이지로_이동한다() throws Exception {
        subject.actionListener.onSelectProject("12345");

        Intent nextStartedIntent = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedIntent.getComponent().getShortClassName()).isEqualTo(".activity.ProjectDetailActivity");
        assertThat(nextStartedIntent.getStringExtra(AppBeeConstants.EXTRA.PROJECT_ID)).isEqualTo("12345");
    }

    @Test
    public void onRequestToCancelProject호출시_인터뷰취소요청페이지로_이동한다() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 11, 4);
        Date interviewDate = calendar.getTime();

        subject.actionListener.onRequestToCancelInterview("12345", 11L, "time8", "WHOn", "확정", interviewDate, "우면사업장");

        Intent nextStartedIntent = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedIntent.getComponent().getShortClassName()).isEqualTo(".activity.CancelInterviewActivity");
        assertThat(nextStartedIntent.getStringExtra(AppBeeConstants.EXTRA.PROJECT_ID)).isEqualTo("12345");
        assertThat(nextStartedIntent.getLongExtra(AppBeeConstants.EXTRA.INTERVIEW_SEQ, 0L)).isEqualTo(11L);
        assertThat(nextStartedIntent.getStringExtra(AppBeeConstants.EXTRA.PROJECT_NAME)).isEqualTo("WHOn");
        assertThat(nextStartedIntent.getStringExtra(AppBeeConstants.EXTRA.INTERVIEW_STATUS)).isEqualTo("확정");
        assertThat(nextStartedIntent.getSerializableExtra(AppBeeConstants.EXTRA.INTERVIEW_DATE)).isEqualTo(interviewDate);
        assertThat(nextStartedIntent.getStringExtra(AppBeeConstants.EXTRA.LOCATION)).isEqualTo("우면사업장");
    }

    @Test
    public void 다가오는인터뷰가_null인경우_인터뷰목록표시없이_유저인터뷰가_없다는_텍스트를_표시한다() throws Exception {
        when(projectService.getRegisteredInterviews()).thenReturn(Observable.just(null));

        subject.onPostCreate(null);

        assertThat(subject.findViewById(R.id.not_found_interview_text).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.interview_recycler_view).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void 다가오는인터뷰가_없는경우_인터뷰목록표시없이_유저인터뷰가_없다는_텍스트를_표시한다() throws Exception {
        when(projectService.getRegisteredInterviews()).thenReturn(Observable.just(new ArrayList<>()));

        subject.onPostCreate(null);

        assertThat(subject.findViewById(R.id.not_found_interview_text).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.interview_recycler_view).getVisibility()).isEqualTo(View.GONE);
    }
}
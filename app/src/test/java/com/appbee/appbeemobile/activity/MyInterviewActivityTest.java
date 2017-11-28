package com.appbee.appbeemobile.activity;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
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

        when(projectService.getRegisteredInterviews()).thenReturn(Observable.just(null));
    }

    @Test
    public void onPostCreate호출시_신청한인터뷰목록조회API를_호출한다() throws Exception {
        subject.onPostCreate(null);

        verify(projectService).getRegisteredInterviews();
    }

    @Test
    public void 신청한인터뷰목록조회API호출시_한건이상의목록을_리턴받은경우_interviewRecyclerview에_결과를_셋팅한다() throws Exception {
        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(mock(Project.class));
        mockProjectList.add(mock(Project.class));

        when(projectService.getRegisteredInterviews()).thenReturn(rx.Observable.just(mockProjectList));

        subject.onPostCreate(null);

        assertThat(subject.interviewRecyclerView.getAdapter().getItemCount()).isEqualTo(2);
    }

    @Test
    public void backButton클릭시_메인화면으로_이동한다() throws Exception {
        subject.onPostCreate(null);

        subject.findViewById(R.id.back_button).performClick();

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}
package com.appbee.appbeemobile.fragment;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.adapter.InterviewListAdapter;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class InterviewListFragmentTest {

    @Inject
    ProjectService mockProjectService;

    private InterviewListFragment subject;
    private SupportFragmentController<InterviewListFragment> controller;
    private Unbinder unbinder;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        subject = new InterviewListFragment();

        controller = SupportFragmentController.of(subject);
        controller.create().start();

        unbinder = ButterKnife.bind(this, subject.getView());
    }

    @After
    public void tearDown() throws Exception {
        if(unbinder != null) {
            unbinder.unbind();
        }
    }

    @Test
    public void onPostCreate시_당신의참여를기다리는프로젝트를_표시하기위한_Adapter를_매핑한다() throws Exception {
        assertThat(subject.interviewListRecyclerView.getAdapter().getClass().getSimpleName()).contains(InterviewListAdapter.class.getSimpleName());
    }

    @Test
    public void onResume시_프로젝트목록정보를_갱신한다() throws Exception {
        List<Project> mockInterviewList = new ArrayList<>();
        mockInterviewList.add(new Project("projectId4", "리얼포토", "증강현실로 한장의 사진에 담는 나만의 추억", "temporary"));
        mockInterviewList.add(new Project("projectId5", "엔빵", "모임별로 엔빵해", "temporary"));
        mockInterviewList.add(new Project("projectId6", "겜돌이", "게임하자", "temporary"));
        when(mockProjectService.getAllInterviews()).thenReturn(Observable.just(mockInterviewList));

        controller.resume();

        verify(mockProjectService).getAllInterviews();
        InterviewListAdapter interviewListAdapter = (InterviewListAdapter) subject.interviewListRecyclerView.getAdapter();
        assertThat(interviewListAdapter.getItemCount()).isEqualTo(4);
        assertThat(interviewListAdapter.getItem(0)).isEqualTo(mockInterviewList.get(0));
        assertThat(interviewListAdapter.getItem(1)).isEqualTo(mockInterviewList.get(1));
        assertThat(interviewListAdapter.getItem(2)).isEqualTo(mockInterviewList.get(2));
    }

}
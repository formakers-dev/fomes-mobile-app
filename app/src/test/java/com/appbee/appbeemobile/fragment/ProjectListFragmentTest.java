package com.appbee.appbeemobile.fragment;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.adapter.InterviewListAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProjectListFragmentTest {

    private ProjectListFragment subject;
    private Unbinder unbinder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new ProjectListFragment();

        SupportFragmentTestUtil.startFragment(subject);
        unbinder = ButterKnife.bind(this, subject.getView());
    }

    @After
    public void tearDown() throws Exception {
        unbinder.unbind();
    }

    @Test
    public void onPostCreate시_당신의참여를기다리는프로젝트를_표시하기위한_Adapter를_매핑한다() throws Exception {
        assertThat(subject.projectListRecyclerView.getAdapter().getClass().getSimpleName()).contains(InterviewListAdapter.class.getSimpleName());
    }

//    @Test
//    public void onResume시_프로젝트목록정보를_갱신한다() throws Exception {
//        List<Project> mockProjectList = new ArrayList<>();
//        mockProjectList.add(new Project("projectId4", "리얼포토", "증강현실로 한장의 사진에 담는 나만의 추억", "temporary"));
//        mockProjectList.add(new Project("projectId5", "엔빵", "모임별로 엔빵해", "temporary"));
//        mockProjectList.add(new Project("projectId6", "겜돌이", "게임하자", "temporary"));
//        when(mockProjectService.getAllProjects()).thenReturn(Observable.just(mockProjectList));
//
//        subject.onResume();
//
//        verify(mockProjectService).getAllProjects();
//        InterviewListAdapter projectListAdapter = (InterviewListAdapter) subject.projectListRecyclerView.getAdapter();
//        assertThat(projectListAdapter.getItemCount()).isEqualTo(4);
//        assertThat(projectListAdapter.getItem(0)).isEqualTo(mockProjectList.get(0));
//        assertThat(projectListAdapter.getItem(1)).isEqualTo(mockProjectList.get(1));
//        assertThat(projectListAdapter.getItem(2)).isEqualTo(mockProjectList.get(2));
//    }

}
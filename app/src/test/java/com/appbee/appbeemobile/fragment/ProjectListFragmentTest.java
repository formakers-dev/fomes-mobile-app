package com.appbee.appbeemobile.fragment;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.adapter.ProjectListAdapter;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.network.ProjectService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
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
public class ProjectListFragmentTest {

    @Inject
    ProjectService mockProjectService;

    private ProjectListFragment subject;
    private SupportFragmentController controller;
    private Unbinder unbinder;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        MockitoAnnotations.initMocks(this);
        subject = new ProjectListFragment();

        controller = SupportFragmentController.of(subject);
        controller.create().start();

        unbinder = ButterKnife.bind(this, subject.getView());
    }

    @After
    public void tearDown() throws Exception {
        if(unbinder!= null) {
            unbinder.unbind();
        }
    }

    @Test
    public void onPostCreate시_프로젝트리스트를_표시하기위한_Adapter를_매핑한다() throws Exception {
        assertThat(subject.projectListRecyclerView.getAdapter().getClass().getSimpleName()).contains(ProjectListAdapter.class.getSimpleName());
    }

    @Test
    public void onResume시_프로젝트목록정보를_갱신한다() throws Exception {
        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(new Project("projectId4", "리얼포토", "증강현실로 한장의 사진에 담는 나만의 추억", "temporary"));
        mockProjectList.add(new Project("projectId5", "엔빵", "모임별로 엔빵해", "temporary"));
        mockProjectList.add(new Project("projectId6", "겜돌이", "게임하자", "temporary"));
        when(mockProjectService.getAllProjects()).thenReturn(Observable.just(mockProjectList));

        controller.resume();

        verify(mockProjectService).getAllProjects();
        ProjectListAdapter projectListAdapter = (ProjectListAdapter) subject.projectListRecyclerView.getAdapter();
        assertThat(projectListAdapter.getItemCount()).isEqualTo(4);
        assertThat(projectListAdapter.getItem(0)).isEqualTo(mockProjectList.get(0));
        assertThat(projectListAdapter.getItem(1)).isEqualTo(mockProjectList.get(1));
        assertThat(projectListAdapter.getItem(2)).isEqualTo(mockProjectList.get(2));
    }
}
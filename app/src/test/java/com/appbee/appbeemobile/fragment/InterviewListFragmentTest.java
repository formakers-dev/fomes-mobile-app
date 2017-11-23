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
        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Project.Person owner = new Project.Person("프로젝트 담당자", "www.projectOwnerImage.com", "프로젝트 담당자 소개입니다");

        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered"));
        mockProjectList.add(new Project("projectId2", "릴루미노2", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)2", imageObject, "안녕하세요 릴루미노팀입니다.2", imageObjectList, owner, "registered"));

        when(mockProjectService.getAllInterviews()).thenReturn(Observable.just(mockProjectList));

        controller.resume();

        verify(mockProjectService).getAllInterviews();
        InterviewListAdapter interviewListAdapter = (InterviewListAdapter) subject.interviewListRecyclerView.getAdapter();
        assertThat(interviewListAdapter.getItemCount()).isEqualTo(3);
        assertThat(interviewListAdapter.getItem(1)).isEqualTo(mockProjectList.get(0));
        assertThat(interviewListAdapter.getItem(2)).isEqualTo(mockProjectList.get(1));
    }

}
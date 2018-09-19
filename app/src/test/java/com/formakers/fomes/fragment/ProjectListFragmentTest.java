package com.formakers.fomes.fragment;

import android.view.View;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.adapter.MainListAdapter;
import com.formakers.fomes.model.Project;
import com.formakers.fomes.common.network.ProjectService;

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
        assertThat(subject.projectListRecyclerView.getAdapter().getClass().getSimpleName()).contains(MainListAdapter.class.getSimpleName());
    }

    @Test
    public void onResume시_프로젝트목록정보를_갱신한다() throws Exception {
        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Project.Person owner = new Project.Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        List<Project> mockProjectList = new ArrayList<>();
        mockProjectList.add(new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered"));
        mockProjectList.add(new Project("projectId2", "릴루미노2", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)2", imageObject, "안녕하세요 릴루미노팀입니다.2", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered"));

        when(mockProjectService.getAllProjects()).thenReturn(Observable.just(mockProjectList));

        controller.resume();

        verify(mockProjectService).getAllProjects();
        MainListAdapter mainListAdapter = (MainListAdapter) subject.projectListRecyclerView.getAdapter();
        assertThat(mainListAdapter.getItemCount()).isEqualTo(3);
        assertThat(mainListAdapter.getItem(1)).isEqualTo(mockProjectList.get(0));
        assertThat(mainListAdapter.getItem(2)).isEqualTo(mockProjectList.get(1));
    }

    @Test
    public void 프로젝트목록이_null인경우_프로젝트가_없다는_텍스트를_표시한다() throws Exception {
        when(mockProjectService.getAllProjects()).thenReturn(Observable.just(null));

        controller.resume();

        assertThat(subject.getView().findViewById(R.id.empty_content_text_view).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void 프로젝트목록이_없는경우_프로젝트가_없다는_텍스트를_표시한다() throws Exception {
        when(mockProjectService.getAllProjects()).thenReturn(Observable.just(new ArrayList<>()));

        controller.resume();

        assertThat(subject.getView().findViewById(R.id.empty_content_text_view).getVisibility()).isEqualTo(View.VISIBLE);
    }
}
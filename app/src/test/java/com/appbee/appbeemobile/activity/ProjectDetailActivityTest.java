package com.appbee.appbeemobile.activity;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.model.Project.ImageObject;
import com.appbee.appbeemobile.model.Project.Person;
import com.appbee.appbeemobile.network.ProjectService;

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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

        intent.putExtra("EXTRA_PROJECT_ID", "projectId");

        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ImageObject imageObject = new ImageObject("www.imageUrl.com", "urlName");

        List<ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new ImageObject("www.imageUrl.com3", "urlName3"));

        Person owner = new Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        Project project = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered");

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
        assertThat(subject.representationImageView.getTag(R.string.tag_key_image_url)).isEqualTo("www.imageUrl.com");
        assertThat(subject.projectNameTextView.getText()).isEqualTo("릴루미노");
        assertThat(subject.projectIntroduceTextView.getText()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)");
    }


    @Test
    public void onPostCreate시_조회된_project_상세설명정보를_화면에_보여준다() throws Exception {
        assertThat(subject.descriptionImageViewPager.getAdapter().getCount()).isEqualTo(3);
//        assertThat(subject.descriptionImageViewPager.getChildAt(0).getTag()).isEqualTo("www.imageUrl.com1");
//        assertThat(subject.descriptionImageViewPager.getChildAt(1).getTag()).isEqualTo("www.imageUrl.com2");
//        assertThat(subject.descriptionImageViewPager.getChildAt(2).getTag()).isEqualTo("www.imageUrl.com3");
        assertThat(subject.projectDescriptionTextView.getText()).isEqualTo("안녕하세요 릴루미노팀입니다.");
    }

    @Test
    public void onPostCreate시_조회된_owner정보를_화면에_보여준다() throws Exception {
        assertThat(subject.interviewerPhotoImageView.getTag(R.string.tag_key_image_url)).isEqualTo("www.projectOwnerImage.com");
        assertThat(subject.interviewerNameTextView.getText()).isEqualTo("프로젝트 담당자");
        assertThat(subject.interviewerIntroduceTextView.getText()).contains("프로젝트 담당자 소개입니다");
    }

    @Test
    public void BackButton클릭시_이전화면으로_복귀한다() throws Exception {
        subject.findViewById(R.id.back_button).performClick();
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}
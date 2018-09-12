package com.formakers.fomes.activity;

import android.app.Fragment;
import android.content.Intent;
import android.view.View;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.fragment.ProjectYoutubePlayerFragment;
import com.formakers.fomes.helper.GoogleSignInAPIHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.helper.TimeHelper;
import com.formakers.fomes.model.Project;
import com.formakers.fomes.model.Project.ImageObject;
import com.formakers.fomes.model.Project.Person;
import com.formakers.fomes.network.ProjectService;
import com.formakers.fomes.network.UserService;

import org.junit.After;
import org.junit.Before;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProjectDetailActivityTest extends BaseActivityTest<ProjectDetailActivity> {
    private ProjectDetailActivity subject;

    @Inject
    ProjectService mockProjectService;

    @Inject
    TimeHelper mockTimeHelper;

    @Inject
    GoogleSignInAPIHelper mockGoogleSignInAPIHelper;

    @Inject
    UserService mockUserService;

    @Inject
    SharedPreferencesHelper mockSharedPreferencesHelper;

    private ActivityController<ProjectDetailActivity> activityController;

    private Intent intent = new Intent();

    private Project mockProject;

    public ProjectDetailActivityTest() {
        super(ProjectDetailActivity.class);
    }

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

        mockProject = new Project("projectId", "릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered");

        when(mockProjectService.getProject(anyString())).thenReturn(rx.Observable.just(mockProject));
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03

        activityController = getActivityController(intent);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        super.tearDown();
    }

    @Test
    public void onPostCreate시_전달받은_projectID에_해당하는_project정보를_조회한다() throws Exception {
        subject = activityController.create().postCreate(null).get();

        assertThat(subject.getIntent().getStringExtra("EXTRA_PROJECT_ID")).isEqualTo("projectId");
        verify(mockProjectService).getProject(eq("projectId"));
    }

    @Test
    public void onPostCreate시_조회된_project_정보를_화면에_보여준다() throws Exception {
        subject = activityController.create().postCreate(null).get();

        assertThat(subject.representationImageView.getTag(R.string.tag_key_image_url)).isEqualTo("www.imageUrl.com");
        assertThat(subject.projectNameTextView.getText()).isEqualTo("릴루미노");
        assertThat(subject.projectIntroduceTextView.getText()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)");

        // 비디오레이아웃을 보여준다
        Fragment youTubePlayerFragment = subject.getFragmentManager().findFragmentByTag("YouTubePlayerFragment");
        assertThat(youTubePlayerFragment).isNotNull();
        assertThat(youTubePlayerFragment.getArguments().getString(ProjectYoutubePlayerFragment.EXTRA_YOUTUBE_ID)).isEqualTo("o-rnYD47wmo");
        assertThat(subject.findViewById(R.id.project_video_layout).getVisibility()).isEqualTo(View.VISIBLE);

        // 상세설명정보를_화면에_보여준다
        assertThat(subject.descriptionImageRecyclerView.getAdapter().getItemCount()).isEqualTo(3);
        assertThat(subject.projectDescriptionTextView.getText()).isEqualTo("안녕하세요 릴루미노팀입니다.");

        // owner정보를_화면에_보여준다
        assertThat(subject.ownerPhotoImageView.getTag(R.string.tag_key_image_url)).isEqualTo("www.projectOwnerImage.com");
        assertThat(subject.ownerNameTextView.getText()).isEqualTo("프로젝트 담당자");
        assertThat(subject.ownerIntroduceTextView.getText()).contains("프로젝트 담당자 소개입니다");
    }

    @Test
    public void onPostCreate시_비디오정보가_없는경우_비디오레이아웃을_숨긴다() throws Exception {
        mockProject.setVideoUrl("");

        subject = activityController.create().postCreate(null).get();

        assertHideVideoLayout();
    }

    @Test
    public void onPostCreate시_비디오정보가_유투브URL이_아닌_경우_비디오레이아웃을_숨긴다() throws Exception {
        mockProject.setVideoUrl("http://www.naver.com/4.mp4");

        subject = activityController.create().postCreate(null).get();

        assertHideVideoLayout();
    }

    private void assertHideVideoLayout() {
        assertThat(subject.getFragmentManager().findFragmentByTag("YouTubePlayerFragment")).isNull();
        assertThat(subject.findViewById(R.id.project_video_layout).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void BackButton클릭시_이전화면으로_복귀한다() throws Exception {
        subject = activityController.create().postCreate(null).get();

        subject.findViewById(R.id.back_button).performClick();
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }



}
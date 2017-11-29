package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.adapter.DetailPlansAdapter;
import com.appbee.appbeemobile.adapter.ImagePagerAdapter;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.Project;
import com.appbee.appbeemobile.model.Project.Interview;
import com.appbee.appbeemobile.model.Project.Person;
import com.appbee.appbeemobile.network.ProjectService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
public class InterviewDetailActivityTest extends ActivityTest {
    private InterviewDetailActivity subject;

    @Inject
    ProjectService mockProjectService;

    @Inject
    TimeHelper mockTimeHelper;

    private ActivityController<InterviewDetailActivity> activityController;

    private Intent intent = new Intent();
    private int dimGrayColorId;
    private int yellowColorId;
    private int warmGrayColorId;
    private int grayColorId;
    private int dimForegroundColorId;
    private int transparentColorId;

    @Before
    public void setUp() throws Exception {

        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        intent.putExtra("EXTRA_INTERVIEW_SEQ", 1L);
        intent.putExtra("EXTRA_PROJECT_ID", "projectId");

        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Person owner = new Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        // month 1월
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 2, 4);
        Date interviewDate = calendar.getTime();
        calendar.set(2018, 2, 2);
        Date openDate = calendar.getTime();
        calendar.set(2018, 2, 3);
        Date closeDate = calendar.getTime();

        Interview interview = new Interview(1L, Collections.singletonList(new AppInfo("com.naver.webtoon", "네이버웹툰")), "인터뷰소개", interviewDate, openDate, closeDate, "우면사업장", "오시는길입니다", 5, Arrays.asList("time8", "time9", "time10"), "", "", "오프라인 인터뷰");
        Project project = new Project("projectId", "[앱] 릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered", interview);

        when(mockProjectService.getInterview(anyString(), anyLong())).thenReturn(rx.Observable.just(project));
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03

        activityController = Robolectric.buildActivity(InterviewDetailActivity.class, intent);
        subject = activityController.create().postCreate(null).get();

        extractColorIds();
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void onPostCreate시_조회된_인터뷰_상세_정보를_화면에_보여준다() throws Exception {
        // 프로젝트 요약 내용
        assertThat(subject.representationImageView.getTag(R.string.tag_key_image_url)).isEqualTo("www.imageUrl.com");
        assertThat(subject.appsDescriptionTextView.getText()).isEqualTo("'네이버웹툰' 앱 유저에게 추천");
        assertThat(subject.projectNameTextView.getText()).isEqualTo("[앱] 릴루미노");
        assertThat(subject.projectIntroduceTextView.getText()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)");

        // 인터뷰요약 정보
        assertThat(subject.locationTextView.getText()).isEqualTo("우면사업장");
        assertThat(subject.dateTextView.getText()).isEqualTo("03/04 (일)");
        assertThat(subject.dDayTextView.getText()).isEqualTo("D-120");

        // 프로젝트 설명
        assertThat(subject.projectDescriptionTextView.getText()).contains("안녕하세요 릴루미노팀입니다.");
        assertThat(subject.descriptionImageViewPager.getAdapter().getClass().getSimpleName()).contains(ImagePagerAdapter.class.getSimpleName());

        // 조회된 인터뷰 소개 정보
        assertThat(subject.interviewIntroduceTextView.getText()).contains("인터뷰소개");

        // 조회된 대표자 정보
        assertThat(subject.ownerPhotoImageView.getTag(R.string.tag_key_image_url)).isEqualTo("www.projectOwnerImage.com");
        assertThat(subject.ownerNameTextView.getText()).isEqualTo("프로젝트 담당자");
        assertThat(subject.ownerIntroduceTextView.getText()).isEqualTo("프로젝트 담당자 소개입니다");
    }

    @Test
    public void onPostCreate시_오늘날짜가_마감일이후인경우_D_Day는_0이_표시된다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1520035200000L);   //2017-12-01 03:03:03

        subject = Robolectric.buildActivity(InterviewDetailActivity.class, intent).create().postCreate(null).get();

        assertThat(subject.dDayTextView.getText()).isEqualTo("D-0");
    }

    @Test
    public void onPostCreate시_조회된_project_설명정보를_화면에_보여준다() throws Exception {
        assertThat(subject.projectDescriptionTextView.getText()).contains("안녕하세요 릴루미노팀입니다.");
        assertThat(subject.descriptionImageViewPager.getAdapter().getClass().getSimpleName()).contains(ImagePagerAdapter.class.getSimpleName());
    }

    @Test
    @Config(minSdk = 22)
    public void onPostCreate시_신청하지않은인터뷰가조회되면_인터뷰신청하기형태의_submit버튼이나타난다() throws Exception {
        assertThat(subject.submitArrowButton.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(shadowOf(subject.submitArrowButton.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.submit_open);
        assertThat(subject.submitArrowButton.isClickable()).isTrue();
        assertThat(subject.submitButton.getText()).isEqualTo("유저 인터뷰 신청하기");
        assertThat(subject.submitButton.getCurrentTextColor()).isEqualTo(grayColorId);
        assertThat(((ColorDrawable) subject.submitButton.getBackground()).getColor()).isEqualTo(yellowColorId);
        assertThat(subject.submitButton.isClickable()).isTrue();
    }

    @Test
    @Config(minSdk = 22)
    public void onPostCreate시_이미신청된인터뷰가조회되면_이미신청한인터뷰안내형태의_submit버튼이나타난다다() throws Exception {
        mockRegisteredInterview();

        subject = Robolectric.buildActivity(InterviewDetailActivity.class, intent).create().postCreate(null).get();

        assertThat(subject.submitArrowButton.getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.submitButton.getText()).isEqualTo("이미 신청한 인터뷰입니다.");
        assertThat(subject.submitButton.getCurrentTextColor()).isEqualTo(warmGrayColorId);
        assertThat(((ColorDrawable) subject.submitButton.getBackground()).getColor()).isEqualTo(dimGrayColorId);
        assertThat(subject.submitButton.isClickable()).isFalse();
    }


    @Test
    public void onPostCreate시_세부일정선택영역이_나타나지않는다() throws Exception {
        assertThat(subject.detailPlansLayout.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void BackButton클릭시_이전화면으로_복귀한다() throws Exception {
        subject.findViewById(R.id.back_button).performClick();
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    @Config(minSdk = 22)
    public void 세부일정선택영역이_나타나지_않은_상태에서_submit클릭시_세부일정선택영역을_표시한다() throws Exception {
        subject.submitButton.performClick();

        assertThat(subject.detailPlansLayout.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.detailPlansTitle.getText()).isEqualTo("[앱] 릴루미노 유저 인터뷰");
        assertThat(subject.detailPlansDescription.getText()).isEqualTo("3월 4일 (일) 우면사업장");
        assertThat(subject.detailPlansRecyclerView.getAdapter().getItemCount()).isEqualTo(3);
        assertThat(((DetailPlansAdapter) subject.detailPlansRecyclerView.getAdapter()).getItem(0)).isEqualTo("time8");
        assertThat(((DetailPlansAdapter) subject.detailPlansRecyclerView.getAdapter()).getItem(1)).isEqualTo("time9");
        assertThat(((DetailPlansAdapter) subject.detailPlansRecyclerView.getAdapter()).getItem(2)).isEqualTo("time10");

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNull();

        assertThat(shadowOf(subject.submitArrowButton.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.submit_close);
        assertThat(((ColorDrawable) subject.scrollViewLayout.getForeground()).getColor()).isEqualTo(dimForegroundColorId);
    }

    @Test
    public void 세부일정선택영역이_나타나지_않은_상태에서_submitArrow버튼클릭시_세부일정선택영역을_표시한다() throws Exception {
        subject.submitArrowButton.performClick();

        assertThat(subject.detailPlansLayout.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.detailPlansTitle.getText()).isEqualTo("[앱] 릴루미노 유저 인터뷰");
        assertThat(subject.detailPlansDescription.getText()).isEqualTo("3월 4일 (일) 우면사업장");
        assertThat(subject.detailPlansRecyclerView.getAdapter().getItemCount()).isEqualTo(3);
        assertThat(((DetailPlansAdapter) subject.detailPlansRecyclerView.getAdapter()).getItem(0)).isEqualTo("time8");
        assertThat(((DetailPlansAdapter) subject.detailPlansRecyclerView.getAdapter()).getItem(1)).isEqualTo("time9");
        assertThat(((DetailPlansAdapter) subject.detailPlansRecyclerView.getAdapter()).getItem(2)).isEqualTo("time10");

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNull();
    }

    @Test
    public void 세부일정선택영역이_나타난_상태에서_세부일정을_선택하고_submitButton클릭시_인터뷰참여신청API를_호출한다() throws Exception {
        subject.submitButton.performClick();

        when(mockProjectService.postParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.just(true));
        ((DetailPlansAdapter) subject.detailPlansRecyclerView.getAdapter()).setSelectedTimeSlot(0);

        subject.submitButton.performClick();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockProjectService).postParticipate(anyString(), anyLong(), captor.capture());
        assertThat(captor.getValue()).isEqualTo("time8");
    }

    @Test
    public void 세부일정선택영역이_나타난_상태에서_submitArrowButton클릭시_세부일정선택영이_사라진다() throws Exception {
        subject.submitArrowButton.performClick();

        subject.submitArrowButton.performClick();

        assertThat(subject.detailPlansLayout.getVisibility()).isEqualTo(View.GONE);
        assertThat(shadowOf(subject.submitArrowButton.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.submit_open);
    }

    @Test
    @Config(minSdk = 22)
    public void 세부일정선택영이_나타나면_scrollView영역이Dim처리된다() throws Exception {
        subject.submitArrowButton.performClick();

        assertThat(((ColorDrawable) subject.scrollViewLayout.getForeground()).getColor()).isEqualTo(dimForegroundColorId);

        subject.submitArrowButton.performClick();

        assertThat(((ColorDrawable) subject.scrollViewLayout.getForeground()).getColor()).isEqualTo(transparentColorId);
    }

    @Test
    public void 인터뷰참여신청성공시_인터뷰참여완료팝업을_표시한다() throws Exception {
        subject.submitButton.performClick();

        ((DetailPlansAdapter) subject.detailPlansRecyclerView.getAdapter()).setSelectedTimeSlot(0);
        when(mockProjectService.postParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.just(true));

        subject.submitButton.performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        View rootView = shadowOf(dialog).getView();
        assertThat(shadowOf(((ImageView) rootView.findViewById(R.id.dialog_image)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.dialog_success_image);
        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("신청이 완료되었습니다.");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).contains("인터뷰 확정이 되면");
    }

    @Test
    public void 인터뷰_참여완료_팝업의_확인버튼을클릭시_팝업을_닫고_다가오는_유저인터뷰_페이지로_이동한다() throws Exception {
        subject.submitButton.performClick();

        ((DetailPlansAdapter) subject.detailPlansRecyclerView.getAdapter()).setSelectedTimeSlot(0);
        when(mockProjectService.postParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.just(true));

        subject.submitButton.performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        assertThat(shadowOf(dialog).hasBeenDismissed()).isTrue();
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(MyInterviewActivity.class.getName());
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void 인터뷰참여신청실패시_인터뷰참여실패팝업을_표시한다() throws Exception {
        subject.submitButton.performClick();

        ((DetailPlansAdapter) subject.detailPlansRecyclerView.getAdapter()).setSelectedTimeSlot(0);

        int errorCode = 406;
        when(mockProjectService.postParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.error(new HttpException(Response.error(errorCode, ResponseBody.create(null, "")))));

        subject.submitButton.performClick();

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo(String.valueOf(errorCode));
    }

    @Test
    public void 세부일정선택영역이_나타난_상태에서_세부일정을_선택하지않은상태에서_submitButton클릭시_경고팝업을_표시한다() throws Exception {
        subject.submitButton.performClick();

        subject.submitButton.performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        View rootView = shadowAlertDialog.getView();
        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("시간을 선택해주세요.");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).isEqualTo("세부일정 선택은 필수입니다.");

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        assertThat(shadowAlertDialog.hasBeenDismissed()).isTrue();
    }

    private void mockRegisteredInterview() {
        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Person owner = new Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        // month 1월
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 2, 4);
        Date interviewDate = calendar.getTime();
        calendar.set(2018, 2, 2);
        Date openDate = calendar.getTime();
        calendar.set(2018, 2, 3);
        Date closeDate = calendar.getTime();

        Interview interview = new Interview(1L, Collections.singletonList(new AppInfo("com.naver.webtoon", "네이버웹툰")), "인터뷰소개", interviewDate, openDate, closeDate, "우면사업장", "오시는길입니다", 5, Arrays.asList("time8", "time9", "time10"), "time8", "", "오프라인 인터뷰");
        Project project = new Project("projectId", "[앱] 릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", imageObjectList, owner, "registered", interview);

        when(mockProjectService.getInterview(anyString(), anyLong())).thenReturn(rx.Observable.just(project));
    }

    private void extractColorIds() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dimGrayColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_dim_gray, null);
            dimForegroundColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_dim_foreground, null);
            warmGrayColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_warm_gray, null);
            grayColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_gray, null);
            yellowColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_yellow, null);
            transparentColorId = RuntimeEnvironment.application.getResources().getColor(android.R.color.transparent, null);
        } else {
            dimGrayColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_dim_gray);
            dimForegroundColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_dim_foreground);
            warmGrayColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_warm_gray);
            grayColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_gray);
            yellowColorId = RuntimeEnvironment.application.getResources().getColor(R.color.appbee_yellow);
            transparentColorId = RuntimeEnvironment.application.getResources().getColor(android.R.color.transparent);
        }
    }
}
package com.formakers.fomes.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.adapter.DescriptionImageAdapter;
import com.formakers.fomes.fragment.ProjectYoutubePlayerFragment;
import com.formakers.fomes.helper.ResourceHelper;
import com.formakers.fomes.helper.TimeHelper;
import com.formakers.fomes.model.AppInfo;
import com.formakers.fomes.model.Project;
import com.formakers.fomes.model.Project.Interview;
import com.formakers.fomes.model.Project.Person;
import com.formakers.fomes.common.network.ProjectService;
import com.formakers.fomes.util.FomesConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Completable;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
public class InterviewDetailActivityTest extends BaseActivityTest<InterviewDetailActivity> {
    private final int DIM_GRAY_COLOR = 1;
    private final int YELLOW_COLOR = 2;
    private final int WARM_GRAY_COLOR = 3;
    private final int GRAY_COLOR = 4;
    private final int DIM_FOREGROUND_COLOR = 5;
    private final int TRANSPARENT_COLOR = 6;

    @Inject
    ProjectService mockProjectService;

    @Inject
    TimeHelper mockTimeHelper;

    @Inject
    ResourceHelper mockResourceHelper;

    private Intent intent = new Intent();
    private Project mockProject;
    private ActivityController<InterviewDetailActivity> activityController;

    public InterviewDetailActivityTest() {
        super(InterviewDetailActivity.class);
    }

    @Before
    public void setUp() throws Exception {

        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        intent.putExtra("EXTRA_INTERVIEW_SEQ", 1L);
        intent.putExtra("EXTRA_PROJECT_ID", "projectId");

        setupMockProject();
        when(mockTimeHelper.getCurrentTime()).thenReturn(1509667200000L);   //2017-11-03
        mockColorValue();
    }

    private void setupMockProject() {
        Project.ImageObject imageObject = new Project.ImageObject("www.imageUrl.com", "urlName");

        List<Project.ImageObject> imageObjectList = new ArrayList<>();
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com1", "urlName1"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com2", "urlName2"));
        imageObjectList.add(new Project.ImageObject("www.imageUrl.com3", "urlName3"));

        Person owner = new Person("프로젝트 담당자", new Project.ImageObject("www.projectOwnerImage.com", "projectOwnerImageName"), "프로젝트 담당자 소개입니다");

        int MARCH = 2;
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, MARCH, 4);
        Date interviewDate = calendar.getTime();
        calendar.set(2018, MARCH, 2);
        Date openDate = calendar.getTime();
        calendar.set(2018, MARCH, 3);
        Date closeDate = calendar.getTime();

        Interview interview = new Interview(1L, Collections.singletonList(new AppInfo("com.naver.webtoon", "네이버웹툰")), "인터뷰소개", interviewDate, openDate, closeDate, "우면사업장", "오시는길입니다", 5, Arrays.asList("time8", "time9", "time10"), "", "", "오프라인 인터뷰", "스벅쿠폰 3만원");
        mockProject = new Project("projectId", "[앱] 릴루미노", "저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)", imageObject, "안녕하세요 릴루미노팀입니다.", "https://www.youtube.com/watch?v=o-rnYD47wmo&feature=youtu.be", imageObjectList, owner, "registered");
        mockProject.setInterview(interview);

        when(mockProjectService.getInterview(anyString(), anyLong())).thenReturn(Observable.just(mockProject));
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        super.tearDown();
    }

    @Test
    public void onPostCreate시_조회된_인터뷰_상세_정보를_화면에_보여준다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        // 프로젝트 요약 내용
        assertThat(subject.representationImageView.getTag(R.string.tag_key_image_url)).isEqualTo("www.imageUrl.com");
        assertThat(subject.appsDescriptionTextView.getText()).isEqualTo("'네이버웹툰' 앱 유저에게 추천");
        assertThat(subject.projectNameTextView.getText()).isEqualTo("[앱] 릴루미노");
        assertThat(subject.projectIntroduceTextView.getText()).isEqualTo("저시력 장애인들의 눈이 되어주고 싶은 착하고 똑똑한 안경-)");

        // 인터뷰요약 정보
        assertThat(subject.locationTextView.getText()).isEqualTo("우면사업장");
        assertThat(subject.rewardsTextView.getText()).isEqualTo("스벅쿠폰 3만원");
        assertThat(subject.dateTextView.getText()).isEqualTo("03/04 (일)");
        assertThat(subject.dDayTextView.getText()).isEqualTo("D-120");

        // 비디오레이아웃을 보여준다
        Fragment youTubePlayerFragment = subject.getFragmentManager().findFragmentByTag("YouTubePlayerFragment");
        assertThat(youTubePlayerFragment).isNotNull();
        assertThat(youTubePlayerFragment.getArguments().getString(ProjectYoutubePlayerFragment.EXTRA_YOUTUBE_ID)).isEqualTo("o-rnYD47wmo");
        assertThat(subject.findViewById(R.id.project_video_layout).getVisibility()).isEqualTo(View.VISIBLE);

        // 프로젝트 설명
        assertThat(subject.projectDescriptionTextView.getText()).contains("안녕하세요 릴루미노팀입니다.");
        assertThat(subject.descriptionImageRecyclerView.getAdapter().getClass().getSimpleName()).contains(DescriptionImageAdapter.class.getSimpleName());

        // 조회된 인터뷰 소개 정보
        assertThat(subject.interviewIntroduceTextView.getText()).contains("인터뷰소개");

        // 조회된 대표자 정보
        assertThat(subject.ownerPhotoImageView.getTag(R.string.tag_key_image_url)).isEqualTo("www.projectOwnerImage.com");
        assertThat(subject.ownerNameTextView.getText()).isEqualTo("프로젝트 담당자");
        assertThat(subject.ownerIntroduceTextView.getText()).isEqualTo("프로젝트 담당자 소개입니다");
    }

    @Test
    public void onPostCreate시_비디오정보가_없는경우_비디오레이아웃을_숨긴다() throws Exception {
        mockProject.setVideoUrl("");

        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        assertHideVideoLayout();
    }

    @Test
    public void onPostCreate시_비디오정보가_유투브URL이_아닌_경우_비디오레이아웃을_숨긴다() throws Exception {
        mockProject.setVideoUrl("http://www.naver.com/4.mp4");

        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        assertHideVideoLayout();
    }

    private void assertHideVideoLayout() {
        assertThat(subject.getFragmentManager().findFragmentByTag("YouTubePlayerFragment")).isNull();
        assertThat(subject.findViewById(R.id.project_video_layout).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void onPostCreate시_오늘날짜가_마감일이후인경우_D_Day는_0이_표시된다() throws Exception {
        when(mockTimeHelper.getCurrentTime()).thenReturn(1520035200000L);   //2017-12-01 03:03:03

        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        assertThat(subject.dDayTextView.getText()).isEqualTo("D-0");
    }

    @Test
    public void onPostCreate시_조회된_project_설명정보를_화면에_보여준다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        assertThat(subject.projectDescriptionTextView.getText()).contains("안녕하세요 릴루미노팀입니다.");
        assertThat(subject.descriptionImageRecyclerView.getAdapter().getClass().getSimpleName()).contains(DescriptionImageAdapter.class.getSimpleName());
    }

    @Test
    @Config(minSdk = 22)
    public void onPostCreate시_신청하지않은인터뷰가조회되면_인터뷰신청하기형태의_submit버튼이나타난다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        assertThat(subject.submitArrowButton.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(shadowOf(subject.submitArrowButton.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.submit_open);
        assertThat(subject.submitArrowButton.isClickable()).isTrue();
        assertThat(subject.submitButton.getText()).isEqualTo("유저 인터뷰 신청하기");
        assertThat(subject.submitButton.isClickable()).isTrue();
    }

    @Test
    @Config(minSdk = 22)
    public void onPostCreate시_이미신청된인터뷰가조회되면_이미신청한인터뷰안내형태의_submit버튼이나타난다다() throws Exception {
        mockProject.getInterview().setSelectedTimeSlot("time8");

        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        assertThat(subject.submitArrowButton.getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.submitButton.getText()).isEqualTo("이미 신청한 인터뷰입니다.");
        assertThat(subject.submitButton.getCurrentTextColor()).isEqualTo(WARM_GRAY_COLOR);
        assertThat(((ColorDrawable) subject.submitButton.getBackground()).getColor()).isEqualTo(DIM_GRAY_COLOR);
        assertThat(subject.submitButton.isClickable()).isFalse();
    }

    @Test
    public void onPostCreate시_신청이_마감된인터뷰가조회되면_신청이_마감된_인터뷰안내형태의_submit버튼이나타난다() throws Exception {
        mockProject.getInterview().setTimeSlots(new ArrayList<>());

        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        assertThat(subject.submitArrowButton.getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.submitButton.getText()).isEqualTo("신청이 마감된 인터뷰입니다.");
        assertThat(subject.submitButton.getCurrentTextColor()).isEqualTo(WARM_GRAY_COLOR);
        assertThat(((ColorDrawable) subject.submitButton.getBackground()).getColor()).isEqualTo(DIM_GRAY_COLOR);
        assertThat(subject.submitButton.isClickable()).isFalse();
    }

    @Test
    public void onPostCreate시_세부일정선택영역이_나타나지않는다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        assertThat(subject.detailPlansLayout.getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void BackButton클릭시_이전화면으로_복귀한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        subject.findViewById(R.id.back_button).performClick();
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    @Config(minSdk = 22)
    public void 세부일정선택영역이_나타나지_않은_상태에서_submit클릭시_세부일정선택영역을_표시한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        subject.submitButton.performClick();

        assertThat(subject.detailPlansLayout.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.detailPlansTitle.getText()).isEqualTo("[앱] 릴루미노 유저 인터뷰");
        assertThat(subject.detailPlansDescription.getText()).isEqualTo("3월 4일 (일) 우면사업장");
        assertThat(subject.timeSlotRadioGroup.getChildCount()).isEqualTo(3);
        assertThat(subject.timeSlotRadioGroup.getChildAt(0).getId()).isEqualTo(8);
        assertThat(subject.timeSlotRadioGroup.getChildAt(1).getId()).isEqualTo(9);
        assertThat(subject.timeSlotRadioGroup.getChildAt(2).getId()).isEqualTo(10);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNull();

        assertThat(shadowOf(subject.submitArrowButton.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.submit_close);
        assertThat(((ColorDrawable) subject.scrollViewLayout.getForeground()).getColor()).isEqualTo(DIM_FOREGROUND_COLOR);
    }

    @Test
    public void 세부일정선택영역이_나타나지_않은_상태에서_submitArrow버튼클릭시_세부일정선택영역을_표시한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        subject.submitArrowButton.performClick();

        assertThat(subject.detailPlansLayout.getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.detailPlansTitle.getText()).isEqualTo("[앱] 릴루미노 유저 인터뷰");
        assertThat(subject.detailPlansDescription.getText()).isEqualTo("3월 4일 (일) 우면사업장");
        assertThat(subject.timeSlotRadioGroup.getChildCount()).isEqualTo(3);
        assertThat(subject.timeSlotRadioGroup.getChildAt(0).getId()).isEqualTo(8);
        assertThat(subject.timeSlotRadioGroup.getChildAt(1).getId()).isEqualTo(9);
        assertThat(subject.timeSlotRadioGroup.getChildAt(2).getId()).isEqualTo(10);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNull();
    }

    @Test
    public void 세부일정선택영역이_나타난_상태에서_세부일정을_선택하고_submitButton클릭시_인터뷰참여신청API를_호출한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        subject.submitButton.performClick();

        when(mockProjectService.postParticipate(anyString(), anyLong(), anyString())).thenReturn(Completable.complete());
        subject.timeSlotRadioGroup.getChildAt(0).performClick();

        subject.submitButton.performClick();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockProjectService).postParticipate(anyString(), anyLong(), captor.capture());
        assertThat(captor.getValue()).isEqualTo("time8");
    }

    @Test
    public void 세부일정선택영역이_나타난_상태에서_submitArrowButton클릭시_세부일정선택영역이_사라진다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        subject.submitArrowButton.performClick();

        subject.submitArrowButton.performClick();

        assertThat(subject.detailPlansLayout.getVisibility()).isEqualTo(View.GONE);
        assertThat(shadowOf(subject.submitArrowButton.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.submit_open);
    }

    @Test
    public void 세부일정선택영역이_나타난_상태에서_scrollView터치시_세부일정선택영역이_사라진다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        subject.submitArrowButton.performClick();

        subject.scrollView.dispatchTouchEvent(MotionEvent.obtain(100L, 100L, MotionEvent.ACTION_DOWN, 100, 100, 0));

        assertThat(subject.detailPlansLayout.getVisibility()).isEqualTo(View.GONE);
        assertThat(shadowOf(subject.submitArrowButton.getBackground()).getCreatedFromResId()).isEqualTo(R.drawable.submit_open);
    }

    @Test
    public void 세부일정선택영역이_나타난_상태에서_세부일정영역클릭시_세부일정선택영역이_사라지지않는다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        subject.submitButton.performClick();

        subject.detailPlansLayout.performClick();

        assertThat(subject.detailPlansLayout.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    @Config(minSdk = 22)
    public void 세부일정선택영역이_나타나면_scrollView영역이Dim처리된다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        subject.submitArrowButton.performClick();

        assertThat(((ColorDrawable) subject.scrollViewLayout.getForeground()).getColor()).isEqualTo(DIM_FOREGROUND_COLOR);

        subject.submitArrowButton.performClick();

        assertThat(((ColorDrawable) subject.scrollViewLayout.getForeground()).getColor()).isEqualTo(TRANSPARENT_COLOR);
    }

    @Test
    public void 인터뷰참여신청성공시_인터뷰참여완료팝업을_표시한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupForPreparationOfParticipation();

        subject.submitButton.performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        View rootView = shadowOf(dialog).getView();
        assertThat(shadowOf(((ImageView) rootView.findViewById(R.id.dialog_image)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.dialog_success_image);
        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("신청이 완료되었습니다.");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).contains("인터뷰 확정이 되면");
    }

    @Test
    public void 인터뷰참여신청성공시_인터뷰참여완료팝업을_표시후_팝업이닫히면_다가오는_인터뷰페이지로이동한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupForPreparationOfParticipation();

        subject.submitButton.performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        dialog.cancel();

        assertMoveToMyInterviewActivity(dialog);
    }

    @Test
    public void 인터뷰_참여완료_팝업의_확인버튼을클릭시_팝업을_닫고_다가오는_유저인터뷰_페이지로_이동한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupForPreparationOfParticipation();

        subject.submitButton.performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        assertMoveToMyInterviewActivity(dialog);
    }

    @Test
    public void 세부일정중_특정시간을_선택하면_RadioGroup의_체크값이_해당시간으로_설정된다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupForPreparationOfParticipation();

        RadioButton checkedRadioButton = ((RadioButton) subject.timeSlotRadioGroup.getChildAt(0));
        assertThat(subject.timeSlotRadioGroup.getCheckedRadioButtonId()).isEqualTo(checkedRadioButton.getId());
    }

    @Test
    public void 세부일정중_특정시간이_선택된상태에서_다른_시간을_클릭하면_선택한_시간으로_라디오그룹의_체크된_버튼이_변경된다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupForPreparationOfParticipation();
        subject.timeSlotRadioGroup.getChildAt(1).performClick();

        RadioButton checkedRadioButton = ((RadioButton) subject.timeSlotRadioGroup.getChildAt(1));
        assertThat(subject.timeSlotRadioGroup.getCheckedRadioButtonId()).isEqualTo(checkedRadioButton.getId());
    }

    @Test
    public void 세부일정중_특정시간이_선택된상태에서_해당_시간을_다시_클릭하면_RadioGroup의_체크상태를_해제한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupForPreparationOfParticipation();
        subject.timeSlotRadioGroup.getChildAt(0).performClick();

        assertThat(subject.timeSlotRadioGroup.getCheckedRadioButtonId()).isEqualTo(-1);
    }

    private void setupForPreparationOfParticipation() {
        subject.submitButton.performClick();
        subject.timeSlotRadioGroup.getChildAt(0).performClick();
        when(mockProjectService.postParticipate(anyString(), anyLong(), anyString())).thenReturn(Completable.complete());
    }

    private void assertMoveToMyInterviewActivity(AlertDialog dialog) {
        assertThat(shadowOf(dialog).hasBeenDismissed()).isTrue();
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(MyInterviewActivity.class.getName());
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void 이미_신청한인터뷰인_경우_인터뷰참여실패팝업을_표시한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupParticipateError(405);

        subject.submitButton.performClick();

        assertAlertDialog("인터뷰 신청 실패", "이미 신청한 인터뷰입니다.");
    }

    @Test
    public void 인터뷰참여신청실패시_인터뷰참여실패팝업을_표시한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupParticipateError(406);

        subject.submitButton.performClick();

        assertAlertDialog("인터뷰 신청 실패", "인터뷰 신청에 실패하였습니다.");
    }

    @Test
    public void 신청한슬롯을_이미다른사람이_신청하여_인터뷰참여신청실패시_인터뷰참여실패팝업을_표시한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupParticipateError(409);

        subject.submitButton.performClick();

        assertAlertDialog("인터뷰 신청 실패", "선택한 일정의 인터뷰 신청이 마감되었습니다.");
    }

    @Test
    public void 이미마감되거나_신청기한이_아닌_인터뷰신청에_대해_인터뷰참여실패팝업을_표시한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupParticipateError(412);

        subject.submitButton.performClick();

        assertAlertDialog("인터뷰 신청 실패", "선택한 일정의 인터뷰 신청이 마감되었습니다.");
    }

    @Test
    public void HTTP오류가_아닌이유로_인터뷰신청에_실패한경우_인터뷰신청실패팝업을_표시한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        subject.submitButton.performClick();
        subject.timeSlotRadioGroup.getChildAt(0).performClick();
        when(mockProjectService.postParticipate(anyString(), anyLong(), anyString())).thenReturn(Completable.error(new Exception("Unknown")));

        subject.submitButton.performClick();

        assertAlertDialog("인터뷰 신청 실패", "인터뷰 신청에 실패하였습니다.");
    }

    private void assertAlertDialog(String exprectedTitle, String expectedMessage) {
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(alertDialog).isNotNull();
        assertThat(((TextView) shadowOf(alertDialog).getView().findViewById(R.id.dialog_title)).getText()).isEqualTo(exprectedTitle);
        assertThat(((TextView) shadowOf(alertDialog).getView().findViewById(R.id.dialog_message)).getText()).isEqualTo(expectedMessage);
    }

    private void setupParticipateError(int httpErrorCode) {
        subject.submitButton.performClick();
        subject.timeSlotRadioGroup.getChildAt(0).performClick();
        when(mockProjectService.postParticipate(anyString(), anyLong(), anyString())).thenReturn(Completable.error(new HttpException(Response.error(httpErrorCode, ResponseBody.create(null, "")))));
    }

    @Test
    public void 신청한슬롯의인터뷰가마감되어_인터뷰참여실패팝업이_표시된경우_확인버튼을클릭했을때_인터뷰상세조회화면을_리프레시한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

        setupParticipateError(409);

        subject.submitButton.performClick();

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        assertThat(shadowOf(alertDialog).hasBeenDismissed()).isTrue();
        assertThat(subject.isFinishing()).isTrue();

        Intent nextIntent = shadowOf(subject).getNextStartedActivity();
        assertThat(nextIntent.getComponent().getClassName()).isEqualTo(InterviewDetailActivity.class.getName());
        assertThat(nextIntent.getStringExtra(FomesConstants.EXTRA.PROJECT_ID)).isEqualTo("projectId");
        assertThat(nextIntent.getLongExtra(FomesConstants.EXTRA.INTERVIEW_SEQ, 0L)).isEqualTo(1L);
    }

    @Test
    public void 세부일정선택영역이_나타난_상태에서_세부일정을_선택하지않은상태에서_submitButton클릭시_경고팝업을_표시한다() throws Exception {
        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE, intent);

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

    private void mockColorValue() {
        when(mockResourceHelper.getColorValue(R.color.appbee_dim_gray)).thenReturn(DIM_GRAY_COLOR);
        when(mockResourceHelper.getColorValue(R.color.appbee_dim_foreground)).thenReturn(DIM_FOREGROUND_COLOR);
        when(mockResourceHelper.getColorValue(R.color.appbee_warm_gray)).thenReturn(WARM_GRAY_COLOR);
        when(mockResourceHelper.getColorValue(R.color.appbee_gray)).thenReturn(GRAY_COLOR);
        when(mockResourceHelper.getColorValue(R.color.appbee_yellow)).thenReturn(YELLOW_COLOR);
        when(mockResourceHelper.getColorValue(android.R.color.transparent)).thenReturn(TRANSPARENT_COLOR);
    }
}
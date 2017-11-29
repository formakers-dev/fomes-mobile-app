package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.AppBeeConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowToast;

import java.util.Calendar;
import java.util.Date;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CancelInterviewActivityTest extends ActivityTest {

    @Inject
    ProjectService mockProjectService;

    ActivityController<CancelInterviewActivity> activityContorller;
    private CancelInterviewActivity subject;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        // month 1월
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 11, 4);
        Date interviewDate = calendar.getTime();

        Intent intent = new Intent();
        intent.putExtra(AppBeeConstants.EXTRA.PROJECT_ID, "projectId");
        intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_SEQ, 1L);
        intent.putExtra(AppBeeConstants.EXTRA.TIME_SLOT, "time15");
        intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_DATE, interviewDate);
        intent.putExtra(AppBeeConstants.EXTRA.LOCATION, "우면사업장");
        intent.putExtra(AppBeeConstants.EXTRA.PROJECT_NAME, "WHOn");
        intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_STATUS, "확정");

        activityContorller = Robolectric.buildActivity(CancelInterviewActivity.class, intent);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void 취소버튼클릭시_취소요청시_인터뷰취소_API를_호출한다() throws Exception {
        when(mockProjectService.postCancelParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.just(true));
        subject = activityContorller.create().postCreate(null).get();

        subject.findViewById(R.id.cancel_yes).performClick();

        verify(mockProjectService).postCancelParticipate(anyString(), anyLong(), anyString());
    }

    @Test
    public void onPostCreate시_인터뷰관련데이터가_화면에_바인딩된다() throws Exception {
        subject = activityContorller.create().postCreate(null).get();
        assertThat(subject.interviewNameStatusTextView.getText()).isEqualTo("WHOn 유저인터뷰 : 확정");
        assertThat(subject.interviewDateLocationTextView.getText()).isEqualTo("12월 4일 (월) 우면사업장 15:00");
    }

    @Test
    public void 인터뷰취소요청이_성공하면_취소완료_팝업이_호출된다() throws Exception {
        when(mockProjectService.postCancelParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.just(true));
        subject = activityContorller.create().postCreate(null).get();

        subject.findViewById(R.id.cancel_yes).performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        View rootView = shadowOf(dialog).getView();
        assertThat(shadowOf(((ImageView) rootView.findViewById(R.id.dialog_image)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.dialog_cancel_image);
        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("취소 되었습니다.");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).contains("확정 후 취소하셨기 때문에");
    }

    @Test
    public void 인터뷰취소팝업_확인_버튼을_클릭하면_팝업을_닫고_다가오는_유저인터뷰_페이지로_이동한다() throws Exception {
        when(mockProjectService.postCancelParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.just(true));
        subject = activityContorller.create().postCreate(null).get();

        subject.findViewById(R.id.cancel_yes).performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        assertThat(shadowOf(dialog).hasBeenDismissed()).isTrue();
        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(MyInterviewActivity.class.getName());
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void 인터뷰취소요청이_실패하면_오류메시지를_토스트로_보여준다() throws Exception {
        int errorCode = 406;
        when(mockProjectService.postCancelParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.error(new HttpException(Response.error(errorCode, ResponseBody.create(null, "")))));
        subject = activityContorller.create().postCreate(null).get();

        subject.findViewById(R.id.cancel_yes).performClick();

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo(String.valueOf(errorCode));
    }

    @Test
    public void 아니오버튼을_클릭하면_이전화면으로_이동한다() throws Exception {
        subject = activityContorller.create().postCreate(null).get();

        subject.findViewById(R.id.cancel_no).performClick();

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void home클릭시_이전화면으로_이동한다() throws Exception {
        MenuItem homeMenuItem = mock(MenuItem.class);
        when(homeMenuItem.getItemId()).thenReturn(android.R.id.home);

        subject = activityContorller.create().get();
        subject.onOptionsItemSelected(homeMenuItem);

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}
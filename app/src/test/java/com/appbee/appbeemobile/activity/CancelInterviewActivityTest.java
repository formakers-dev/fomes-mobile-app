package com.appbee.appbeemobile.activity;

import android.app.Activity;
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
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

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

    private CancelInterviewActivity subject;
    private Intent intent = new Intent();
    private MenuItem homeMenuItem = mock(MenuItem.class);

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.onIOScheduler(Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(homeMenuItem.getItemId()).thenReturn(android.R.id.home);

        int DECEMBER = 11;
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, DECEMBER, 4);
        Date interviewDate = calendar.getTime();

        intent.putExtra(AppBeeConstants.EXTRA.PROJECT_ID, "projectId");
        intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_SEQ, 1L);
        intent.putExtra(AppBeeConstants.EXTRA.TIME_SLOT, "time15");
        intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_DATE, interviewDate);
        intent.putExtra(AppBeeConstants.EXTRA.LOCATION, "우면사업장");
        intent.putExtra(AppBeeConstants.EXTRA.PROJECT_NAME, "WHOn");
        intent.putExtra(AppBeeConstants.EXTRA.INTERVIEW_STATUS, "확정");

        when(mockProjectService.postCancelParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.just(true));

        subject = Robolectric.buildActivity(CancelInterviewActivity.class, intent).create().postCreate(null).resume().get();
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void 취소버튼클릭시_취소요청시_인터뷰취소_API를_호출한다() throws Exception {
        subject.findViewById(R.id.cancel_yes).performClick();

        verify(mockProjectService).postCancelParticipate(anyString(), anyLong(), anyString());
    }

    @Test
    public void onPostCreate시_인터뷰관련데이터가_화면에_바인딩된다() throws Exception {
        assertThat(subject.interviewNameStatusTextView.getText()).isEqualTo("WHOn 유저인터뷰 : 확정");
        assertThat(subject.interviewDateLocationTextView.getText()).isEqualTo("12월 4일 (월) 우면사업장 15:00");
    }

    @Test
    public void 인터뷰취소요청이_성공하면_취소완료_팝업이_호출된다() throws Exception {
        subject.findViewById(R.id.cancel_yes).performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        View rootView = shadowOf(dialog).getView();
        assertThat(shadowOf(((ImageView) rootView.findViewById(R.id.dialog_image)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.dialog_cancel_image);
        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("취소 되었습니다.");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).contains("확정 후 취소하셨기 때문에");
    }

    @Test
    public void 인터뷰취소요청이_성공하면_취소완료_팝업이_호출된상태에서_팝업이_닫히면_이전페이지로_이동한다() throws Exception {
        subject.findViewById(R.id.cancel_yes).performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();

        dialog.cancel();

        assertThat(shadowOf(dialog).hasBeenDismissed()).isTrue();
        assertThat(shadowOf(subject).getResultCode()).isEqualTo(Activity.RESULT_OK);
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void 인터뷰취소팝업_확인_버튼을_클릭하면_팝업을_닫고_이전페이지로_복귀한다() throws Exception {
        subject.findViewById(R.id.cancel_yes).performClick();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        assertThat(shadowOf(dialog).hasBeenDismissed()).isTrue();
        assertThat(shadowOf(subject).getResultCode()).isEqualTo(Activity.RESULT_OK);
        assertThat(subject.isFinishing()).isTrue();
    }

    @Test
    public void 인터뷰취소신청시_취소기한이_지난경우_팝업메시지를_표시한다() throws Exception {
        setupCancelHttpError(412);

        subject.findViewById(R.id.cancel_yes).performClick();

        assertAlertDialogAndFinishActivity("인터뷰 취소 실패", "취소 기한이 지난 인터뷰입니다.");
    }

    @Test
    public void 인터뷰취소요청이_존재하지않는_시간대취소요청으로_실패하면_팝업메시지를_표시한다() throws Exception {
        setupCancelHttpError(416);

        subject.findViewById(R.id.cancel_yes).performClick();

        assertAlertDialogAndFinishActivity("인터뷰 취소 실패", "인터뷰 취소에 실패하였습니다.");
    }

    @Test
    public void 인터뷰취소신청시_다른사람신청건에_대한_취소요청인_경우_팝업메시지를_표시한다() throws Exception {
        setupCancelHttpError(406);

        subject.findViewById(R.id.cancel_yes).performClick();

        assertAlertDialogAndFinishActivity("인터뷰 취소 실패", "인터뷰 취소에 실패하였습니다.");
    }

    @Test
    public void 인터뷰취소요청이_알수없는오류로_실패한경우_팝업메시지를_표시한다() throws Exception {
        setupCancelHttpError(500);

        subject.findViewById(R.id.cancel_yes).performClick();

        assertAlertDialogAndFinishActivity("인터뷰 취소 실패", "인터뷰 취소에 실패하였습니다.");
    }

    @Test
    public void 인터뷰취소요청이_HTTP오류가_아닌_이유로_실패한경우_팝업메시지를_표시한다() throws Exception {
        when(mockProjectService.postCancelParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.error(new Exception("Unknown Error")));
        subject = Robolectric.buildActivity(CancelInterviewActivity.class, intent).create().postCreate(null).resume().get();

        subject.findViewById(R.id.cancel_yes).performClick();

        assertAlertDialogAndFinishActivity("인터뷰 취소 실패", "인터뷰 취소에 실패하였습니다.");
    }

    private void assertAlertDialogAndFinishActivity(String title, String message) {
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(alertDialog).isNotNull();
        assertThat(((TextView) shadowOf(alertDialog).getView().findViewById(R.id.dialog_title)).getText()).isEqualTo(title);
        assertThat(((TextView) shadowOf(alertDialog).getView().findViewById(R.id.dialog_message)).getText()).isEqualTo(message);

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

        assertThat(shadowOf(alertDialog).hasBeenDismissed()).isTrue();
        assertThat(shadowOf(subject).getResultCode()).isEqualTo(Activity.RESULT_OK);
        assertThat(subject.isFinishing()).isTrue();
    }

    private void setupCancelHttpError(int errorCode) {
        when(mockProjectService.postCancelParticipate(anyString(), anyLong(), anyString())).thenReturn(Observable.error(new HttpException(Response.error(errorCode, ResponseBody.create(null, "")))));
        subject = Robolectric.buildActivity(CancelInterviewActivity.class, intent).create().postCreate(null).resume().get();
    }

    @Test
    public void 아니오버튼을_클릭하면_이전화면으로_이동한다() throws Exception {
        subject.findViewById(R.id.cancel_no).performClick();

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void home클릭시_이전화면으로_이동한다() throws Exception {
        subject.onOptionsItemSelected(homeMenuItem);

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}
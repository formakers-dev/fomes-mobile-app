package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.receiver.PowerConnectedReceiver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StartActivityTest extends ActivityTest {

    private StartActivity subject;
    private Unbinder binder;

    @BindView(R.id.start_analysis_button)
    Button startButton;

    @Inject
    AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        subject = Robolectric.setupActivity(StartActivity.class);
        binder = ButterKnife.bind(this, subject);
    }

    @After
    public void tearDown() throws Exception {
        binder.unbind();
    }

    @Test
    public void onCreate호출시_startButton이_보여진다() throws Exception {
        assertThat(startButton.isShown()).isTrue();
    }

    @Test
    public void startButton클릭시_권한이없는경우_권한확인다이얼로그를_호출한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        startButton.performClick();

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        ShadowAlertDialog shadowAlertDialog = shadowOf(alertDialog);
        assertThat(alertDialog.isShowing()).isTrue();
        assertThat(shadowAlertDialog.getTitle()).isEqualTo("앱 사용 정보 접근 권한 허용");
        assertThat(shadowAlertDialog.getMessage()).contains("허용 하시겠습니까?");
        assertThat(alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).getText()).isEqualTo("동의");
        assertThat(alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).getText()).isEqualTo("취소");
    }

    @Test
    public void startButton클릭시_권한이있는경우_MainActivity로_이동한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);

        startButton.performClick();

        assertMoveToMainActivity();
    }

    @Test
    public void 권한허용요청다이얼로그에서_동의버튼클릭시_MainActivity로_이동한다() throws Exception {
        startButton.performClick();

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        assertMoveToMainActivity();
    }

    @Test
    public void 권한허용요청다이얼로그에서_취소버튼클릭시_다이얼로그를_닫는다() throws Exception {
        startButton.performClick();

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();

        assertThat(alertDialog.isShowing()).isFalse();
    }

    private void assertMoveToMainActivity() {
        Intent intent = Shadows.shadowOf(subject).peekNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(MainActivity.class.getCanonicalName());
    }
}
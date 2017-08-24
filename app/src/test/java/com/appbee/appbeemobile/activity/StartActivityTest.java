package com.appbee.appbeemobile.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StartActivityTest {

    private StartActivity subject;
    private Unbinder binder;

    @BindView(R.id.start_analysis_button)
    Button startButton;

    @Before
    public void setUp() throws Exception {
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
    public void startButton클릭시_권한확인다이얼로그를_호출한다() throws Exception {
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
    public void 권한허용요청다이얼로그에서_동의버튼클릭시_MainActivity로_이동한다() throws Exception {
        startButton.performClick();

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        Intent intent = Shadows.shadowOf(subject).peekNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(MainActivity.class.getCanonicalName());
    }

    @Test
    public void 권한허용요청다이얼로그에서_취소버튼클릭시_다이얼로그를_닫는다() throws Exception {
        startButton.performClick();

        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();

        assertThat(alertDialog.isShowing()).isFalse();
    }
}
package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.widget.Button;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StartActivityTest {

    private StartActivity subject;

    @BindView(R.id.start_analysis_button)
    Button startButton;

    @Before
    public void setUp() throws Exception {
        subject = Robolectric.setupActivity(StartActivity.class);
        ButterKnife.bind(this, subject);
    }

    @Test
    public void onCreate호출시_startButton이_보여진다() throws Exception {
        assertThat(startButton.isShown()).isTrue();
    }

    @Test
    public void startButton클릭시_MainActivity로_이동한다() throws Exception {
        startButton.performClick();
        Intent intent = Shadows.shadowOf(subject).peekNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(MainActivity.class.getCanonicalName());
    }
}
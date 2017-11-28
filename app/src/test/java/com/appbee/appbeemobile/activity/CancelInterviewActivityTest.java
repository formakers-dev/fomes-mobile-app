package com.appbee.appbeemobile.activity;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CancelInterviewActivityTest extends ActivityTest {
    CancelInterviewActivity subject;

    @Before
    public void setUp() throws Exception {
        subject = Robolectric.setupActivity(CancelInterviewActivity.class);
    }

    @Test
    public void 뒤로가가_버튼클릭시_현재페이지를_종료한다() throws Exception {
        subject.findViewById(R.id.back_button).performClick();

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}
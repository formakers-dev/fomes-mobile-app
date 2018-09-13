package com.formakers.fomes.main.view;

import android.view.View;
import android.view.ViewGroup;

import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.common.view.FomesBaseActivityTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MainActivityTest extends FomesBaseActivityTest<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void launchActivity() {
        subject = getActivity();
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        super.setUp();
    }

    @Test
    public void MainActivity_시작시_메인화면이_나타난다() {
        launchActivity();

        assertThat(((ViewGroup) subject.findViewById(R.id.main_side_bar_layout))).isNotNull();
        assertThat(subject.findViewById(R.id.main_content_layout).getVisibility()).isEqualTo(View.VISIBLE);
    }
}
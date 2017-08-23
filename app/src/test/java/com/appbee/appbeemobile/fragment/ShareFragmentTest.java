package com.appbee.appbeemobile.fragment;

import android.widget.Button;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.ShareSnsHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.FragmentController;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ShareFragmentTest {

    ShareFragment subject;

    @BindView(R.id.share_button)
    Button sharedButton;

    @Inject
    ShareSnsHelper mockShareSnsHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        subject = new ShareFragment();
        FragmentController.of(subject).create();
        ButterKnife.bind(this, subject.getView());
    }

    @Test
    public void 공유버튼_클릭시_메세지가_SNS로_공유된다() throws Exception {
        sharedButton.performClick();
        verify(mockShareSnsHelper).shareKakao();
    }
}
package com.formakers.fomes.provisioning.view;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.formakers.fomes.R;
import com.formakers.fomes.activity.BaseActivityTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowLooper;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProvisioningActivityTest extends BaseActivityTest<ProvisioningActivity> {

    private ProvisioningActivity subject;

    public ProvisioningActivityTest() {
        super(ProvisioningActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = getActivity();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void ProvisioningActivity_시작시_프로비저닝화면이_나타난다() {
        assertThat(subject.findViewById(R.id.provision_icon_imageview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.provision_viewpager).getVisibility()).isEqualTo(View.VISIBLE);

        List<Fragment> fragmentList = ((ProvisioningActivity.ProvisioningPagerAdapter) ((ViewPager) subject.findViewById(R.id.provision_viewpager)).getAdapter()).getFragmentList();
        assertThat(fragmentList.size()).isEqualTo(3);
        assertThat(fragmentList.get(0).getClass()).isEqualTo(ProvisioningUserInfoFragment.class);
        assertThat(fragmentList.get(1).getClass()).isEqualTo(ProvisioningLifeGameFragment.class);
        assertThat(fragmentList.get(2).getClass()).isEqualTo(ProvisioningNickNameFragment.class);
    }

    // TODO : 수정 필요. Tick 문제로 보임. Roboletrics에서 nextTick 처리 필요.
    @Ignore
    @Test
    public void nextPage_호출시_프래그먼트를_다음으로_이동시킨다() {
        int currentPageIndex = subject.viewPager.getCurrentItem();

        subject.nextPage();

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(subject.viewPager.getCurrentItem()).isEqualTo(currentPageIndex + 1);
    }
}
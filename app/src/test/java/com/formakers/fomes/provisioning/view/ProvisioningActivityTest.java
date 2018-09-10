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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProvisioningActivityTest extends BaseActivityTest<ProvisioningActivity> {

    private ProvisioningActivity subject;

    public ProvisioningActivityTest() {
        super(ProvisioningActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void ProvisioningActivity_시작시__프로비저닝화면이_나타난다() {
        subject = getActivity();

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
    public void nextPage_호출시__프래그먼트를_다음으로_이동시킨다() {
        subject = getActivity();
        int currentPageIndex = subject.viewPager.getCurrentItem();

        subject.nextPage();

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(subject.viewPager.getCurrentItem()).isEqualTo(currentPageIndex + 1);
    }

    @Test
    public void 다음버튼_클릭시__현재_프래그먼트에게_동작을_위임한다() {
        ProvisioingTestFragment mockFragment = mock(ProvisioingTestFragment.class);
        ProvisioningActivity.ProvisioningPagerAdapter mockPagerAdapter = mock(ProvisioningActivity.ProvisioningPagerAdapter.class);
        when(mockPagerAdapter.getItem(anyInt())).thenReturn(mockFragment);

        ViewPager mockViewPager = mock(ViewPager.class);
        when(mockViewPager.getAdapter()).thenReturn(mockPagerAdapter);
        when(mockViewPager.getCurrentItem()).thenReturn(0);

        subject = getActivity();
        subject.viewPager = mockViewPager;
        subject.onNextButtonClick();

        verify(mockFragment).onNextButtonClick();
    }

    class ProvisioingTestFragment extends Fragment implements ProvisioningActivity.FragmentCommunicator {
        @Override
        public void onNextButtonClick() { }
    }
}
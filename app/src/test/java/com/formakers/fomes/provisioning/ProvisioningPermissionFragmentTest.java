package com.formakers.fomes.provisioning;


import android.content.Intent;
import android.provider.Settings;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import com.formakers.fomes.R;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static com.formakers.fomes.provisioning.ProvisioningPermissionFragment.REQUEST_CODE_USAGE_STATS_PERMISSION;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class ProvisioningPermissionFragmentTest {

    @Mock
    ProvisioningContract.Presenter mockPresenter;

    ProvisioningPermissionFragment subject;
    FragmentScenario<ProvisioningPermissionFragment> scenario;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockPresenter.getAnalytics()).thenReturn(mock(AnalyticsModule.Analytics.class));

        scenario = FragmentScenario.launchInContainer(ProvisioningPermissionFragment.class);
        scenario.onFragment(fragment -> {
            subject = fragment;
            fragment.setPresenter(mockPresenter);
        });

        scenario.moveToState(Lifecycle.State.CREATED)
                .moveToState(Lifecycle.State.STARTED)
                .moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void ProvisioningPermissionFragment_시작시__프로비저닝_데모그래픽정보입력_화면이_나타난다() {
        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.provision_permission_imageview).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void ProvisioningPermissionFragment_가_보여질시__프로비저닝_진행상태를_설정하고_권한_설정_여부를_체크한다() {
        subject.onSelectedPage();

        verify(mockPresenter).setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.PERMISSION);
        verify(mockPresenter).checkGrantedOnPermissionFragment();
    }

    @Test
    public void 다음으로_버튼_클릭이벤트_수신시__권한_설정_화면으로_이동한다() {
        subject.onNextButtonClick();

        Intent intent = shadowOf(subject.getActivity()).getNextStartedActivity();
        assertThat(intent.getAction()).isEqualTo(Settings.ACTION_USAGE_ACCESS_SETTINGS);
    }

    @Test
    public void 권한_설정_액티비티_종료시__권한_설정여부를_체크한다() {
        subject.onActivityResult(REQUEST_CODE_USAGE_STATS_PERMISSION, -1, null);

        verify(mockPresenter).checkGrantedOnPermissionFragment();
    }

    @Test
    public void 권한_설정_액티비티_종료시__권한설정_되지않았을경우__아무것도_안한다() {
        when(mockPresenter.hasUsageStatsPermission()).thenReturn(false);

        subject.onActivityResult(REQUEST_CODE_USAGE_STATS_PERMISSION, -1, null);

        Intent nextStartedActivity = shadowOf(subject.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity).isNull();
    }
}
package com.formakers.fomes.provisioning.view;


import android.content.Intent;
import android.provider.Settings;
import android.view.View;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.view.RecentAnalysisReportActivity;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import static com.formakers.fomes.provisioning.view.ProvisioningPermissionFragment.REQUEST_CODE_USAGE_STATS_PERMISSION;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProvisioningPermissionFragmentTest {

    @Mock
    ProvisioningContract.Presenter mockPresenter;

    ProvisioningPermissionFragment subject;
    SupportFragmentController<ProvisioningPermissionFragment> controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockPresenter.getAnalytics()).thenReturn(mock(AnalyticsModule.Analytics.class));

        subject = new ProvisioningPermissionFragment();
        subject.setPresenter(mockPresenter);
        controller = SupportFragmentController.of(subject);
        controller.create().start().resume().visible();
    }

    @Test
    public void ProvisioningPermissionFragment_시작시__프로비저닝_데모그래픽정보입력_화면이_나타난다() {
        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.provision_permission_imageview).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void ProvisioningPermissionFragment_가_보여질시__권한이_없으면__권한허용이_필요하다는_이벤트를_보낸다() {
        when(mockPresenter.hasUsageStatsPermission()).thenReturn(false);

        subject.onSelectedPage();

        verify(mockPresenter).setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.PERMISSION);
        verify(mockPresenter).emitNeedToGrantEvent();
    }

    @Test
    public void ProvisioningPermissionFragment_가_보여질시__권한이_있으면__상태를_업데이트하고_다음화면으로_이동한다() {
        when(mockPresenter.hasUsageStatsPermission()).thenReturn(true);
        when(mockPresenter.isProvisiongProgress()).thenReturn(true);

        subject.onSelectedPage();

        verify(mockPresenter).setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);
        verify(mockPresenter).emitStartActivityAndFinishEvent(eq(RecentAnalysisReportActivity.class));
    }

    @Test
    public void ProvisioningPermissionFragment_가_보여질시__권한이_있고_프로비저닝상태가_아닐경우__종료한다() {
        when(mockPresenter.hasUsageStatsPermission()).thenReturn(true);
        when(mockPresenter.isProvisiongProgress()).thenReturn(false);

        subject.onSelectedPage();

        assertThat(subject.getActivity().isFinishing()).isTrue();
    }

    @Test
    public void 다음으로_버튼_클릭이벤트_수신시__권한_설정_화면으로_이동한다() {
        subject.onNextButtonClick();

        Intent intent = shadowOf(subject.getActivity()).getNextStartedActivity();
        assertThat(intent.getAction()).isEqualTo(Settings.ACTION_USAGE_ACCESS_SETTINGS);
    }

    @Test
    public void 권한_설정_액티비티_종료시__권한설정_되었을경우__상태를_업데이트하고_다음화면으로_이동한다() {
        when(mockPresenter.hasUsageStatsPermission()).thenReturn(true);
        when(mockPresenter.isProvisiongProgress()).thenReturn(true);

        subject.onActivityResult(REQUEST_CODE_USAGE_STATS_PERMISSION, -1, null);

        verify(mockPresenter).setProvisioningProgressStatus(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);
        verify(mockPresenter).emitStartActivityAndFinishEvent(eq(RecentAnalysisReportActivity.class));
    }

    @Test
    public void 권한_설정_액티비티_종료시__권한설정_되었고_프로비저닝상태가_아닐경우__종료한다() {
        when(mockPresenter.hasUsageStatsPermission()).thenReturn(true);
        when(mockPresenter.isProvisiongProgress()).thenReturn(false);

        subject.onActivityResult(REQUEST_CODE_USAGE_STATS_PERMISSION, -1, null);

        assertThat(subject.getActivity().isFinishing()).isTrue();
    }

    @Test
    public void 권한_설정_액티비티_종료시__권한설정_되지않았을경우__아무것도_안한다() {
        when(mockPresenter.hasUsageStatsPermission()).thenReturn(false);

        subject.onActivityResult(REQUEST_CODE_USAGE_STATS_PERMISSION, -1, null);

        Intent nextStartedActivity = shadowOf(subject.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity).isNull();
    }
}
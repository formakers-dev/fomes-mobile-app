package com.formakers.fomes.common.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.R;
import com.formakers.fomes.common.network.ConfigService;
import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.formakers.fomes.provisioning.view.ProvisioningPermissionFragment;
import com.formakers.fomes.provisioning.view.ProvisioningUserInfoFragment;
import com.formakers.fomes.util.FomesConstants;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowAlertDialog;

import javax.inject.Inject;

import rx.Single;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public abstract class FomesBaseActivityTest<T extends FomesBaseActivity> extends BaseActivityTest<T> {

    @Inject SharedPreferencesHelper mockSharedPreferencesHelper;
    @Inject AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;
    @Inject ConfigService configService;

    public FomesBaseActivityTest(Class<T> clazz) {
        super(clazz);
    }

    /*
     * You must call this under inject by dagger in setUp() of child class.
     * examples)
     * in setUp() of child class:
     * ```java
     *   DaggerComponent.inject(childClass);
     *   super.setUp()
     * ```
     */
    @Before
    public void setUp() throws Exception {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);

        when(configService.getAppVersion()).thenReturn(Single.just(Long.MIN_VALUE));
    }

    /* base test for each activity */
    @Test
    public void 액티비티_진입시__프로비저닝_상태가_미로그인_상태면__로그인_화면으로_진입한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        subject = getActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(LoginActivity.class.getName());
    }

    @Test
    public void 액티비티_진입시__프로비저닝_상태가_인트로_상태면__프로비저닝_시작화면으로_진입한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.INTRO);

        subject = getActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity.getStringExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME))
                .isEqualTo(ProvisioningUserInfoFragment.TAG);
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(ProvisioningActivity.class.getName());
    }

    @Test
    public void 액티비티_진입시__프로비저닝_상태가_권한미허용_상태면__프로비저닝_권한화면으로_진입한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.PERMISSION);

        subject = getActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity.getStringExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME))
                .isEqualTo(ProvisioningPermissionFragment.TAG);
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(ProvisioningActivity.class.getName());
    }

    @Test
    public void 액티비티_진입시__프로비저닝_상태가_완료_상태면__현재_화면으로_진입한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        subject = getActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity).isNull();
    }

    @Test
    public void 액티비티_진입시__권한이_없으면__권한허용화면으로_진입한다() {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        subject = getActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity.getStringExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME))
                .isEqualTo(ProvisioningPermissionFragment.TAG);
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(ProvisioningActivity.class.getName());
    }

    @Test
    public void 액티비티_진입시__권한이_있으면__현재화면으로_진입한다() {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);

        subject = getActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity).isNull();
    }

    @Test
    public void 액티비티_진입시__크리티컬_업데이트_버전을_체크한다() {
        when(configService.getAppVersion()).thenReturn(Single.just(3L));

        subject = getActivity();

        verify(configService).getAppVersion();
    }

    @Test
    public void 액티비티_진입시__현재버전이_크리티컬_업데이트_버전보다_낮으면__업데이트_팝업을_띄운다() {
        when(configService.getAppVersion()).thenReturn(Single.just(Long.MAX_VALUE));

        subject = getActivity();

        verify(configService).getAppVersion();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        View rootView = shadowAlertDialog.getView();
        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("신규 버전 업데이트");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).isEqualTo("Fomes의 신규 버전이 업데이트 되었습니다.\n마켓에서 업데이트 진행 후 이용 부탁드립니다.");
    }
}
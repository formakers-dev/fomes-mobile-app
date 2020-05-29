package com.formakers.fomes.common.view;

import android.content.Intent;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.provisioning.login.LoginActivity;
import com.formakers.fomes.provisioning.ProvisioningActivity;
import com.formakers.fomes.provisioning.ProvisioningNickNameFragment;
import com.formakers.fomes.provisioning.ProvisioningPermissionFragment;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public abstract class FomesBaseActivityTest<T extends FomesBaseActivity> extends BaseActivityTest<T> {

    @Inject SharedPreferencesHelper mockSharedPreferencesHelper;
    @Inject
    AndroidNativeHelper mockAndroidNativeHelper;

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
        super.setUp();

        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        when(mockAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
    }

    /* base test for each activity */
    @Test
    public void 액티비티_진입시__프로비저닝_상태가_미로그인_상태면__로그인_화면으로_진입한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.LOGIN);

        launchActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(LoginActivity.class.getName());
    }

    @Test
    public void 액티비티_진입시__프로비저닝_상태가_인트로_상태면__프로비저닝_시작화면으로_진입한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.INTRO);

        launchActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity.getStringExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME))
                .isEqualTo(ProvisioningNickNameFragment.TAG);
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(ProvisioningActivity.class.getName());
    }

    @Test
    public void 액티비티_진입시__프로비저닝_상태가_권한미허용_상태면__프로비저닝_권한화면으로_진입한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.PERMISSION);

        launchActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity.getStringExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME))
                .isEqualTo(ProvisioningPermissionFragment.TAG);
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(ProvisioningActivity.class.getName());
    }

    @Test
    public void 액티비티_진입시__프로비저닝_상태가_완료_상태면__현재_화면으로_진입한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.COMPLETED);

        launchActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity).isNull();
    }

    @Test
    public void 액티비티_진입시__권한이_없으면__권한허용화면으로_진입한다() {
        when(mockAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        launchActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity.getStringExtra(FomesConstants.EXTRA.START_FRAGMENT_NAME))
                .isEqualTo(ProvisioningPermissionFragment.TAG);
        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(ProvisioningActivity.class.getName());
    }

    @Test
    public void 액티비티_진입시__권한이_있으면__현재화면으로_진입한다() {
        when(mockAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);

        launchActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity).isNull();
    }

//    @Test
//    public void 액티비티_진입시__마이그레이션_버전이_바뀐_상태면__마이그레이션_화면으로_진입한다() {
//        if (this instanceof RecentAnalysisReportActivityTest) {
//            return;
//        }
//
//        when(mockSharedPreferencesHelper.getOldLatestMigrationVersion()).thenReturn(0);
//
//        launchActivity();
//
//        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
//        assertThat(nextStartedActivity.getComponent().getClassName()).isEqualTo(NoticeMigrationActivity.class.getName());
//    }

    @Test
    public void 액티비티_진입시__마이그레이션_버전이_바뀐_상태가_아니면__현재화면으로_진입한다() {
        launchActivity();

        Intent nextStartedActivity = shadowOf(subject).getNextStartedActivity();
        assertThat(nextStartedActivity).isNull();
    }
}
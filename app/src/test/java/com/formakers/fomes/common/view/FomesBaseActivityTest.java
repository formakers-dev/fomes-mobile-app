package com.formakers.fomes.common.view;

import android.content.Intent;

import com.formakers.fomes.activity.BaseActivityTest;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.provisioning.view.LoginActivity;
import com.formakers.fomes.provisioning.view.ProvisioningActivity;
import com.formakers.fomes.provisioning.view.ProvisioningPermissionFragment;
import com.formakers.fomes.provisioning.view.ProvisioningUserInfoFragment;
import com.formakers.fomes.util.FomesConstants;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public abstract class FomesBaseActivityTest<T extends FomesBaseActivity> extends BaseActivityTest<T> {

    @Inject SharedPreferencesHelper mockSharedPreferencesHelper;

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
    }

    /* base test for each activity */
    @Test
    public void 액티비티_진입시__프로비저닝_상태가_미로그인_상태면__로그인_화면으로_진입한다() {
        when(mockSharedPreferencesHelper.getProvisioningProgressStatus())
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.NOT_LOGIN);

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
                .thenReturn(FomesConstants.PROVISIONING.PROGRESS_STATUS.NO_PERMISSION);

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
}
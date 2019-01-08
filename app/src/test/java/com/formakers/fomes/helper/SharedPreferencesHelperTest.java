package com.formakers.fomes.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.formakers.fomes.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SharedPreferencesHelperTest {
    private SharedPreferencesHelper subject;
    private SharedPreferences sf;

    @Before
    public void setUp() throws Exception {
        subject = new SharedPreferencesHelper(RuntimeEnvironment.application);

        sf = RuntimeEnvironment.application.getSharedPreferences("FOMES_SHARED_PREFERENCES", Context.MODE_PRIVATE);
        sf.edit()
                .putString("ACCESS_TOKEN", "TEST_STRING_VALUE")
                .putLong("LAST_USAGE_TIME", 1234567890L)
                .putString("USER_ID", "user_id")
                .putString("REGISTRATION_TOKEN", "TEST_REGISTRATION_TOKEN")
                .putString("EMAIL", "appbee0627@gmail.com")
                .putInt("BIRTHDAY", 19991231)
                .putString("GENDER", "female")
                .putLong("LAST_UPDATE_STAT_TIMESTAMP", 1000L)
                .putLong("LAST_UPDATE_SHORT_TERM_STAT_TIMESTAMP", 1000L)
                .putInt("PROVISIONING_PROGRESS_STATUS", 0)
                .apply();
    }

    @Test
    public void setAccessToken호출시_SharedPreference에_값을_저장한다() throws Exception {
        subject.setAccessToken("testToken");
        assertThat(sf.getString("ACCESS_TOKEN", "")).isEqualTo("testToken");
    }

    @Test
    public void getAccessToken호출시_SharedPreference에_저장된_값을_리턴한다() throws Exception {
        assertThat(subject.getAccessToken()).isEqualTo("TEST_STRING_VALUE");
    }

    @Test
    public void setRegistrationToken호출시_SharedPreference에_fcm_등록토큰값을_저장한다() throws Exception {
        subject.setRegistrationToken("test_registration_token");
        assertThat(sf.getString("REGISTRATION_TOKEN", "")).isEqualTo("test_registration_token");
    }

    @Test
    public void getRegistrationToken호출시_SharedPreference에_저장된_값을_리턴한다() throws Exception {
        assertThat(subject.getRegistrationToken()).isEqualTo("TEST_REGISTRATION_TOKEN");
    }

    @Test
    public void setLastUpdateAppUsageTimestamp호출시_SharedPreference에_최종업데이트시간을_저장한다() throws Exception {
        subject.setLastUpdateAppUsageTimestamp(1000L);
        assertThat(sf.getLong("LAST_UPDATE_STAT_TIMESTAMP", 0)).isEqualTo(1000L);
    }

    @Test
    public void getLastUpdateAppUsageTimestamp호출시_SharedPreference에_저장된_최종업데이트시간을_리턴한다() throws Exception {
        assertThat(subject.getLastUpdateAppUsageTimestamp()).isEqualTo(1000L);
    }

    @Test
    public void setLastUpdateShortTermStatTimestamp호출시_SharedPreference에_최종업데이트시간을_저장한다() throws Exception {
        subject.setLastUpdateShortTermStatTimestamp(1000L);
        assertThat(sf.getLong("LAST_UPDATE_SHORT_TERM_STAT_TIMESTAMP", 0)).isEqualTo(1000L);
    }

    @Test
    public void getLastUpdateShortTermStatTimestamp호출시_SharedPreference에_저장된_최종업데이트시간을_리턴한다() throws Exception {
        assertThat(subject.getLastUpdateShortTermStatTimestamp()).isEqualTo(1000L);
    }

    @Test
    public void getProvisioningProgressStatus호출시__프로비저닝_진행상태를_리턴한다() throws Exception {
        assertThat(subject.getProvisioningProgressStatus()).isEqualTo(0);
    }

    @Test
    public void setProvisioningProgressStatus호출시__프로비저닝_진행상태를_저장한다() throws Exception {
        subject.setProvisioningProgressStatus(1);
        assertThat(sf.getInt("PROVISIONING_PROGRESS_STATUS", 0)).isEqualTo(1);

        subject.setProvisioningProgressStatus(0);
        assertThat(sf.getInt("PROVISIONING_PROGRESS_STATUS", 0)).isEqualTo(1);

        subject.setProvisioningProgressStatus(2);
        assertThat(sf.getInt("PROVISIONING_PROGRESS_STATUS", 0)).isEqualTo(2);

        subject.setProvisioningProgressStatus(-1);
        assertThat(sf.getInt("PROVISIONING_PROGRESS_STATUS", 0)).isEqualTo(-1);
    }
}
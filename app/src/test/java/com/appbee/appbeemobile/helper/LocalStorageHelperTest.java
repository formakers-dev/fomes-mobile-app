package com.appbee.appbeemobile.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.appbee.appbeemobile.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LocalStorageHelperTest {
    private LocalStorageHelper subject;
    private SharedPreferences sf;

    @Before
    public void setUp() throws Exception {
        subject = new LocalStorageHelper(RuntimeEnvironment.application);

        sf = RuntimeEnvironment.application.getSharedPreferences("APP_BEE_SHARED_PREFERENCES", Context.MODE_PRIVATE);
        sf.edit()
                .putString("ACCESS_TOKEN", "TEST_STRING_VALUE")
                .putLong("LAST_USAGE_TIME", 1234567890L)
                .putString("USER_ID", "user_id")
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
    public void setLastUsageTime호출시_SharedPreference에_값을_저장한다() throws Exception {
        subject.setLastUsageTime(987654321L);
        assertThat(sf.getLong("LAST_USAGE_TIME", 0L)).isEqualTo(987654321L);
    }

    @Test
    public void getLastUsageTime호출시_SharedPreference에_저장된_값을_리턴한다() throws Exception {
        assertThat(subject.getLastUsageTime()).isEqualTo(1234567890L);
    }

    @Test
    public void setUserId호출시_SharedPreference에_값을_저장한다() throws Exception {
        subject.setUserId("test_user_id");
        assertThat(sf.getString("USER_ID", "")).isEqualTo("test_user_id");
    }

    @Test
    public void getUserId호출시_SharedPreference에_저장된_값을_리턴한다() throws Exception {
        assertThat(subject.getUserId()).isEqualTo("user_id");
    }
}
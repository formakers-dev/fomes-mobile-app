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
                .putString("REGISTRATION_TOKEN", "TEST_REGISTRATION_TOKEN")
                .putString("EMAIL", "appbee0627@gmail.com")
                .putInt("BIRTHDAY", 19991231)
                .putString("GENDER", "female")
                .putLong("LAST_UPDATE_STAT_TIMESTAMP", 1000L)
                .putString("INVITATION_CODE", "CODE")
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
    public void setUserId호출시_SharedPreference에_값을_저장한다() throws Exception {
        subject.setUserId("test_user_id");
        assertThat(sf.getString("USER_ID", "")).isEqualTo("test_user_id");
    }

    @Test
    public void getUserId호출시_SharedPreference에_저장된_값을_리턴한다() throws Exception {
        assertThat(subject.getUserId()).isEqualTo("user_id");
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
    public void getEmail호출시_SharedPreference에_저장된_값을_리턴한다() throws Exception {
        assertThat(subject.getEmail()).isEqualTo("appbee0627@gmail.com");
    }

    @Test
    public void setEmail호출시_SharedPreference에_이메일주소를_저장한다() throws Exception {
        subject.setEmail("appbee0627@gmail.com");
        assertThat(sf.getString("EMAIL", "")).isEqualTo("appbee0627@gmail.com");
    }

    @Test
    public void setBirthday호출시_SharedPreference에_생년월일을_저장한다() throws Exception {
        subject.setBirthday(20000101);
        assertThat(sf.getInt("BIRTHDAY", 0)).isEqualTo(20000101);
    }

    @Test
    public void getBirthday호출시_SharedPreference에_저장된_생년월일을_리턴한다() throws Exception {
        assertThat(subject.getBirthday()).isEqualTo(19991231);
    }

    @Test
    public void setGender호출시_SharedPreference에_성별을_저장한다() throws Exception {
        subject.setGender("male");
        assertThat(sf.getString("GENDER", "")).isEqualTo("male");
    }

    @Test
    public void getGender호출시_SharedPreference에_저장된_성별을_리턴한다() throws Exception {
        assertThat(subject.getGender()).isEqualTo("female");
    }

    @Test
    public void setLastUpdateStatTimestamp호출시_SharedPreference에_생년월일을_저장한다() throws Exception {
        subject.setLastUpdateStatTimestamp(1000L);
        assertThat(sf.getLong("LAST_UPDATE_STAT_TIMESTAMP", 0)).isEqualTo(1000L);
    }

    @Test
    public void getLastUpdateStatTimestamp호출시_SharedPreference에_저장된_생년월일을_리턴한다() throws Exception {
        assertThat(subject.getLastUpdateStatTimestamp()).isEqualTo(1000L);
    }

    @Test
    public void setInvitationCode호출시_SharedPreference에_인증코드를_저장한다() throws Exception {
        subject.setInvitationCode("CODE");
        assertThat(sf.getString("INVITATION_CODE", "")).isEqualTo("CODE");
    }

    @Test
    public void getInvitationCode호출시_SharedPreference에_저장된_인증코드를_리턴한다() throws Exception {
        assertThat(subject.getInvitationCode()).isEqualTo("CODE");
    }
}
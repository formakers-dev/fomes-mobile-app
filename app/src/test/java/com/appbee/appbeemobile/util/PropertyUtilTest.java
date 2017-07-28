package com.appbee.appbeemobile.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PropertyUtilTest {
    private PropertyUtil subject;
    private SharedPreferences sf;

    @Before
    public void setUp() throws Exception {
        subject = new PropertyUtil(RuntimeEnvironment.application);

        sf = RuntimeEnvironment.application.getSharedPreferences(RuntimeEnvironment.application.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        sf.edit()
                .putString("TEST_STRING_KEY", "TEST_STRING_VALUE")
                .putLong("TEST_LONG_KEY", 1234567890L)
                .apply();
    }

    @Test
    public void getStringTest() throws Exception {
        assertThat(subject.getString("TEST_STRING_KEY", null)).isEqualTo("TEST_STRING_VALUE");
        assertThat(subject.getString("INVALID_TEST_STRING_KEY", "DEFAULT_VALUE")).isEqualTo("DEFAULT_VALUE");
    }

    @Test
    public void getLongTest() throws Exception {
        assertThat(subject.getLong("TEST_LONG_KEY", 0L)).isEqualTo(1234567890L);
        assertThat(subject.getLong("INVALID_TEST_LONG_KEY", 1234L)).isEqualTo(1234L);
    }

    @Test
    public void putStringTest() throws Exception {
        subject.putString("TEST_STRING_KEY", "NEW_TEST_STRING_VALUE");
        assertThat(sf.getString("TEST_STRING_KEY", null)).isEqualTo("NEW_TEST_STRING_VALUE");
    }

    @Test
    public void putLongTest() throws Exception {
        subject.putLong("TEST_LONG_KEY", 9876543210L);
        assertThat(sf.getLong("TEST_LONG_KEY", 0L)).isEqualTo(9876543210L);
    }
}
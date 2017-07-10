package com.appbee.appbeemobile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ExampleUnitTest {
    MainActivity subject;

    @Before
    public void setUp() throws Exception {
        subject = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void test_helloworld() throws Exception {
        assertNotNull(subject.findViewById(R.id.helloWorld));
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}
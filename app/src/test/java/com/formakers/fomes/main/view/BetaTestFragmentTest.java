package com.formakers.fomes.main.view;

import com.formakers.fomes.BuildConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
@Ignore
public class BetaTestFragmentTest {
    BetaTestFragment subject;
    SupportFragmentController<BetaTestFragment> controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new BetaTestFragment();
        controller = SupportFragmentController.of(subject);

        controller.create().start().resume().visible();
    }

    @After
    public void tearDown() throws Exception {
    }
}
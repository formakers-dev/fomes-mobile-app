package com.formakers.fomes.provisioning.view;

import android.app.Activity;
import android.content.Intent;

import com.formakers.fomes.main.view.MainActivity;
import com.formakers.fomes.provisioning.contract.LoginContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityViewTest {

    ActivityController<LoginActivity> controller;

    LoginContract.View subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        controller = Robolectric.buildActivity(LoginActivity.class);
        subject = controller.get();
    }

    @Test
    public void showToast() {
        subject.showToast("test");

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("test");
    }

    @Test
    public void startActivityAndFinish() {
        subject.startActivityAndFinish(MainActivity.class);

        Intent intent = shadowOf((Activity) subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(MainActivity.class.getName());
    }
}
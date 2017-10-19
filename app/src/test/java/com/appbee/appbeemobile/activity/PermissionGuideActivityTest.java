package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.provider.Settings;
import android.widget.Button;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PermissionGuideActivityTest extends ActivityTest {

    private PermissionGuideActivity subject;
    private Unbinder binder;

    @BindView(R.id.permission_button)
    Button permissionButton;

    @Inject
    AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        subject = Robolectric.setupActivity(PermissionGuideActivity.class);
        binder = ButterKnife.bind(this, subject);
    }

    @After
    public void tearDown() throws Exception {
        binder.unbind();
    }

    @Test
    public void onCreate호출시_startButton이_보여진다() throws Exception {
        assertThat(permissionButton.isShown()).isTrue();
    }

    @Test
    public void startButton클릭시_권한이없는경우_권할설정페이지가_보여진다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        permissionButton.performClick();

        assertThat(shadowOf(subject).getNextStartedActivity().getAction()).isEqualTo(Settings.ACTION_USAGE_ACCESS_SETTINGS);
    }

    @Test
    public void startButton클릭시_권한이있는경우_LoadingActivity를_호출한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);

        permissionButton.performClick();

        assertMoveToLoadingActivity();
    }

    private void assertMoveToLoadingActivity() {
        Intent intent = Shadows.shadowOf(subject).peekNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).isEqualTo(LoadingActivity.class.getCanonicalName());
    }

    @Test
    public void 권한설정이_완료되고_돌아오면_LoadingActivity를_호출한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        subject.onActivityResult(1001, 0, null);
        assertThat(shadowOf(subject).getNextStartedActivityForResult().intent.getComponent().getClassName()).isEqualTo(LoadingActivity.class.getCanonicalName());
    }

    @Ignore
    @Test
    public void 권한설정이_완료되지않고_돌아오면_앱을_종료한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        subject.onActivityResult(1001, 0, null);
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}
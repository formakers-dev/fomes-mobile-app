package com.appbee.appbeemobile.activity;

import android.provider.Settings;
import android.widget.Button;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
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

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockLocalStorageHelper.getEmail()).thenReturn("test@test.com");
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        subject = Robolectric.setupActivity(PermissionGuideActivity.class);
        binder = ButterKnife.bind(this, subject);
    }

    @After
    public void tearDown() throws Exception {
        binder.unbind();
    }

    @Test
    public void onCreate호출시_LocalStorage에저장된이메일이없을경우_로그인화면으로_이동하고_종료한다() throws Exception {
        when(mockLocalStorageHelper.getEmail()).thenReturn("");
        subject = Robolectric.setupActivity(PermissionGuideActivity.class);

        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(LoginActivity.class.getName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void onCreate호출시_권한이있는경우_MainActivity로_이동한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        subject = Robolectric.setupActivity(PermissionGuideActivity.class);

        assertThat(shadowOf(subject).getNextStartedActivity().getComponent().getClassName()).isEqualTo(MainActivity.class.getName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void startButton클릭시_권한설정페이지가_보여진다() throws Exception {
        permissionButton.performClick();

        assertThat(shadowOf(subject).getNextStartedActivity().getAction()).isEqualTo(Settings.ACTION_USAGE_ACCESS_SETTINGS);
    }

    @Test
    public void 권한설정이_완료되고_돌아오면_LoadingActivity로_이동한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        subject.onActivityResult(1001, 0, null);

        assertThat(shadowOf(subject).getNextStartedActivityForResult().intent.getComponent().getClassName()).isEqualTo(LoadingActivity.class.getName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}
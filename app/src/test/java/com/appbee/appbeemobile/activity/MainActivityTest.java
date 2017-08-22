package com.appbee.appbeemobile.activity;

import android.content.Intent;
import android.provider.Settings;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest extends ActivityTest {

    private ActivityController<MainActivity> activityController;

    @Inject
    AppStatService mockAppStatService;

    @Inject
    AppService mockAppService;

    @Inject
    AppRepositoryHelper mockAppRepositoryHelper;

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;

    @Inject
    AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication)RuntimeEnvironment.application).getComponent().inject(this);
        activityController = Robolectric.buildActivity(MainActivity.class);

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
    }

    @Test
    public void onCreate호출시_Stat접근권한이_없는_경우_권한요청_Activity를_호출한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        MainActivity subject = activityController.create().get();

        ShadowActivity.IntentForResult nextStartedActivityForResult = shadowOf(subject).getNextStartedActivityForResult();
        assertThat(nextStartedActivityForResult.intent.getAction()).isEqualTo(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        assertThat(nextStartedActivityForResult.requestCode).isEqualTo(1001);
    }

    @Test
    public void onCreate호출시_Stat접근권한이_있는_경우_권한요청_Activity를_호출하지_않는다() throws Exception {
        MainActivity subject = activityController.create().get();
        assertThat(shadowOf(subject).getNextStartedActivity()).isNull();
    }

    @Test
    public void onCreate호출시_Stat접근권한이_있는_경우_사용이력이있는_앱목록정보조회API를_호출한다() throws Exception {
        List<String> usedPackageNameList = Arrays.asList("com.package.name1","com.package.name2");
        when(mockAppStatService.getUsedPackageNameList()).thenReturn(usedPackageNameList);

        MainActivity subject = activityController.create().get();

        verify(mockAppService).getInfos(eq(usedPackageNameList), eq(subject.appInfosServiceCallback));
    }

    @Test
    public void onCreate호출시_SocialApp을_insert한다() throws Exception {
        activityController.create().get();
        verify(mockAppRepositoryHelper).insertSocialApps(any(List.class));
    }

    @Test
    public void appInfosServiceCallback의_onFail을_호출했을때_에러메시지가_표시된다() throws Exception {
        MainActivity subject = activityController.get();
        subject.appInfosServiceCallback.onFail("ERROR_CODE");

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("appList API fail");
    }

    @Test
    public void appInfosServiceCallback의_onSuccess를_호출했을때_DB에_결과를_저장한다() throws Exception {
        List<AppInfo> returnedAppInfos = mock(List.class);
        MainActivity subject = activityController.create().get();
        Map<String, Long> mockLongTermStatsSummary = mock(Map.class);
        when(mockAppUsageDataHelper.getLongTermStatsSummary()).thenReturn(mockLongTermStatsSummary);

        subject.appInfosServiceCallback.onSuccess(returnedAppInfos);

        verify(mockAppRepositoryHelper).insertUsedApps(eq(returnedAppInfos));
        verify(mockAppRepositoryHelper).updateTotalUsedTime(eq(mockLongTermStatsSummary));
    }

    @Test
    public void appInfosServiceCallback의_onSuccess를_호출했을때_분석결과화면으로_이동한다() throws Exception {
        MainActivity subject = activityController.create().get();
        subject.appInfosServiceCallback.onSuccess(mock(List.class));

        assertLaunchAnalysisResultActivity(subject);
    }

    @Test
    public void 권한요청에대한_onActivityResult호출시_접근권한이_부여된_경우_앱목록정보조회API를_요청한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        MainActivity subject = activityController.create().get();
        shadowOf(subject).getNextStartedActivity();

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        subject.onActivityResult(1001, 0, null);

        verify(mockAppService).getInfos(any(), eq(subject.appInfosServiceCallback));
    }

    @Test
    public void 권한요청에대한_onActivityResult호출시_접근권한이_부여되지_않은_경우_앱을_종료한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        MainActivity subject = activityController.create().get();

        subject.onActivityResult(1001, 0, null);

        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    private void assertLaunchAnalysisResultActivity(MainActivity subject) {
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).contains(AnalysisResultActivity.class.getSimpleName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}
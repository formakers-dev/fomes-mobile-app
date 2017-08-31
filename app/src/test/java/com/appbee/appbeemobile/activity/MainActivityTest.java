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

import org.junit.After;
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

import butterknife.ButterKnife;
import butterknife.Unbinder;

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

    private Unbinder binder;

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
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        activityController = Robolectric.buildActivity(MainActivity.class);

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        if (binder != null) {
            binder.unbind();
        }
    }

    @Test
    public void onCreate호출시_Stat접근권한이_없는_경우_권한요청_Activity를_호출한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        MainActivity subject = createSubjectWithPostCreateLifecycle();

        ShadowActivity.IntentForResult nextStartedActivityForResult = shadowOf(subject).getNextStartedActivityForResult();
        assertThat(nextStartedActivityForResult.intent.getAction()).isEqualTo(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        assertThat(nextStartedActivityForResult.requestCode).isEqualTo(1001);
    }

    private MainActivity createSubjectWithPostCreateLifecycle() {
        MainActivity subject = activityController.create().postCreate(null).get();
        binder = ButterKnife.bind(this, subject);
        return subject;
    }

    @Test
    public void onCreate호출시_Stat접근권한이_있는_경우_권한요청_Activity를_호출하지_않는다() throws Exception {
        MainActivity subject = createSubjectWithPostCreateLifecycle();
        assertThat(shadowOf(subject).getNextStartedActivity()).isNull();
    }

    @Test
    public void onCreate호출시_Stat접근권한이_있는_경우_사용이력이있는_앱목록정보조회API를_호출한다() throws Exception {
        List<String> usedPackageNameList = Arrays.asList("com.package.name1", "com.package.name2");
        when(mockAppStatService.getUsedPackageNameList()).thenReturn(usedPackageNameList);

        MainActivity subject = createSubjectWithPostCreateLifecycle();

        verify(mockAppService).getInfos(eq(usedPackageNameList), eq(subject.appInfosServiceCallback));
    }

    @Test
    public void appInfosServiceCallback의_onFail을_처음_호출했을때_AppInfo서비스를_재요청한다() throws Exception {
        MainActivity subject = activityController.create().get();
        subject.appInfosServiceCallback.onFail("ERROR_CODE");

        verify(mockAppService).getInfos(any(List.class), eq(subject.appInfosServiceCallback));
        assertThat(ShadowToast.getLatestToast()).isNull();
    }

    @Test
    public void appInfosServiceCallback의_onFail을_2번_호출했을때_에러메시지가_표시된다() throws Exception {
        MainActivity subject = activityController.create().get();
        subject.appInfosServiceCallback.onFail("ERROR_CODE");
        subject.appInfosServiceCallback.onFail("ERROR_CODE");

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("appList API fail");
    }

    @Test
    public void appInfosServiceCallback의_onSuccess를_호출했을때_DB에_결과를_저장한다() throws Exception {
        List<AppInfo> returnedAppInfos = mock(List.class);
        MainActivity subject = createSubjectWithPostCreateLifecycle();
        Map<String, Long> mockLongTermStatsSummary = mock(Map.class);
        when(mockAppUsageDataHelper.getLongTermStatsSummary()).thenReturn(mockLongTermStatsSummary);

        subject.appInfosServiceCallback.onSuccess(returnedAppInfos);

        verify(mockAppRepositoryHelper).insertUsedApps(eq(returnedAppInfos));
        verify(mockAppRepositoryHelper).updateTotalUsedTime(eq(mockLongTermStatsSummary));
    }

    @Test
    public void appInfosServiceCallback의_onSuccess를_호출했을때_분석결과화면으로_이동한다() throws Exception {
        MainActivity subject = createSubjectWithPostCreateLifecycle();
        subject.appInfosServiceCallback.onSuccess(mock(List.class));

        assertLaunchAnalysisResultActivity(subject);
    }

    @Test
    public void 권한요청에대한_onActivityResult호출시_접근권한이_부여된_경우_앱목록정보조회API를_요청한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        MainActivity subject = createSubjectWithPostCreateLifecycle();
        shadowOf(subject).getNextStartedActivity();

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        subject.onActivityResult(1001, 0, null);

        verify(mockAppService).getInfos(any(), eq(subject.appInfosServiceCallback));
    }

    @Test
    public void 권한요청에대한_onActivityResult호출시_접근권한이_부여되지_않은_경우_앱을_종료한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);
        MainActivity subject = createSubjectWithPostCreateLifecycle();

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
package com.appbee.appbeemobile.activity;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppBeeAndroidNativeHelper;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.AppStatService;
import com.appbee.appbeemobile.network.UserService;
import com.appbee.appbeemobile.repository.helper.AppRepositoryHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoadingActivityTest extends ActivityTest {

    private ActivityController<LoadingActivity> activityController;

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

    @Inject
    UserService mockUserService;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        activityController = Robolectric.buildActivity(LoadingActivity.class);

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        when(mockAppStatService.getLastUpdateStatTimestamp()).thenReturn(Observable.just(0L));
        when(mockAppStatService.sendShortTermStats(anyLong())).thenReturn(Observable.just(true));

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    }

    @After
    public void tearDown() throws Exception {
        if (binder != null) {
            binder.unbind();
        }
        RxJavaHooks.reset();
    }

    private LoadingActivity createSubjectWithPostCreateLifecycle() {
        LoadingActivity subject = activityController.create().postCreate(null).get();
        binder = ButterKnife.bind(this, subject);
        return subject;
    }

    @Test
    public void onPostCreate호출시_유저의정보와_사용이력이있는단기통계데이터를_전송하고_앱목록정보조회API를_호출한다() throws Exception {
        List<String> usedPackageNameList = Arrays.asList("com.package.name1", "com.package.name2");
        when(mockAppStatService.getUsedPackageNameList()).thenReturn(usedPackageNameList);

        LoadingActivity subject = createSubjectWithPostCreateLifecycle();

        verify(mockUserService).sendUser(any(User.class));
        verify(mockAppStatService).sendShortTermStats(anyLong());
        verify(mockAppService).getInfos(eq(usedPackageNameList), eq(subject.appInfosServiceCallback));
    }

    @Test
    public void appInfosServiceCallback의_onFail을_처음_호출했을때_AppInfo서비스를_재요청한다() throws Exception {
        LoadingActivity subject = activityController.create().get();
        subject.appInfosServiceCallback.onFail("ERROR_CODE");

        verify(mockAppService).getInfos(any(List.class), eq(subject.appInfosServiceCallback));
        assertThat(ShadowToast.getLatestToast()).isNull();
    }

    @Test
    @Ignore
    public void appInfosServiceCallback의_onFail을_2번_호출했을때_에러메시지가_표시된다() throws Exception {
        LoadingActivity subject = activityController.create().get();
        subject.appInfosServiceCallback.onFail("ERROR_CODE");
        subject.appInfosServiceCallback.onFail("ERROR_CODE");

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("appList API fail");
    }

    @Test
    public void appInfosServiceCallback의_onSuccess를_호출했을때_DB에_결과를_저장한다() throws Exception {
        List<AppInfo> returnedAppInfos = mock(List.class);
        LoadingActivity subject = createSubjectWithPostCreateLifecycle();
        Map<String, Long> mockStatsSummary = mock(Map.class);
        when(mockAppUsageDataHelper.getShortTermStatsTimeSummary()).thenReturn(mockStatsSummary);

        subject.appInfosServiceCallback.onSuccess(returnedAppInfos);

        verify(mockAppRepositoryHelper).insertUsedApps(eq(returnedAppInfos));
        verify(mockAppRepositoryHelper).updateTotalUsedTime(eq(mockStatsSummary));
    }

    @Test
    public void appInfosServiceCallback의_onSuccess를_호출했을때_크롤링되지않은_앱목록을_서버로_전송한다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        List<String> usedPackageNameList = new ArrayList<>();
        usedPackageNameList.add("com.package.name1");
        usedPackageNameList.add("com.package.name2");
        usedPackageNameList.add("com.package.name3");
        when(mockAppStatService.getUsedPackageNameList()).thenReturn(usedPackageNameList);

        List<AppInfo> returnedAppInfos = new ArrayList<>();
        returnedAppInfos.add(new AppInfo("com.package.name1", null, null, null, null, null));
        returnedAppInfos.add(new AppInfo("com.package.name2", null, null, null, null, null));
        LoadingActivity subject = createSubjectWithPostCreateLifecycle();
        Map<String, Long> mockShortTermStatsSummary = mock(Map.class);
        when(mockAppUsageDataHelper.getShortTermStatsTimeSummary()).thenReturn(mockShortTermStatsSummary);

        subject.appInfosServiceCallback.onSuccess(returnedAppInfos);

        ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockAppService).postUncrawledApps(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().size(), 1);
        assertEquals(argumentCaptor.getValue().get(0), "com.package.name3");
    }

    @Test
    public void appInfosServiceCallback의_onSuccess를_호출했을때_크롤링되지않은_앱목록이_없으면_크롤링되지않은_앱목록을_서버로_전송하지_않는다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        List<String> usedPackageNameList = new ArrayList<>();
        usedPackageNameList.add("com.package.name1");
        when(mockAppStatService.getUsedPackageNameList()).thenReturn(usedPackageNameList);

        List<AppInfo> returnedAppInfos = new ArrayList<>();
        returnedAppInfos.add(new AppInfo("com.package.name1", null, null, null, null, null));
        LoadingActivity subject = createSubjectWithPostCreateLifecycle();
        Map<String, Long> mockShortTermStatsSummary = mock(Map.class);
        when(mockAppUsageDataHelper.getShortTermStatsTimeSummary()).thenReturn(mockShortTermStatsSummary);

        subject.appInfosServiceCallback.onSuccess(returnedAppInfos);

        verify(mockAppService, never()).postUncrawledApps(any(List.class));
    }

    @Test
    public void appInfosServiceCallback의_onSuccess를_호출했을때_분석결과화면으로_이동한다() throws Exception {
        LoadingActivity subject = createSubjectWithPostCreateLifecycle();
        subject.appInfosServiceCallback.onSuccess(mock(List.class));

        assertLaunchMainActivity(subject);
    }

    private void assertLaunchMainActivity(LoadingActivity subject) {
        ShadowActivity shadowActivity = shadowOf(subject);
        Intent nextStartedActivity = shadowActivity.getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName()).contains(MainActivity.class.getSimpleName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }
}
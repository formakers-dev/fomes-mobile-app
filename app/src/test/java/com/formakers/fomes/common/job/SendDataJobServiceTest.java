package com.formakers.fomes.common.job;

import android.app.job.JobParameters;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.helper.AppBeeAndroidNativeHelper;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.helper.MessagingHelper;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.network.AppStatService;
import com.formakers.fomes.network.UserService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SendDataJobServiceTest {

    private SendDataJobService subject;

    @Inject SharedPreferencesHelper mockSharedPreferencesHelper;
    @Inject AppBeeAndroidNativeHelper mockAppBeeAndroidNativeHelper;
    @Inject AppUsageDataHelper mockAppUsageDataHelper;
    @Inject MessagingHelper mockMessagingHelper;
    @Inject UserService mockUserService;
    @Inject AppStatService mockAppStatService;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        when(mockSharedPreferencesHelper.isLoggedIn()).thenReturn(true);
        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("myToken");
        when(mockSharedPreferencesHelper.getRegistrationToken()).thenReturn("myRegistrationToken");
        when(mockMessagingHelper.getMessagingToken()).thenReturn("myRegistrationToken");

        subject = Robolectric.setupService(SendDataJobService.class);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
    }

    @Test
    public void onStartJob_실행시_노티토큰이_업데이트되어있지않을경우_서버로_노티토큰_정보를_전송한다() throws Exception {
        when(mockMessagingHelper.getMessagingToken()).thenReturn("newRegistrationToken");
        when(mockUserService.updateRegistrationToken(eq("newRegistrationToken"))).thenReturn(Completable.complete());

        JobParameters jobParameters = mock(JobParameters.class);
        when(jobParameters.getJobId()).thenReturn(1);
        when(jobParameters.isOverrideDeadlineExpired()).thenReturn(false);

        subject.onStartJob(jobParameters);

        verify(mockUserService).updateRegistrationToken(eq("newRegistrationToken"));
    }

    @Test
    public void onStartJob_실행시_단기통계데이터와_7일동안의_앱사용통계정보를_서버로_전송한다() throws Exception {
        List<AppUsage> appUsages = new ArrayList<>();
        appUsages.add(new AppUsage("packageName1", 1000));
        appUsages.add(new AppUsage("packageName2", 2000));
        when(mockAppUsageDataHelper.getAppUsagesFor(7)).thenReturn(appUsages);

        JobParameters jobParameters = mock(JobParameters.class);
        when(jobParameters.getJobId()).thenReturn(1);
        when(jobParameters.isOverrideDeadlineExpired()).thenReturn(false);

        subject.onStartJob(jobParameters);

        verify(mockAppUsageDataHelper).sendShortTermStats();
        verify(mockAppUsageDataHelper).getAppUsagesFor(eq(7));
        verify(mockAppStatService).sendAppUsages(eq(appUsages));
    }

    @Test
    public void onStartJob_실행시_권한이없으면_아무것도하지않는다() throws Exception {
        when(mockAppBeeAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        JobParameters jobParameters = mock(JobParameters.class);
        when(jobParameters.getJobId()).thenReturn(1);
        when(jobParameters.isOverrideDeadlineExpired()).thenReturn(false);
        subject.onStartJob(jobParameters);

        verify(mockUserService, never()).updateRegistrationToken(any());
        verify(mockAppUsageDataHelper, never()).sendShortTermStats();
        verify(mockAppUsageDataHelper, never()).sendAppUsages();
    }

//    @Test
//    public void onStartJob_실행시_로그인상태가_아니면_아무것도하지않는다() throws Exception {
//        when(mockSharedPreferencesHelper.isLoggedIn()).thenReturn(false);
//
//        JobParameters jobParameters = mock(JobParameters.class);
//        when(jobParameters.getJobId()).thenReturn(1);
//        when(jobParameters.isOverrideDeadlineExpired()).thenReturn(false);
//        subject.onStartJob(jobParameters);
//
//        verify(mockAppUsageDataHelper, never()).sendShortTermStats(any());
//        verify(mockAppUsageDataHelper, never()).sendAppUsages(any());
//    }
}
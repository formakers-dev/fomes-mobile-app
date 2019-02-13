package com.formakers.fomes.common.job;

import android.app.job.JobParameters;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.helper.AndroidNativeHelper;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.AppUsage;
import com.formakers.fomes.model.User;

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
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
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
    @Inject AndroidNativeHelper mockAndroidNativeHelper;
    @Inject AppUsageDataHelper mockAppUsageDataHelper;
    @Inject UserService mockUserService;
    @Inject AppStatService mockAppStatService;
    @Inject ChannelManager mockChannelManager;
    @Inject UserDAO mockUserDAO;

    List<AppUsage> appUsages = new ArrayList<>();
    User mockUser = mock(User.class);

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        ((TestFomesApplication) RuntimeEnvironment.application).getComponent().inject(this);

        appUsages.add(new AppUsage("packageName1", 1000));
        appUsages.add(new AppUsage("packageName2", 2000));

        when(mockAppUsageDataHelper.getAppUsagesFor(7)).thenReturn(appUsages);
        when(mockAppUsageDataHelper.sendShortTermStats()).thenReturn(Completable.complete());
        when(mockAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        when(mockSharedPreferencesHelper.hasAccessToken()).thenReturn(true);
        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("myToken");
        when(mockSharedPreferencesHelper.getRegistrationToken()).thenReturn("myRegistrationToken");
        when(mockUserDAO.getUserInfo()).thenReturn(Single.just(mockUser));
        when(mockUserService.updateUser(any(User.class))).thenReturn(Completable.complete());
        when(mockUserService.notifyActivated()).thenReturn(Completable.complete());

        subject = Robolectric.setupService(SendDataJobService.class);
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void onStartJob_실행시_단기통계데이터와_7일동안의_앱사용통계정보를_서버로_전송한다() {
        JobParameters jobParameters = mock(JobParameters.class);
        when(jobParameters.getJobId()).thenReturn(1);
        when(jobParameters.isOverrideDeadlineExpired()).thenReturn(false);

        subject.onStartJob(jobParameters);

        verify(mockAppUsageDataHelper).sendShortTermStats();
        verify(mockAppUsageDataHelper).getAppUsagesFor(eq(7));
        verify(mockAppStatService).sendAppUsages(eq(appUsages));
        verify(mockUserService).notifyActivated();
        verify(mockChannelManager).subscribePublicTopic();
        verify(mockUserDAO).getUserInfo();
        verify(mockUserService).updateUser(eq(mockUser));
    }

    @Test
    public void onStartJob_실행시_권한이없으면_통계데이터는_전송하지않는다() {
        when(mockAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        JobParameters jobParameters = mock(JobParameters.class);
        when(jobParameters.getJobId()).thenReturn(1);
        when(jobParameters.isOverrideDeadlineExpired()).thenReturn(false);

        subject.onStartJob(jobParameters);

        verify(mockAppUsageDataHelper, never()).sendShortTermStats();
        verify(mockAppStatService, never()).sendAppUsages(any());
        verify(mockUserService).notifyActivated();
        verify(mockChannelManager).subscribePublicTopic();
        verify(mockUserDAO).getUserInfo();
        verify(mockUserService).updateUser(eq(mockUser));
    }

//    @Test
//    public void onStartJob_실행시_로그인상태가_아니면_아무것도하지않는다() throws Exception {
//        when(mockSharedPreferencesHelper.hasAccessToken()).thenReturn(false);
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
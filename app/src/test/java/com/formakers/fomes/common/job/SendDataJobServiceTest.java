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
import org.mockito.ArgumentCaptor;
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

import static org.assertj.core.api.Java6Assertions.assertThat;
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

        when(mockAppUsageDataHelper.getAppUsages()).thenReturn(appUsages);
        when(mockAppUsageDataHelper.sendShortTermStats()).thenReturn(Completable.complete());
        when(mockAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        when(mockSharedPreferencesHelper.hasAccessToken()).thenReturn(true);
        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("myToken");
        when(mockSharedPreferencesHelper.getRegistrationToken()).thenReturn("myRegistrationToken");
        when(mockUserDAO.getUserInfo()).thenReturn(Single.just(new User()));
        when(mockUserService.updateUser(any(User.class))).thenReturn(Completable.complete());
        when(mockUserService.notifyActivated()).thenReturn(Completable.complete());

        subject = Robolectric.setupService(SendDataJobService.class);
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void onStartJob_실행시__유저의_활성화시각을_업데이트를_요청한다() {
        subject_onStartJob();

        verify(mockUserService).notifyActivated();
    }

    @Test
    public void onStartJob_실행시__공지용_전체채널을_구독시킨다() {
        subject_onStartJob();

        verify(mockChannelManager).subscribePublicTopic();
    }

    @Test
    public void onStartJob_실행시__유저정보와_유저의_현재앱버전과_FCM토큰을__서버로_올린다() {
        subject_onStartJob();

        // 유저정보
        verify(mockUserDAO).getUserInfo();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).updateUser(userArgumentCaptor.capture());

        // 현재 앱버전 셋팅했는지
        User requestedUser = userArgumentCaptor.getValue();
        assertThat(requestedUser.getAppVersion()).isEqualTo(BuildConfig.VERSION_NAME);

        // FCM 토큰 셋팅했는지
        assertThat(requestedUser.getRegistrationToken()).isEqualTo("myRegistrationToken");
    }

    @Test
    public void onStartJob_실행시_단기통계데이터와_7일동안의_앱사용통계정보를_서버로_전송한다() {
        subject_onStartJob();

        verify(mockAppUsageDataHelper).sendShortTermStats();
        verify(mockAppUsageDataHelper).getAppUsages();
        verify(mockAppStatService).sendAppUsages(eq(appUsages));
    }

    @Test
    public void onStartJob_실행시_권한이없으면_통계데이터는_전송하지않는다() {
        when(mockAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        subject_onStartJob();

        verify(mockAppUsageDataHelper, never()).sendShortTermStats();
        verify(mockAppStatService, never()).sendAppUsages(any());
    }

    private void subject_onStartJob() {
        JobParameters jobParameters = mock(JobParameters.class);
        when(jobParameters.getJobId()).thenReturn(1);
        when(jobParameters.isOverrideDeadlineExpired()).thenReturn(false);

        subject.onStartJob(jobParameters);
    }
}
package com.formakers.fomes.common.job;

import android.app.job.JobParameters;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.noti.ChannelManager;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.AppUsageDataHelper;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.model.AppUsage;
import com.formakers.fomes.common.model.ShortTermStat;
import com.formakers.fomes.common.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
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
    List<ShortTermStat> shortTermStats = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.trampoline());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.trampoline());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.trampoline());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.trampoline();
            }
        });

        ((TestFomesApplication) ApplicationProvider.getApplicationContext()).getComponent().inject(this);

        shortTermStats.add(new ShortTermStat("packageName1", 1000, 2000));
        shortTermStats.add(new ShortTermStat("packageName2", 2000, 3000));

        appUsages.add(new AppUsage("packageName1", 1000));
        appUsages.add(new AppUsage("packageName2", 2000));

        when(mockAppUsageDataHelper.getAppUsages()).thenReturn(Observable.from(appUsages));
        when(mockAppUsageDataHelper.getShortTermStats()).thenReturn(Observable.from(shortTermStats));
        when(mockAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(true);
        when(mockSharedPreferencesHelper.hasAccessToken()).thenReturn(true);
        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("myToken");
        when(mockSharedPreferencesHelper.getUserRegistrationToken()).thenReturn("myRegistrationToken");
        when(mockUserDAO.getUserInfo()).thenReturn(Single.just(new User()));
        when(mockUserService.updateUser(any(User.class))).thenReturn(Completable.complete());
        when(mockUserService.notifyActivated()).thenReturn(Completable.complete());
        when(mockAppStatService.sendAppUsages(any())).thenReturn(Completable.complete());
        when(mockAppStatService.sendShortTermStats(any())).thenReturn(Completable.complete());

        subject = Robolectric.setupService(SendDataJobService.class);
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void onStartJob_?????????__?????????_??????????????????_???????????????_????????????() {
        subject_onStartJob();

        verify(mockUserService).notifyActivated();
    }

    @Test
    public void onStartJob_?????????__?????????_???????????????__?????????_???????????????_???????????????() {
        when(mockChannelManager.isSubscribedTopic(FomesConstants.Notification.TOPIC_NOTICE_ALL))
                .thenReturn(true);

        subject_onStartJob();

        verify(mockChannelManager).subscribeTopic(FomesConstants.Notification.TOPIC_NOTICE_ALL);
    }

    @Test
    public void onStartJob_?????????__?????????_???????????????___?????????_???????????????_?????????????????????() {
        when(mockChannelManager.isSubscribedTopic(FomesConstants.Notification.TOPIC_NOTICE_ALL))
                .thenReturn(false);

        subject_onStartJob();

        verify(mockChannelManager, never()).subscribeTopic(FomesConstants.Notification.TOPIC_NOTICE_ALL);
    }

    @Test
    public void onStartJob_?????????__?????????_??????????????????_FCM?????????__?????????_?????????() {
        subject_onStartJob();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).updateUserInfo(userArgumentCaptor.capture());

        // ?????? ????????? ???????????????
        User requestedUser = userArgumentCaptor.getValue();
        assertThat(requestedUser.getAppVersion()).isEqualTo(BuildConfig.VERSION_NAME);

        // FCM ?????? ???????????????
        assertThat(requestedUser.getRegistrationToken()).isEqualTo("myRegistrationToken");

        // ???????????? ?????? ??????????????? (CI ????????? ????????? ????????? ??? ??? ?????? ????????? ?????????)
        System.out.println(requestedUser.getDevice());
        assertThat(requestedUser.getDevice()).isNotNull();
        assertThat(requestedUser.getDevice().getManufacturer()).isEqualTo(Build.MANUFACTURER);
        assertThat(requestedUser.getDevice().getModel()).isEqualTo(Build.MODEL);
        assertThat(requestedUser.getDevice().getOsVersion()).isEqualTo(Build.VERSION.SDK_INT);
    }

    @Test
    public void onStartJob_?????????_????????????????????????_????????????????????????_????????????????????????_?????????_????????????() {
        subject_onStartJob();

        verify(mockAppUsageDataHelper).getShortTermStats();
        verify(mockAppStatService).sendShortTermStats(eq(mockAppUsageDataHelper.getShortTermStats()));

        verify(mockAppStatService).sendAppUsages(eq(mockAppUsageDataHelper.getAppUsages()));
    }

    @Test
    public void onStartJob_?????????_??????????????????_??????????????????_?????????????????????() {
        when(mockAndroidNativeHelper.hasUsageStatsPermission()).thenReturn(false);

        subject_onStartJob();

        verify(mockAppStatService, never()).sendShortTermStats(any());
        verify(mockAppStatService, never()).sendAppUsages(any());
    }

    private void subject_onStartJob() {
        JobParameters jobParameters = mock(JobParameters.class);
        when(jobParameters.getJobId()).thenReturn(1);
        when(jobParameters.isOverrideDeadlineExpired()).thenReturn(false);

        subject.onStartJob(jobParameters);
    }
}
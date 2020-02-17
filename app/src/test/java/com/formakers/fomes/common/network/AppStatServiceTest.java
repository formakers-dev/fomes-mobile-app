package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.StatAPI;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.helper.TimeHelper;
import com.formakers.fomes.common.model.AppUsage;
import com.formakers.fomes.common.model.ShortTermStat;
import com.formakers.fomes.common.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AppStatServiceTest extends AbstractServiceTest {

    private AppStatService subject;

    @Mock private SharedPreferencesHelper mockSharedPreferencesHelper;
    @Mock private StatAPI mockStatAPI;
    @Mock private TimeHelper mockTimeHelper;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.trampoline());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.trampoline());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.trampoline());
//        RxJavaHooks.setOnComputationScheduler(scheduler -> testScheduler);

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        subject = new AppStatService(mockStatAPI, mockSharedPreferencesHelper, getMockAPIHelper(), mockTimeHelper);
        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");
    }

    @Test
    public void sendShortTermStats호출시_전달받은_단기통계데이터를_서버로_전송한다() throws Exception {
        List<ShortTermStat> mockShortTermStats = new ArrayList<>();
        mockShortTermStats.add(new ShortTermStat("anyPackage", 1000L, 3000L));
        when(mockStatAPI.postShortTermStats(anyString(), any(List.class))).thenReturn(mock(Observable.class));

        subject.sendShortTermStats(Observable.from(mockShortTermStats)).subscribe(new TestSubscriber<>());

        ArgumentCaptor<List<ShortTermStat>> shortTermStatsCaptor = ArgumentCaptor.forClass(List.class);

        verify(mockStatAPI).postShortTermStats(anyString(), shortTermStatsCaptor.capture());
        ShortTermStat actualShortTermStat = shortTermStatsCaptor.getValue().get(0);
        assertEquals(actualShortTermStat.getPackageName(), "anyPackage");
        assertEquals(actualShortTermStat.getStartTimeStamp(), 1000L);
        assertEquals(actualShortTermStat.getEndTimeStamp(), 3000L);
        assertEquals(actualShortTermStat.getTotalUsedTime(), 2000L);
    }

    @Test
    public void sendShortTermStats호출시_단기통계데이터가_없는경우_서버로_전송하지_않는다() throws Exception {
        subject.sendShortTermStats(Observable.from(new ArrayList<>()));

        verify(mockStatAPI, never()).postShortTermStats(anyString(), any(List.class));
    }

    @Test
    public void sendShortTermStats호출시_토큰_만료_여부를_확인한다() throws Exception {
        List<ShortTermStat> shortTermStatList = Collections.singletonList(new ShortTermStat("packageName", 0L, 999L));
        verifyToCheckExpiredToken(subject.sendShortTermStats(Observable.from(shortTermStatList)).toObservable());
    }


    @Test
    public void sendShortTermStats_완료시__마지막_업데이트_시간을_갱신한다() throws Exception {
        when(mockTimeHelper.getStatBasedCurrentTime()).thenReturn(10L);
        List<ShortTermStat> shortTermStatList = Collections.singletonList(new ShortTermStat("packageName", 0L, 999L));
        when(mockStatAPI.postShortTermStats(anyString(), any(List.class))).thenReturn(Observable.empty());

        TestSubscriber<Void> testSubscriber = new TestSubscriber<>();
        subject.sendShortTermStats(Observable.from(shortTermStatList)).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        verify(mockSharedPreferencesHelper).setLastUpdateShortTermStatTimestamp(eq(10L));
    }

    @Test
    public void postAppUsages호출시_앱별_사용정보통계를_서버로_전송한다() throws Exception {
        List<AppUsage> appUsageList = new ArrayList<>();
        appUsageList.add(new AppUsage("packageA", 1000L));
        appUsageList.add(new AppUsage("packageB", 2000L));
        Observable<AppUsage> appUsages = Observable.from(appUsageList);
        when(mockStatAPI.postUsages(anyString(), any(List.class))).thenReturn(mock(Observable.class));
        subject.sendAppUsages(appUsages).subscribe(new TestSubscriber<>());

        verify(mockStatAPI).postUsages(anyString(), eq(appUsageList));
    }

    @Test
    public void sendAppUsages호출시_토큰_만료_여부를_확인한다() throws Exception {
        verifyToCheckExpiredToken(subject.sendAppUsages(Observable.just(new AppUsage("anything", 1))).toObservable());
    }

    @Test
    public void requestRecentReport_호출시__카테고리별_사용량을_요청한다() throws Exception {
        User user = new User();
        subject.requestRecentReport("GAME", user).subscribe(new TestSubscriber<>());

        verify(mockStatAPI).getRecentReport(anyString(), eq("GAME"), eq(user));
    }

    @Test
    public void requestRecentReport_호출시__토큰_만료_여부를_확인한다() throws Exception {
        verifyToCheckExpiredToken(subject.requestRecentReport("GAME", new User()));
    }
}
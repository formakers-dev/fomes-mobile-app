package com.formakers.fomes.analysis.presenter;

import android.util.Pair;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.analysis.contract.RecentAnalysisReportContract;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.vo.Rank;
import com.formakers.fomes.common.network.vo.RecentReport;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.common.network.vo.UsageGroup;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.model.User;

import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RecentAnalysisReportPresenterTest {

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;
    @Inject
    AppStatService mockAppStatService;

    @Mock RecentAnalysisReportContract.View mockView;

    @Mock User mockUser;

    RecentAnalysisReportPresenter subject;

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

        MockitoAnnotations.initMocks(this);

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockAppUsageDataHelper.getAppUsagesFor(7)).thenReturn(new ArrayList<>());
        when(mockUser.getUserId()).thenReturn("mockUserId");
        when(mockUser.getGender()).thenReturn("male");
        when(mockUser.getBirthday()).thenReturn(1989);
        when(mockUser.getJob()).thenReturn("IT종사자");

        subject = new RecentAnalysisReportPresenter(mockView, mockAppUsageDataHelper, mockAppStatService, mockUser);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void loading_호출시__게임_분석_로딩_관련_연산을_수행후__뷰를_업데이트한다() {
        when(mockAppStatService.sendAppUsages(anyList())).thenReturn(Completable.complete());

        List<Rank> totalUsedTimeRank = new ArrayList<>();
        totalUsedTimeRank.add(new Rank("user1", 1, 10000000L));
        totalUsedTimeRank.add(new Rank("mockUserId", 24, 1000000L));
        totalUsedTimeRank.add(new Rank("user3", 999, 1L));

        List<UsageGroup> usages = new ArrayList<>();

        // TODO : 리스트를 ... 다..만들어야...한다...........귀차니즘..............
        usages.add(new UsageGroup(UsageGroup.TYPE_MINE, Lists.emptyList(), Lists.emptyList(), Lists.emptyList()));
        usages.add(new UsageGroup(UsageGroup.TYPE_AGE | UsageGroup.TYPE_GENDER, Lists.emptyList(), Lists.emptyList(), Lists.emptyList()));
        usages.add(new UsageGroup(UsageGroup.TYPE_JOB, Lists.emptyList(), Lists.emptyList(), Lists.emptyList()));

        RecentReport report = new RecentReport(totalUsedTimeRank, usages);

        when(mockAppStatService.requestRecentReport(eq("GAME"), eq(mockUser))).thenReturn(Observable.just(report));

        subject.loading().subscribe();

        // 앱 누적 사용 시간 데이터 서버로 전송
        // requestPostUsages - 7일간의_앱_누적_사용시간을_서버에_전송한다
        verify(mockAppUsageDataHelper).getAppUsagesFor(eq(7));
        verify(mockAppStatService).sendAppUsages(anyList());

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockAppStatService).requestRecentReport(eq("GAME"), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getUserId()).isEqualTo("mockUserId");
        assertThat(argumentCaptor.getValue().getGender()).isEqualTo("male");
        assertThat(argumentCaptor.getValue().getBirthday()).isEqualTo(1989);
        assertThat(argumentCaptor.getValue().getJob()).isEqualTo("IT종사자");

        verify(mockView).bindMyGenreViews(eq(report.getUsages().get(0).getCategoryUsages()));

        verify(mockView).bindPeopleGenreViews(eq(report.getUsages().get(1).getCategoryUsages()),
                eq(report.getUsages().get(2).getCategoryUsages()));

        // TODO : 구현필요
//        verify(mockView).bindRankingViews(eq(report.getTotalUsedTimeRank()));
//
//        verify(mockView).bindFavoritDeveloperViews(eq(report.getUsages().get(0).getDeveloperUsages()),
//                eq(report.getUsages().get(1).getDeveloperUsages()),
//                eq(report.getUsages().get(2).getDeveloperUsages()));
//
//        verify(mockView).bindMyGames(eq(report.getUsages().get(0).getAppUsages()));
//        verify(mockView).bindPeopleGames(eq(report.getUsages().get(1).getAppUsages()),
//                eq(report.getUsages().get(2).getAppUsages()));
    }

    @Test
    public void getPercentage_호출시__전체사용량_대비_현재사용량_비율을_리턴한다() {
        List<Usage> categoryUsages = new ArrayList<>();
        categoryUsages.add(new Usage("GAME_RPG", "롤플레잉", 3000L, null));
        categoryUsages.add(new Usage("GAME_PUZZLE", "퍼즐", 2000L, null));
        categoryUsages.add(new Usage("GAME_SIMUL", "시뮬레이션", 1000L, null));
        categoryUsages.add(new Usage("GAME_ACTION", "액션", 100L, null));

        List<Pair<Usage, Integer>> usagePercentagePair = subject.getPercentage(categoryUsages, 0,3);

        assertThat(usagePercentagePair.size()).isEqualTo(3);
        assertThat(usagePercentagePair.get(0).first.getName()).isEqualTo("롤플레잉");
        assertThat(usagePercentagePair.get(0).second).isEqualTo(49);
        assertThat(usagePercentagePair.get(1).first.getName()).isEqualTo("퍼즐");
        assertThat(usagePercentagePair.get(1).second).isEqualTo(33);
        assertThat(usagePercentagePair.get(2).first.getName()).isEqualTo("시뮬레이션");
        assertThat(usagePercentagePair.get(2).second).isEqualTo(16);
    }
}
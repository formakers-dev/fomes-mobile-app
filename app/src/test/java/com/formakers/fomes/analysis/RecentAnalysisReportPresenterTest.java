package com.formakers.fomes.analysis;

import android.util.Pair;

import androidx.test.core.app.ApplicationProvider;

import com.bumptech.glide.RequestManager;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.analysis.RecentAnalysisReportContract;
import com.formakers.fomes.analysis.RecentAnalysisReportPresenter;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.vo.Rank;
import com.formakers.fomes.common.network.vo.RecentReport;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.common.network.vo.UsageGroup;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.common.helper.AppUsageDataHelper;
import com.formakers.fomes.common.model.ShortTermStat;
import com.formakers.fomes.common.model.User;

import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class RecentAnalysisReportPresenterTest {

    @Inject AppUsageDataHelper mockAppUsageDataHelper;
    @Inject AppStatService mockAppStatService;
    @Inject RequestManager mockRequestManager;
    @Inject UserDAO mockUserDAO;

    @Mock RecentAnalysisReportContract.View mockView;

    @Mock User mockUser;

    RecentReport report;
    List<ShortTermStat> shortTermStats = new ArrayList<>();

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

        ((TestFomesApplication) ApplicationProvider.getApplicationContext()).getComponent().inject(this);

        when(mockAppUsageDataHelper.getAppUsages()).thenReturn(Observable.from(new ArrayList<>()));
        when(mockUser.getNickName()).thenReturn("mockUserNickName");
        when(mockUser.getGender()).thenReturn("male");
        when(mockUser.getBirthday()).thenReturn(1989);
        when(mockUser.getJob()).thenReturn(3);
        when(mockUserDAO.getUserInfo()).thenReturn(Single.just(mockUser));

        subject = new RecentAnalysisReportPresenter(mockView, mockAppUsageDataHelper, mockAppStatService, mockRequestManager, mockUserDAO);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    private void initializeForLoadingTest() {
        when(mockAppStatService.sendAppUsages(anyList())).thenReturn(Completable.complete());

        List<Rank> totalUsedTimeRank = new ArrayList<>();
        totalUsedTimeRank.add(new Rank("user1", 1, 10000000L));
        totalUsedTimeRank.add(new Rank("user3", 999, 1L));
        totalUsedTimeRank.add(new Rank("mockUserId", 24, 1000000L));

        shortTermStats.add(new ShortTermStat("packageName1", 1000, 2000));
        shortTermStats.add(new ShortTermStat("packageName2", 2000, 3000));

        List<UsageGroup> usages = new ArrayList<>();

        // TODO : 리스트를 ... 다..만들어야...한다...........귀차니즘..............
        usages.add(new UsageGroup(UsageGroup.TYPE_MINE, Lists.emptyList(), Lists.emptyList(), Lists.emptyList()));
        usages.add(new UsageGroup(UsageGroup.TYPE_AGE | UsageGroup.TYPE_GENDER, Lists.emptyList(), Lists.emptyList(), Lists.emptyList()));
        usages.add(new UsageGroup(UsageGroup.TYPE_JOB, Lists.emptyList(), Lists.emptyList(), Lists.emptyList()));

        report = new RecentReport().setTotalUsedTimeRank(totalUsedTimeRank).setUsages(usages).setTotalUserCount(999);

        when(mockAppUsageDataHelper.getShortTermStats()).thenReturn(Observable.from(shortTermStats));
        when(mockAppStatService.requestRecentReport(eq("GAME"), eq(mockUser))).thenReturn(Observable.just(report));
        when(mockAppStatService.sendShortTermStats(any())).thenReturn(Completable.complete());
    }

    @Test
    public void loading_호출시__단기_통계_데이터를_서버로_전송한다() {
        initializeForLoadingTest();

        subject.loading().subscribe();

        verify(mockAppStatService).sendShortTermStats(eq(shortTermStats));
    }

    @Test
    public void loading_호출시__게임_분석_로딩_관련_연산을_수행후__뷰를_업데이트한다() {
        initializeForLoadingTest();

        subject.loading().subscribe();

        // 앱 누적 사용 시간 데이터 서버로 전송
        // requestPostUsages - 7일간의_앱_누적_사용시간을_서버에_전송한다
        verify(mockAppUsageDataHelper).getAppUsages();
        verify(mockAppStatService).sendAppUsages(anyList());
        verify(mockUserDAO).getUserInfo();

        // 분석 결과 요청
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockAppStatService).requestRecentReport(eq("GAME"), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getGender()).isEqualTo("male");
        assertThat(argumentCaptor.getValue().getBirthday()).isEqualTo(1989);
        assertThat(argumentCaptor.getValue().getJob()).isEqualTo(3);

        // 나의 장르 뷰 업데이트
        verify(mockView).bindMyGenreViews(eq(report.getUsages().get(0).getCategoryUsages()), eq("mockUserNickName"));

        // 사람들의 장르 뷰 업데이트
        verify(mockView).bindPeopleGenreViews(eq(report.getUsages().get(1).getCategoryUsages()),
                eq(report.getUsages().get(2).getCategoryUsages()));

        // 사용시간 랭킹 뷰 업데이트
        ArgumentCaptor<List<Rank>> ranksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Long> ranksArgumentCaptor2 = ArgumentCaptor.forClass(Long.class);
        verify(mockView).bindRankingViews(ranksArgumentCaptor.capture(), ranksArgumentCaptor2.capture());
        List<Rank> capturedList = ranksArgumentCaptor.getValue();
        assertThat(capturedList.get(0).getRank()).isEqualTo(1);
        assertThat(capturedList.get(1).getRank()).isEqualTo(24);
        assertThat(capturedList.get(2).getRank()).isEqualTo(999);
        assertThat(ranksArgumentCaptor2.getValue()).isEqualTo(999);

        verify(mockView).bindFavoriteDeveloperViews(eq(report.getUsages().get(0).getDeveloperUsages()),
                eq(report.getUsages().get(1).getDeveloperUsages()),
                eq(report.getUsages().get(2).getDeveloperUsages()));

        verify(mockView).bindMyGames(eq(report.getUsages().get(0).getAppUsages()));

        verify(mockView).bindPeopleGamesViews(eq(report.getUsages().get(1).getAppUsages()),
                eq(report.getUsages().get(2).getAppUsages()));
    }

    @Test
    public void getPercentage_호출시__전체사용량_대비_현재사용량_비율을_올림하여_리턴한다() {
        List<Usage> categoryUsages = new ArrayList<>();
        categoryUsages.add(new Usage("GAME_RPG", "롤플레잉", 3000L, null));
        categoryUsages.add(new Usage("GAME_PUZZLE", "퍼즐", 2000L, null));
        categoryUsages.add(new Usage("GAME_SIMUL", "시뮬레이션", 1000L, null));
        categoryUsages.add(new Usage("GAME_ACTION", "액션", 100L, null));

        List<Pair<Usage, Integer>> usagePercentagePair = subject.getPercentage(categoryUsages, 0,3);

        assertThat(usagePercentagePair.size()).isEqualTo(3);
        assertThat(usagePercentagePair.get(0).first.getName()).isEqualTo("롤플레잉");
        assertThat(usagePercentagePair.get(0).second).isEqualTo(50);
        assertThat(usagePercentagePair.get(1).first.getName()).isEqualTo("퍼즐");
        assertThat(usagePercentagePair.get(1).second).isEqualTo(33);
        assertThat(usagePercentagePair.get(2).first.getName()).isEqualTo("시뮬레이션");
        assertThat(usagePercentagePair.get(2).second).isEqualTo(17);
    }

    @Test
    public void getHour_호출시__시간으로_변환후_소수점_첫째까지까지_올림하여_리턴한다() {
        float hour = subject.getHour(10000000L);

        assertThat(hour).isEqualTo(2.8f);
    }
}
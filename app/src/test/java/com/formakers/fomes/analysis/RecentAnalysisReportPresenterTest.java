package com.formakers.fomes.analysis;

import android.util.Pair;

import androidx.test.core.app.ApplicationProvider;

import com.bumptech.glide.RequestManager;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.helper.AppUsageDataHelper;
import com.formakers.fomes.common.model.ShortTermStat;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.Rank;
import com.formakers.fomes.common.network.vo.RecentReport;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.common.network.vo.UsageGroup;
import com.formakers.fomes.common.repository.dao.UserDAO;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class RecentAnalysisReportPresenterTest {

    @Inject AppUsageDataHelper mockAppUsageDataHelper;
    @Inject AppStatService mockAppStatService;
    @Inject RequestManager mockRequestManager;
    @Inject UserService mockUserService;

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
        when(mockUser.getGender()).thenReturn(User.GENDER_MALE);
        when(mockUser.getBirthday()).thenReturn(1989);
        when(mockUser.getJob()).thenReturn(3);
        when(mockUserService.getUser()).thenReturn(Single.just(mockUser));

        subject = new RecentAnalysisReportPresenter(mockView, mockAppUsageDataHelper, mockAppStatService, mockRequestManager, mockUserService);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    private void initializeForLoadingTest() {
        when(mockAppStatService.sendAppUsages(any(Observable.class))).thenReturn(Completable.complete());

        List<Rank> totalUsedTimeRank = new ArrayList<>();
        totalUsedTimeRank.add(new Rank("user1", 1, 10000000L));
        totalUsedTimeRank.add(new Rank("user3", 999, 1L));
        totalUsedTimeRank.add(new Rank("mockUserId", 24, 1000000L));

        shortTermStats.add(new ShortTermStat("packageName1", 1000, 2000));
        shortTermStats.add(new ShortTermStat("packageName2", 2000, 3000));

        List<UsageGroup> usages = new ArrayList<>();

        // TODO : ???????????? ... ???..????????????...??????...........????????????..............
        usages.add(new UsageGroup(UsageGroup.TYPE_MINE, Lists.emptyList(), Lists.emptyList(), Lists.emptyList()));
        usages.add(new UsageGroup(UsageGroup.TYPE_AGE | UsageGroup.TYPE_GENDER, Lists.emptyList(), Lists.emptyList(), Lists.emptyList()));
        usages.add(new UsageGroup(UsageGroup.TYPE_JOB, Lists.emptyList(), Lists.emptyList(), Lists.emptyList()));

        report = new RecentReport().setTotalUsedTimeRank(totalUsedTimeRank).setUsages(usages).setTotalUserCount(999);

        when(mockAppUsageDataHelper.getShortTermStats()).thenReturn(Observable.from(shortTermStats));
        when(mockAppStatService.requestRecentReport(eq("GAME"), eq(mockUser))).thenReturn(Observable.just(report));
        when(mockAppStatService.sendShortTermStats(any())).thenReturn(Completable.complete());
    }

    @Test
    public void loading_?????????__??????_??????_????????????_?????????_????????????() {
        initializeForLoadingTest();

        subject.loading().subscribe();

        verify(mockAppUsageDataHelper).getShortTermStats();
        verify(mockAppStatService).sendShortTermStats(eq(mockAppUsageDataHelper.getShortTermStats()));
    }

    @Test
    public void loading_?????????__??????_??????_??????_??????_?????????_?????????__??????_??????????????????() {
        initializeForLoadingTest();

        subject.loading().subscribe();

        // ??? ?????? ?????? ?????? ????????? ????????? ??????
        // requestPostUsages - 7?????????_???_??????_???????????????_?????????_????????????
        verify(mockAppUsageDataHelper).getAppUsages();
        verify(mockAppStatService).sendAppUsages(eq(mockAppUsageDataHelper.getAppUsages()));
        verify(mockUserService).getUser();

        // ?????? ?????? ??????
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockAppStatService).requestRecentReport(eq("GAME"), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getGender()).isEqualTo(User.GENDER_MALE);
        assertThat(argumentCaptor.getValue().getBirthday()).isEqualTo(1989);
        assertThat(argumentCaptor.getValue().getJob()).isEqualTo(3);

        // ?????? ?????? ??? ????????????
        verify(mockView).bindMyGenreViews(eq(report.getUsages().get(0).getCategoryUsages()), eq("mockUserNickName"));

        // ???????????? ?????? ??? ????????????
        verify(mockView).bindPeopleGenreViews(eq(report.getUsages().get(1).getCategoryUsages()),
                eq(report.getUsages().get(2).getCategoryUsages()));

        // ???????????? ?????? ??? ????????????
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
    public void getPercentage_?????????__???????????????_??????_???????????????_?????????_????????????_????????????() {
        List<Usage> categoryUsages = new ArrayList<>();
        categoryUsages.add(new Usage("GAME_RPG", "????????????", 3000L, null));
        categoryUsages.add(new Usage("GAME_PUZZLE", "??????", 2000L, null));
        categoryUsages.add(new Usage("GAME_SIMUL", "???????????????", 1000L, null));
        categoryUsages.add(new Usage("GAME_ACTION", "??????", 100L, null));

        List<Pair<Usage, Integer>> usagePercentagePair = subject.getPercentage(categoryUsages, 0,3);

        assertThat(usagePercentagePair.size()).isEqualTo(3);
        assertThat(usagePercentagePair.get(0).first.getName()).isEqualTo("????????????");
        assertThat(usagePercentagePair.get(0).second).isEqualTo(50);
        assertThat(usagePercentagePair.get(1).first.getName()).isEqualTo("??????");
        assertThat(usagePercentagePair.get(1).second).isEqualTo(33);
        assertThat(usagePercentagePair.get(2).first.getName()).isEqualTo("???????????????");
        assertThat(usagePercentagePair.get(2).second).isEqualTo(17);
    }

    @Test
    public void getHour_?????????__????????????_?????????_?????????_??????????????????_????????????_????????????() {
        float hour = subject.getHour(10000000L);

        assertThat(hour).isEqualTo(2.8f);
    }
}
package com.formakers.fomes.analysis.presenter;

import android.util.Pair;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.analysis.contract.CurrentAnalysisReportContract;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.model.CategoryUsage;
import com.formakers.fomes.common.network.AppStatService;
import com.formakers.fomes.common.network.api.StatAPI;

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
import java.util.Map;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CurrentAnalysisReportPresenterTest {

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;
    @Inject
    AppStatService mockAppStatService;

    @Mock CurrentAnalysisReportContract.View mockView;

    CurrentAnalysisReportPresenter subject;

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

        subject = new CurrentAnalysisReportPresenter(mockView, mockAppUsageDataHelper, mockAppStatService);
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void loadingForAnalysis_호출시__분석_로딩_관련_연산을_수행후__뷰를_업데이트한다() {
        when(mockAppStatService.sendAppUsages(anyList())).thenReturn(Completable.complete());

        List<CategoryUsage> categoryUsages = new ArrayList<>();
        when(mockAppStatService.requestCategoryUsage()).thenReturn(Observable.just(categoryUsages));

        List<CategoryUsage> peopleCategoryUsages_gender_age = new ArrayList<>();
        peopleCategoryUsages_gender_age.add(new CategoryUsage("GAME_RPG", "롤플레잉", 3000));
        when(mockAppStatService.requestPeopleCategoryUsage(StatAPI.PeopleGroupFilter.GENDER_AND_AGE)).thenReturn(Observable.just(peopleCategoryUsages_gender_age));
        List<CategoryUsage> peopleCategoryUsages2_job = new ArrayList<>();
        peopleCategoryUsages2_job.add(new CategoryUsage("GAME_RPG_2", "롤플레잉2", 2000));
        when(mockAppStatService.requestPeopleCategoryUsage(StatAPI.PeopleGroupFilter.JOB)).thenReturn(Observable.just(peopleCategoryUsages2_job));

        subject.loading().subscribe();

        // 앱 누적 사용 시간 데이터 서버로 전송
        // requestPostUsages - 7일간의_앱_누적_사용시간을_서버에_전송한다
        verify(mockAppUsageDataHelper).getAppUsagesFor(eq(7));
        verify(mockAppStatService).sendAppUsages(anyList());

        // 마이 장르
        // requestMostUsedGameGenres - 카테고리별 사용시간 정렬된 리스트
        verify(mockAppStatService).requestCategoryUsage();
        verify(mockView).bindMyGenreViews(eq(categoryUsages));

        // 사람들 장르
        // requestPeopleCategoryUsage - 사람들의 카테고리별 사용시간 정렬된 리스트
        ArgumentCaptor<Map> argumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockAppStatService, times(2)).requestPeopleCategoryUsage(anyString());
        verify(mockView).bindPeopleGenreViews(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().get(StatAPI.PeopleGroupFilter.GENDER_AND_AGE)).isEqualTo(peopleCategoryUsages_gender_age);
        assertThat(argumentCaptor.getValue().get(StatAPI.PeopleGroupFilter.JOB)).isEqualTo(peopleCategoryUsages2_job);
    }

    @Test
    public void getPercentage_호출시__전체사용량_대비_현재사용량_비율을_리턴한다() {
        List<CategoryUsage> categoryUsages = new ArrayList<>();
        categoryUsages.add(new CategoryUsage("GAME_RPG", "롤플레잉", 3000));
        categoryUsages.add(new CategoryUsage("GAME_PUZZLE", "퍼즐", 2000));
        categoryUsages.add(new CategoryUsage("GAME_SIMUL", "시뮬레이션", 1000));
        categoryUsages.add(new CategoryUsage("GAME_ACTION", "액션", 100));

        List<Pair<CategoryUsage, Integer>> usagePercentagePair = subject.getPercentage(categoryUsages, 0,3);

        assertThat(usagePercentagePair.size()).isEqualTo(3);
        assertThat(usagePercentagePair.get(0).first.getCategoryName()).isEqualTo("롤플레잉");
        assertThat(usagePercentagePair.get(0).second).isEqualTo(49);
        assertThat(usagePercentagePair.get(1).first.getCategoryName()).isEqualTo("퍼즐");
        assertThat(usagePercentagePair.get(1).second).isEqualTo(33);
        assertThat(usagePercentagePair.get(2).first.getCategoryName()).isEqualTo("시뮬레이션");
        assertThat(usagePercentagePair.get(2).second).isEqualTo(16);
    }
}
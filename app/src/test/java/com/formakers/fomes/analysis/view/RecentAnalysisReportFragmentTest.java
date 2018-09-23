package com.formakers.fomes.analysis.view;

import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.contract.RecentAnalysisReportContract;
import com.formakers.fomes.common.network.vo.Rank;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.model.AppInfo;
import com.formakers.fomes.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import java.util.ArrayList;
import java.util.List;

import rx.Completable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RecentAnalysisReportFragmentTest {

    ShadowRecentAnalysisReportFragment subject;
    SupportFragmentController<RecentAnalysisReportFragment> controller;
    @Mock RecentAnalysisReportContract.Presenter mockPresenter;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        MockitoAnnotations.initMocks(this);

        subject = new ShadowRecentAnalysisReportFragment();
        subject.presenter = mockPresenter;
        controller = SupportFragmentController.of(subject);

        when(mockPresenter.loading()).thenReturn(Completable.never());
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void RecentAnalysisReportFragment_시작시__분석_로딩_화면이_나타난다() {
        controller.create().start().resume().visible();

        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.current_analysis_loading_layout).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_error_layout).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void 로딩_화면이_나타난_후__로딩_작업을_요청한다() {
        controller.create().start().resume().visible();

        verify(mockPresenter).loading();
    }

    @Test
    public void 로딩_작업_실패시__화면을_모두_없애고_에러화면만_보여준다() {
        when(mockPresenter.loading()).thenReturn(Completable.error(new Throwable()));

        controller.create().start().resume().visible();

        assertThat(subject.getView().findViewById(R.id.current_analysis_loading_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_error_layout).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void 로딩_작업_성공시__분석_결과_화면을_보여준다() {
        when(mockPresenter.loading()).thenReturn(Completable.complete());

        controller.create().start().resume().visible();

        assertThat(subject.getView().findViewById(R.id.current_analysis_loading_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_layout).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_error_layout).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void bindMyGenreViews_호출시__분석화면의_장르레이아웃에_데이터를_셋팅한다() {
        List<Usage> categoryUsages = new ArrayList<>();
        categoryUsages.add(new Usage("GAME_RPG", "롤플레잉", 3000L, null));
        categoryUsages.add(new Usage("GAME_PUZZLE", "퍼즐", 2000L, null));
        categoryUsages.add(new Usage("GAME_SIMUL", "시뮬레이션", 1000L, null));
        categoryUsages.add(new Usage("GAME_ACTION", "액션", 100L, null));

        List<Pair<Usage, Integer>> usagePercentagePair = new ArrayList<>();
        usagePercentagePair.add(new Pair<>(categoryUsages.get(0), 49));
        usagePercentagePair.add(new Pair<>(categoryUsages.get(1), 33));
        usagePercentagePair.add(new Pair<>(categoryUsages.get(2), 16));

        when(mockPresenter.getPercentage(anyList(), anyInt(), anyInt())).thenReturn(usagePercentagePair);

        controller.create().start().resume().visible();

        subject.bindMyGenreViews(categoryUsages);

        // 상위 3개만 계산해온다
        verify(mockPresenter).getPercentage(eq(categoryUsages), eq(0), eq(3));

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_genre_1)
                .findViewById(R.id.name_textview)).getText()).isEqualTo("롤플레잉");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_genre_1)
                .findViewById(R.id.used_time_textview)).getText()).isEqualTo("총 시간의 49%");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_genre_2)
                .findViewById(R.id.name_textview)).getText()).isEqualTo("퍼즐");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_genre_2)
                .findViewById(R.id.used_time_textview)).getText()).isEqualTo("총 시간의 33%");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_genre_3)
                .findViewById(R.id.name_textview)).getText()).isEqualTo("시뮬레이션");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_genre_3)
                .findViewById(R.id.used_time_textview)).getText()).isEqualTo("총 시간의 16%");
    }

    @Test
    public void bindPeopleGenreViews_호출시__분석화면의_사람들_장르레이아웃에_데이터를_셋팅한다() {
        List<Usage> categoryUsages_gender_age = new ArrayList<>();
        categoryUsages_gender_age.add(new Usage("GAME_RPG", "롤플레잉", 3000L, null));
        categoryUsages_gender_age.add(new Usage("GAME_PUZZLE", "퍼즐", 2000L, null));
        categoryUsages_gender_age.add(new Usage("GAME_SIMUL", "시뮬레이션", 1000L, null));
        categoryUsages_gender_age.add(new Usage("GAME_ACTION", "액션", 100L, null));

        List<Usage> categoryUsages_job = new ArrayList<>();
        categoryUsages_job.add(new Usage("GAME_QUIZ", "퀴즈", 4000L, null));
        categoryUsages_job.add(new Usage("GAME_CASUAL", "캐주얼", 3000L, null));
        categoryUsages_job.add(new Usage("GAME_PUZZLE", "퍼즐", 2000L, null));
        categoryUsages_job.add(new Usage("GAME_ACTION", "액션", 1000L, null));

        List<Pair<Usage, Integer>> usagePercentagePair_gender_age = new ArrayList<>();
        usagePercentagePair_gender_age.add(new Pair<>(categoryUsages_gender_age.get(0), 49));
        usagePercentagePair_gender_age.add(new Pair<>(categoryUsages_gender_age.get(1), 33));
        usagePercentagePair_gender_age.add(new Pair<>(categoryUsages_gender_age.get(2), 16));

        List<Pair<Usage, Integer>> usagePercentagePair_job = new ArrayList<>();
        usagePercentagePair_job.add(new Pair<>(categoryUsages_job.get(0), 49));
        usagePercentagePair_job.add(new Pair<>(categoryUsages_job.get(1), 33));
        usagePercentagePair_job.add(new Pair<>(categoryUsages_job.get(2), 16));

        when(mockPresenter.getPercentage(eq(categoryUsages_gender_age), anyInt(), anyInt())).thenReturn(usagePercentagePair_gender_age);
        when(mockPresenter.getPercentage(eq(categoryUsages_job), anyInt(), anyInt())).thenReturn(usagePercentagePair_job);
        when(mockPresenter.getUserInfo()).thenReturn(new User().setBirthday(1991).setGender("female").setJob("IT"));

        controller.create().start().resume().visible();

        subject.bindPeopleGenreViews(categoryUsages_gender_age, categoryUsages_job);

        // 상위 3개만 계산해온다
        verify(mockPresenter).getPercentage(eq(categoryUsages_gender_age), eq(0), eq(3));
        verify(mockPresenter).getPercentage(eq(categoryUsages_job), eq(0), eq(3));

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_gender_age)
                .findViewById(R.id.demographic_title)).getText()).isEqualTo("20대\n여성");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_gender_age)
                .findViewById(R.id.demographic_name_1)).getText()).isEqualTo("롤플레잉");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_gender_age)
                .findViewById(R.id.demographic_name_2)).getText()).isEqualTo("퍼즐");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_gender_age)
                .findViewById(R.id.demographic_name_3)).getText()).isEqualTo("시뮬레이션");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_job)
                .findViewById(R.id.demographic_title)).getText()).isEqualTo("IT");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_job)
                .findViewById(R.id.demographic_name_1)).getText()).isEqualTo("퀴즈");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_job)
                .findViewById(R.id.demographic_name_2)).getText()).isEqualTo("캐주얼");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_job)
                .findViewById(R.id.demographic_name_3)).getText()).isEqualTo("퍼즐");
    }

    @Test
    public void bindRankingViews_호출시__분석화면의_사용시간_랭킹_레이아웃에_데이터를_셋팅한다() {
        List<Rank> rankList = new ArrayList<>();
        rankList.add(new Rank("bestUserId", 1, 10000000L));
        rankList.add(new Rank("myUserId", 4, 5000000L));
        rankList.add(new Rank("worstUserId", 100, 1000000L));

        controller.create().start().resume().visible();

        subject.bindRankingViews(rankList);

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_playtime_rank_best)
                .findViewById(R.id.rank_number)).getText()).isEqualTo("1등");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_playtime_rank_mine)
                .findViewById(R.id.rank_number)).getText()).isEqualTo("4등");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_playtime_rank_worst)
                .findViewById(R.id.rank_number)).getText()).isEqualTo("꼴등");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_playtime_rank_best)
                .findViewById(R.id.rank_content)).getText()).isEqualTo("2.8\n시간");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_playtime_rank_mine)
                .findViewById(R.id.rank_content)).getText()).isEqualTo("1.4\n시간");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_playtime_rank_worst)
                .findViewById(R.id.rank_content)).getText()).isEqualTo("0.3\n시간");
    }

    @Test
    public void bindFavoriteDeveloperViews_호출시__분석화면의_최애개발사_레이아웃에_데이터를_셋팅한다() {
        List<Usage> mine = new ArrayList<>();
        List<Usage> genderAge = new ArrayList<>();
        List<Usage> job = new ArrayList<>();

        List<AppInfo> appInfos = new ArrayList<>();
        appInfos.add(new AppInfo("pk1", "블루홀게임").setTotalUsedTime(9000L));
        appInfos.add(new AppInfo("pk2", "블루홀게임2").setTotalUsedTime(999L));
        mine.add(new Usage("블루홀", "블루홀", 9999L, appInfos));
        mine.add(new Usage("노잼", "노잼", 99L, appInfos));

        List<AppInfo> appInfos2 = new ArrayList<>();
        appInfos2.add(new AppInfo("pk1", "블리자드게임").setTotalUsedTime(9000L));
        appInfos2.add(new AppInfo("pk2", "블리자드게임2").setTotalUsedTime(999L));
        genderAge.add(new Usage("블리자드", "블리자드", 9999L, appInfos2));
        genderAge.add(new Usage("노잼", "노잼", 99L, appInfos2));

        List<AppInfo> appInfos3 = new ArrayList<>();
        appInfos3.add(new AppInfo("pk1", "순순디자인게임").setTotalUsedTime(9000L));
        appInfos3.add(new AppInfo("pk2", "순순디자인게임2").setTotalUsedTime(999L));
        job.add(new Usage("순순디자인", "순순디자인", 9999L, appInfos3));
        job.add(new Usage("노잼", "노잼", 99L, appInfos3));

        when(mockPresenter.getUserInfo()).thenReturn(new User().setBirthday(1991).setGender("female").setJob("IT"));

        controller.create().start().resume().visible();

        subject.bindFavoriteDeveloperViews(mine, genderAge, job);

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_favorite_developer)
                .findViewById(R.id.group)).getText()).isEqualTo("나의 1위");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_gender_age_favorite_developer)
                .findViewById(R.id.group)).getText()).isEqualTo("20대 여성 1위");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_job_favorite_developer)
                .findViewById(R.id.group)).getText()).isEqualTo("IT 1위");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_favorite_developer)
                .findViewById(R.id.developer_name)).getText()).isEqualTo("블루홀");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_gender_age_favorite_developer)
                .findViewById(R.id.developer_name)).getText()).isEqualTo("블리자드");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_job_favorite_developer)
                .findViewById(R.id.developer_name)).getText()).isEqualTo("순순디자인");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_favorite_developer)
                .findViewById(R.id.developer_description)).getText()).isEqualTo("블루홀게임\n게임 개발사");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_gender_age_favorite_developer)
                .findViewById(R.id.developer_description)).getText()).isEqualTo("블리자드게임\n게임 개발사");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_job_favorite_developer)
                .findViewById(R.id.developer_description)).getText()).isEqualTo("순순디자인게임\n게임 개발사");
    }

    @Test
    public void 확인_버튼_클릭시__화면을_종료한다() {
        controller.create().start().resume().visible();

        subject.getView().findViewById(R.id.current_analysis_exit_button).performClick();

        assertThat(subject.getActivity().isFinishing()).isTrue();
    }

    public static class ShadowRecentAnalysisReportFragment extends RecentAnalysisReportFragment {
        @Override
        public void setPresenter(RecentAnalysisReportContract.Presenter presenter) { }
    }
}
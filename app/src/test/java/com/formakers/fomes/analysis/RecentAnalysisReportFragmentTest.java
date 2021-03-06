package com.formakers.fomes.analysis;

import android.content.Intent;
import android.util.Pair;
import android.util.SupportFragmentController;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.formakers.fomes.R;
import com.formakers.fomes.common.model.AppInfo;
import com.formakers.fomes.common.model.User;
import com.formakers.fomes.common.network.vo.Rank;
import com.formakers.fomes.common.network.vo.Usage;
import com.formakers.fomes.common.view.custom.RankAppItemView;
import com.formakers.fomes.main.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;
import rx.Completable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class RecentAnalysisReportFragmentTest {

    ShadowRecentAnalysisReportFragment subject;
    SupportFragmentController<RecentAnalysisReportFragment> controller;
    @Mock RecentAnalysisReportContract.Presenter mockPresenter;

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

        MockitoAnnotations.initMocks(this);

        subject = new ShadowRecentAnalysisReportFragment();
        subject.presenter = mockPresenter;
        controller = SupportFragmentController.of(subject);

        when(mockPresenter.getImageLoader()).thenReturn(Glide.with(RuntimeEnvironment.application));
        when(mockPresenter.loading()).thenReturn(Completable.never());
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }

    @Test
    public void RecentAnalysisReportFragment_?????????__??????_??????_?????????_????????????() {
        controller.create().start().resume();

        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.current_analysis_loading_layout).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_error_layout).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void ??????_?????????_?????????_???__??????_?????????_????????????() {
        controller.create().start().resume().visible();

        verify(mockPresenter).loading();
    }

    @Test
    public void ??????_??????_?????????__?????????_??????_?????????_???????????????_????????????() {
        when(mockPresenter.loading()).thenReturn(Completable.error(new Throwable()));

        controller.create().start().resume().visible();

        assertThat(subject.getView().findViewById(R.id.current_analysis_loading_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_error_layout).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void ??????_??????_?????????__??????_??????_?????????_????????????() {
        when(mockPresenter.loading()).thenReturn(Completable.complete());

        controller.create().start().resume().visible();

        assertThat(subject.getView().findViewById(R.id.current_analysis_loading_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_layout).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_error_layout).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void bindMyGenreViews_?????????__???????????????_?????????????????????_????????????_????????????() {
        List<Usage> categoryUsages = new ArrayList<>();
        categoryUsages.add(new Usage("GAME_RPG", "????????????", 3000L, null));
        categoryUsages.add(new Usage("GAME_PUZZLE", "??????", 2000L, null));
        categoryUsages.add(new Usage("GAME_SIMUL", "???????????????", 1000L, null));
        categoryUsages.add(new Usage("GAME_ACTION", "??????", 100L, null));

        List<Pair<Usage, Integer>> usagePercentagePair = new ArrayList<>();
        usagePercentagePair.add(new Pair<>(categoryUsages.get(0), 49));
        usagePercentagePair.add(new Pair<>(categoryUsages.get(1), 33));
        usagePercentagePair.add(new Pair<>(categoryUsages.get(2), 16));

        when(mockPresenter.getPercentage(anyList(), anyInt(), anyInt())).thenReturn(usagePercentagePair);

        controller.create().start().resume().visible();

        subject.bindMyGenreViews(categoryUsages, "nickName");

        // ?????? 3?????? ???????????????
        verify(mockPresenter).getPercentage(eq(categoryUsages), eq(0), eq(3));

        assertThat(((RankAppItemView) subject.getView().findViewById(R.id.analysis_my_genre_1)).getTitleText())
                .isEqualTo("????????????");
        assertThat(((RankAppItemView) subject.getView().findViewById(R.id.analysis_my_genre_1)).getDescriptionText())
                .isEqualTo("??? ????????? 49%");

        assertThat(((RankAppItemView) subject.getView().findViewById(R.id.analysis_my_genre_2)).getTitleText())
                .isEqualTo("??????");
        assertThat(((RankAppItemView) subject.getView().findViewById(R.id.analysis_my_genre_2)).getDescriptionText())
                .isEqualTo("??? ????????? 33%");

        assertThat(((RankAppItemView) subject.getView().findViewById(R.id.analysis_my_genre_3)).getTitleText())
                .isEqualTo("???????????????");
        assertThat(((RankAppItemView) subject.getView().findViewById(R.id.analysis_my_genre_3)).getDescriptionText())
                .isEqualTo("??? ????????? 16%");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_genre_chart_text)).getText())
                .contains("nickName");
    }

    @Test
    public void bindPeopleGenreViews_?????????__???????????????_?????????_?????????????????????_????????????_????????????() {
        List<Usage> categoryUsages_gender_age = new ArrayList<>();
        categoryUsages_gender_age.add(new Usage("GAME_RPG", "????????????", 3000L, null));
        categoryUsages_gender_age.add(new Usage("GAME_PUZZLE", "??????", 2000L, null));
        categoryUsages_gender_age.add(new Usage("GAME_SIMUL", "???????????????", 1000L, null));
        categoryUsages_gender_age.add(new Usage("GAME_ACTION", "??????", 100L, null));

        List<Usage> categoryUsages_job = new ArrayList<>();
        categoryUsages_job.add(new Usage("GAME_QUIZ", "??????", 4000L, null));
        categoryUsages_job.add(new Usage("GAME_CASUAL", "?????????", 3000L, null));
        categoryUsages_job.add(new Usage("GAME_PUZZLE", "??????", 2000L, null));
        categoryUsages_job.add(new Usage("GAME_ACTION", "??????", 1000L, null));

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
        when(mockPresenter.getUserInfo()).thenReturn(new User().setBirthday(1991).setGender(User.GENDER_FEMALE).setJob(2001));

        controller.create().start().resume().visible();

        subject.bindPeopleGenreViews(categoryUsages_gender_age, categoryUsages_job);

        // ?????? 3?????? ???????????????
        verify(mockPresenter).getPercentage(eq(categoryUsages_gender_age), eq(0), eq(3));
        verify(mockPresenter).getPercentage(eq(categoryUsages_job), eq(0), eq(3));

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_gender_age)
                .findViewById(R.id.group)).getText()).isEqualTo("20???\n??????");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_gender_age)
                .findViewById(R.id.title_1)).getText()).isEqualTo("????????????");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_gender_age)
                .findViewById(R.id.title_2)).getText()).isEqualTo("??????");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_gender_age)
                .findViewById(R.id.title_3)).getText()).isEqualTo("???????????????");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_job)
                .findViewById(R.id.group)).getText()).isEqualTo("IT ?????????");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_job)
                .findViewById(R.id.title_1)).getText()).isEqualTo("??????");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_job)
                .findViewById(R.id.title_2)).getText()).isEqualTo("?????????");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_people_genre_job)
                .findViewById(R.id.title_3)).getText()).isEqualTo("??????");
    }

    @Test
    public void bindRankingViews_?????????__???????????????_????????????_??????_???????????????_????????????_????????????() {
        List<Rank> rankList = new ArrayList<>();
        rankList.add(new Rank("bestUserId", 1, 10000000L));
        rankList.add(new Rank("myUserId", 4, 5000000L));
        rankList.add(new Rank("worstUserId", 100, 1000000L));

        long totalUserCount = 100;

        when(mockPresenter.getHour(10000000L)).thenReturn(2.8f);
        when(mockPresenter.getHour(5000000L)).thenReturn(1.4f);
        when(mockPresenter.getHour(1000000L)).thenReturn(0.3f);

        controller.create().start().resume().visible();

        subject.bindRankingViews(rankList, totalUserCount);
    }

    @Test
    public void bindFavoriteDeveloperViews_?????????__???????????????_???????????????_???????????????_????????????_????????????() {
        List<Usage> mine = new ArrayList<>();
        List<Usage> genderAge = new ArrayList<>();
        List<Usage> job = new ArrayList<>();

        List<AppInfo> appInfos = new ArrayList<>();
        appInfos.add(new AppInfo("pk1", "???????????????").setTotalUsedTime(9000L));
        appInfos.add(new AppInfo("pk2", "???????????????2").setTotalUsedTime(999L));
        mine.add(new Usage("?????????", "?????????", 9999L, appInfos));
        mine.add(new Usage("??????", "??????", 99L, appInfos));

        List<AppInfo> appInfos2 = new ArrayList<>();
        appInfos2.add(new AppInfo("pk1", "??????????????????").setTotalUsedTime(9000L));
        appInfos2.add(new AppInfo("pk2", "??????????????????2").setTotalUsedTime(999L));
        genderAge.add(new Usage("????????????", "????????????", 9999L, appInfos2));
        genderAge.add(new Usage("??????", "??????", 99L, appInfos2));

        List<AppInfo> appInfos3 = new ArrayList<>();
        appInfos3.add(new AppInfo("pk1", "?????????????????????").setTotalUsedTime(9000L));
        appInfos3.add(new AppInfo("pk2", "?????????????????????2").setTotalUsedTime(999L));
        job.add(new Usage("???????????????", "???????????????", 9999L, appInfos3));
        job.add(new Usage("??????", "??????", 99L, appInfos3));

        when(mockPresenter.getUserInfo()).thenReturn(new User().setBirthday(1991).setGender(User.GENDER_FEMALE).setJob(2001));

        controller.create().start().resume().visible();

        subject.bindFavoriteDeveloperViews(mine, genderAge, job);

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_favorite_developer)
                .findViewById(R.id.group)).getText()).isEqualTo("?????? 1???");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_gender_age_favorite_developer)
                .findViewById(R.id.group)).getText()).isEqualTo("20??? ?????? 1???");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_job_favorite_developer)
                .findViewById(R.id.group)).getText()).isEqualTo("IT ????????? 1???");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_favorite_developer)
                .findViewById(R.id.developer_name)).getText()).isEqualTo("?????????");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_gender_age_favorite_developer)
                .findViewById(R.id.developer_name)).getText()).isEqualTo("????????????");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_job_favorite_developer)
                .findViewById(R.id.developer_name)).getText()).isEqualTo("???????????????");

        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_my_favorite_developer)
                .findViewById(R.id.developer_description)).getText()).isEqualTo("???????????????");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_gender_age_favorite_developer)
                .findViewById(R.id.developer_description)).getText()).isEqualTo("??????????????????");
        assertThat(((TextView) subject.getView().findViewById(R.id.analysis_job_favorite_developer)
                .findViewById(R.id.developer_description)).getText()).isEqualTo("?????????????????????");
    }

    @Test
    public void bindMyGames_?????????__???????????????_??????_??????_??????_??????_????????????() {
        List<Usage> appUsages = new ArrayList<>();
        appUsages.add(new Usage("com.login_logo.rgp", "?????????????????????", 10000000L, Collections.singletonList(new AppInfo("com.login_logo.rgp", "?????????????????????", null, null, null, null, "rpgIconUrl"))));
        appUsages.add(new Usage("com.login_logo.puzzle", "???????????????", 5000000L, Collections.singletonList(new AppInfo("com.login_logo.puzzle", "???????????????", null, null, null, null, "puzzleIconUrl"))));
        appUsages.add(new Usage("com.login_logo.simulation", "????????????????????????", 1000000L, Collections.singletonList(new AppInfo("com.login_logo.simulation", "????????????????????????", null, null, null, null, "simulIconUrl"))));
        appUsages.add(new Usage("com.login_logo.action", "???????????????", 100L, Collections.singletonList(new AppInfo("com.login_logo.action", "???????????????", null, null, null, null, "actionIconUrl"))));

        when(mockPresenter.getHour(10000000L)).thenReturn(2.8f);
        when(mockPresenter.getHour(5000000L)).thenReturn(1.4f);
        when(mockPresenter.getHour(1000000L)).thenReturn(0.3f);

        RequestManager mockRequestManager = mock(RequestManager.class);
        when(mockPresenter.getImageLoader()).thenReturn(mockRequestManager);
        RequestBuilder mockRequestBuilder = mock(RequestBuilder.class);
        when(mockRequestManager.load(java.util.Optional.ofNullable(ArgumentMatchers.any()))).thenReturn(mockRequestBuilder);

        controller.create().start().resume().visible();

        subject.bindMyGames(appUsages);

        // ?????? 3?????? ???????????????

        RankAppItemView game1 = subject.getView().findViewById(R.id.analysis_my_games_1);
        assertThat(game1.getTitleText()).isEqualTo("?????????????????????");
        assertThat(game1.getDescriptionText()).isEqualTo("2.8?????? ?????????");

        RankAppItemView game2 = subject.getView().findViewById(R.id.analysis_my_games_2);
        assertThat(game2.getTitleText()).isEqualTo("???????????????");
        assertThat(game2.getDescriptionText()).isEqualTo("1.4?????? ?????????");

        RankAppItemView game3 = subject.getView().findViewById(R.id.analysis_my_games_3);
        assertThat(game3.getTitleText()).isEqualTo("????????????????????????");
        assertThat(game3.getDescriptionText()).isEqualTo("0.3?????? ?????????");
    }

    @Ignore
    @Test
    public void bindPeopleGamesViews_?????????__???????????????_????????????_??????_??????_??????_????????????() {
        List<Usage> appUsages = new ArrayList<>();
        appUsages.add(new Usage("com.login_logo.rgp", "?????????????????????", 10000000L, Collections.singletonList(new AppInfo("com.login_logo.rgp", "?????????????????????", null, null, null, null, "rpgIconUrl"))));
        appUsages.add(new Usage("com.login_logo.puzzle", "???????????????", 5000000L, Collections.singletonList(new AppInfo("com.login_logo.puzzle", "???????????????", null, null, null, null, "puzzleIconUrl"))));
        appUsages.add(new Usage("com.login_logo.simulation", "????????????????????????", 1000000L, Collections.singletonList(new AppInfo("com.login_logo.simulation", "????????????????????????", null, null, null, null, "simulIconUrl"))));
        appUsages.add(new Usage("com.login_logo.action", "???????????????", 100L, Collections.singletonList(new AppInfo("com.login_logo.action", "???????????????", null, null, null, null, "actionIconUrl"))));

        List<Usage> appUsages2 = new ArrayList<>();
        appUsages2.add(new Usage("com.login_logo.action", "???????????????", 10000000L, Collections.singletonList(new AppInfo("com.login_logo.action", "???????????????", null, null, null, null, "actionIconUrl"))));
        appUsages2.add(new Usage("com.login_logo.edu", "???????????????", 5000000L, Collections.singletonList(new AppInfo("com.login_logo.simulation", "????????????????????????", null, null, null, null, "eduIconUrl"))));
        appUsages2.add(new Usage("com.login_logo.acade", "?????????????????????", 1000000L, Collections.singletonList(new AppInfo("com.login_logo.puzzle", "???????????????", null, null, null, null, "acadeIconUrl"))));
        appUsages2.add(new Usage("com.login_logo.rgp", "?????????????????????", 100L, Collections.singletonList(new AppInfo("com.login_logo.rgp", "?????????????????????", null, null, null, null, "rpgIconUrl"))));

        RequestManager mockRequestManager = mock(RequestManager.class);
        when(mockPresenter.getImageLoader()).thenReturn(mockRequestManager);
        RequestBuilder mockRequestBuilder = mock(RequestBuilder.class);
        when(mockRequestManager.load(java.util.Optional.ofNullable(any()))).thenReturn(mockRequestBuilder);

        when(mockPresenter.getUserInfo()).thenReturn(new User().setBirthday(1991).setGender(User.GENDER_FEMALE).setJob(2001));

        controller.create().start().resume().visible();

        subject.bindPeopleGamesViews(appUsages, appUsages2);

        View genderAgeGamesView = subject.getView().findViewById(R.id.analysis_people_games_gender_age);

        assertThat(((TextView) genderAgeGamesView.findViewById(R.id.group)).getText())
                .isEqualTo("20???\n??????");

        assertThat(((TextView) genderAgeGamesView.findViewById(R.id.title_1)).getText())
                .isEqualTo("?????????????????????");
        verify(mockRequestManager).load("rpgIconUrl");
        verify(mockRequestBuilder).into(eq(((ImageView) genderAgeGamesView.findViewById(R.id.icon_1))));
        assertThat(((TextView) genderAgeGamesView.findViewById(R.id.title_2)).getText())
                .isEqualTo("???????????????");
        verify(mockRequestManager).load("puzzleIconUrl");
        verify(mockRequestBuilder).into(eq(((ImageView) genderAgeGamesView.findViewById(R.id.icon_2))));
        assertThat(((TextView) genderAgeGamesView.findViewById(R.id.title_3)).getText())
                .isEqualTo("????????????????????????");
        verify(mockRequestManager).load("simulIconUrl");
        verify(mockRequestBuilder).into(eq(((ImageView) genderAgeGamesView.findViewById(R.id.icon_3))));

        View jobGamesView = subject.getView().findViewById(R.id.analysis_people_games_job);

        assertThat(((TextView) jobGamesView.findViewById(R.id.group)).getText())
                .isEqualTo("IT ?????????");

        assertThat(((TextView) jobGamesView.findViewById(R.id.title_1)).getText())
                .isEqualTo("???????????????");
        verify(mockRequestManager).load("actionIconUrl");
        verify(mockRequestBuilder).into(eq(((ImageView) jobGamesView.findViewById(R.id.icon_1))));
        assertThat(((TextView) jobGamesView.findViewById(R.id.title_2)).getText())
                .isEqualTo("???????????????");
        verify(mockRequestManager).load("eduIconUrl");
        verify(mockRequestBuilder).into(eq(((ImageView) jobGamesView.findViewById(R.id.icon_2))));
        assertThat(((TextView) jobGamesView.findViewById(R.id.title_3)).getText())
                .isEqualTo("?????????????????????");
        verify(mockRequestManager).load("acadeIconUrl");
        verify(mockRequestBuilder).into(eq(((ImageView) jobGamesView.findViewById(R.id.icon_2))));
    }

    @Test
    public void ??????_??????_?????????__??????????????????_?????????_?????????_????????????() {
        controller.create().start().resume().visible();

        subject.getView().findViewById(R.id.current_analysis_exit_button).performClick();

        Intent intent = shadowOf(subject.getActivity()).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(MainActivity.class.getSimpleName());
        assertThat(subject.getActivity().isFinishing()).isTrue();
    }

    public static class ShadowRecentAnalysisReportFragment extends RecentAnalysisReportFragment {
        @Override
        protected void injectDependency() { }
    }
}
package com.formakers.fomes.analysis.view;

import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.contract.CurrentAnalysisReportContract;
import com.formakers.fomes.model.CategoryUsage;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CurrentAnalysisReportFragmentTest {

    ShadowCurrentAnalysisReportFragment subject;
    SupportFragmentController<CurrentAnalysisReportFragment> controller;
    @Mock CurrentAnalysisReportContract.Presenter mockPresenter;

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

        subject = new ShadowCurrentAnalysisReportFragment();
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
    public void CurrentAnalysisReportFragment_시작시__분석_로딩_화면이_나타난다() {
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
    public void bindMyGenreViews_호출시__분석화면의_장르레이아웃에_데이터를_셋팅한다() {
        List<CategoryUsage> categoryUsages = new ArrayList<>();
        categoryUsages.add(new CategoryUsage("GAME_RPG", "롤플레잉", 3000));
        categoryUsages.add(new CategoryUsage("GAME_PUZZLE", "퍼즐", 2000));
        categoryUsages.add(new CategoryUsage("GAME_SIMUL", "시뮬레이션", 1000));
        categoryUsages.add(new CategoryUsage("GAME_ACTION", "액션", 100));

        List<Pair<CategoryUsage, Integer>> usagePercentagePair = new ArrayList<>();
        usagePercentagePair.add(new Pair<>(categoryUsages.get(0), 49));
        usagePercentagePair.add(new Pair<>(categoryUsages.get(1), 33));
        usagePercentagePair.add(new Pair<>(categoryUsages.get(2), 16));

        when(mockPresenter.getPercentage(anyList(), anyInt(), anyInt())).thenReturn(usagePercentagePair);

        controller.create().start().resume().visible();

        subject.bindMyGenreViews(categoryUsages);

        // 상위 3개만 계산해온다
        verify(mockPresenter).getPercentage(eq(categoryUsages), eq(0), eq(3));

        assertThat(((TextView) subject.getView().findViewById(R.id.current_analysis_my_genre_1)
                .findViewById(R.id.name_textview)).getText()).isEqualTo("롤플레잉");
        assertThat(((TextView) subject.getView().findViewById(R.id.current_analysis_my_genre_1)
                .findViewById(R.id.used_time_textview)).getText()).isEqualTo("총 시간의 49%");

        assertThat(((TextView) subject.getView().findViewById(R.id.current_analysis_my_genre_2)
                .findViewById(R.id.name_textview)).getText()).isEqualTo("퍼즐");
        assertThat(((TextView) subject.getView().findViewById(R.id.current_analysis_my_genre_2)
                .findViewById(R.id.used_time_textview)).getText()).isEqualTo("총 시간의 33%");

        assertThat(((TextView) subject.getView().findViewById(R.id.current_analysis_my_genre_3)
                .findViewById(R.id.name_textview)).getText()).isEqualTo("시뮬레이션");
        assertThat(((TextView) subject.getView().findViewById(R.id.current_analysis_my_genre_3)
                .findViewById(R.id.used_time_textview)).getText()).isEqualTo("총 시간의 16%");
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

    public static class ShadowCurrentAnalysisReportFragment extends CurrentAnalysisReportFragment {
        @Override
        public void setPresenter(CurrentAnalysisReportContract.Presenter presenter) { }
    }
}
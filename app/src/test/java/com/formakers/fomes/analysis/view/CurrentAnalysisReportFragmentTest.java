package com.formakers.fomes.analysis.view;

import android.view.View;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CurrentAnalysisReportFragmentTest {

    CurrentAnalysisReportFragment subject;
    SupportFragmentController<CurrentAnalysisReportFragment> controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new CurrentAnalysisReportFragment();
        controller = SupportFragmentController.of(subject);

        controller.create().start().resume().visible();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void CurrentAnalysisReportFragment_시작시__분석_로딩_화면이_나타난다() {
        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.current_analysis_loading_layout).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_layout).getVisibility()).isEqualTo(View.GONE);
//        assertThat(subject.getView().findViewById(R.id.current_analysis_title_textview).getVisibility()).isEqualTo(View.GONE);
//        assertThat(subject.getView().findViewById(R.id.current_analysis_subtitle_textview).getVisibility()).isEqualTo(View.GONE);
//        assertThat(subject.getView().findViewById(R.id.current_analysis_my_genre_layout).getVisibility()).isEqualTo(View.GONE);
//        assertThat(subject.getView().findViewById(R.id.current_analysis_popular_genre_layout).getVisibility()).isEqualTo(View.GONE);
//        assertThat(subject.getView().findViewById(R.id.current_analysis_playtime_rank_layout).getVisibility()).isEqualTo(View.GONE);
//        assertThat(subject.getView().findViewById(R.id.current_analysis_favorite_developer_layout).getVisibility()).isEqualTo(View.GONE);
//        assertThat(subject.getView().findViewById(R.id.current_analysis_my_game_rank_layout).getVisibility()).isEqualTo(View.GONE);
//        assertThat(subject.getView().findViewById(R.id.current_analysis_popular_game_rank_layout).getVisibility()).isEqualTo(View.GONE);
//        assertThat(subject.getView().findViewById(R.id.current_analysis_announce_layout).getVisibility()).isEqualTo(View.GONE);
//        assertThat(subject.getView().findViewById(R.id.current_analysis_exit_button).getVisibility()).isEqualTo(View.GONE);
    }

}
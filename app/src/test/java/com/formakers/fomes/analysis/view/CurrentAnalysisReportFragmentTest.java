package com.formakers.fomes.analysis.view;

import android.view.View;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.analysis.contract.CurrentAnalysisReportContract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import rx.Completable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CurrentAnalysisReportFragmentTest {

    CurrentAnalysisReportFragment subject;
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

        subject = new CurrentAnalysisReportFragment();
        subject.setPresenter(mockPresenter);
        controller = SupportFragmentController.of(subject);

        when(mockPresenter.requestPostUsages()).thenReturn(Completable.never());
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
    public void 로딩_화면이_나타난_후__앱_누적_사용시간_전송을_요청한다() {
        controller.create().start().resume().visible();

        verify(mockPresenter).requestPostUsages();
    }

    @Test
    public void 앱_누적_사용시간_전송_성공시__화면을_모두_없애고_분석화면을_보여준다() {
        when(mockPresenter.requestPostUsages()).thenReturn(Completable.complete());

        controller.create().start().resume().visible();

        assertThat(subject.getView().findViewById(R.id.current_analysis_loading_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_layout).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_error_layout).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public void 앱_누적_사용시간_전송_실패시__화면을_모두_없애고_에러화면만_보여준다() {
        when(mockPresenter.requestPostUsages()).thenReturn(Completable.error(new Throwable()));

        controller.create().start().resume().visible();

        assertThat(subject.getView().findViewById(R.id.current_analysis_loading_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_layout).getVisibility()).isEqualTo(View.GONE);
        assertThat(subject.getView().findViewById(R.id.current_analysis_error_layout).getVisibility()).isEqualTo(View.VISIBLE);
    }
}
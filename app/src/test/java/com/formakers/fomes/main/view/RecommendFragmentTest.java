package com.formakers.fomes.main.view;

import android.view.View;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.main.presenter.RecommendPresenter;
import com.formakers.fomes.model.AppInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RecommendFragmentTest {

    @Mock RecommendPresenter recommendPresenter;

    RecommendFragment subject;
    SupportFragmentController<RecommendFragment> controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new RecommendFragment();
        subject.setPresenter(recommendPresenter);
        controller = SupportFragmentController.of(subject);

        controller.create().start().resume().visible();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void RecommendFragment_시작시__추천_화면이_나타난다() {
        assertThat(subject.getView()).isNotNull();
        assertThat(subject.getView().findViewById(R.id.recommend_recyclerview).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void 디테일정보를_보여달라는_이벤트_발생시__해당_앱의_패키지명을_토스트로_띄운다() {
        AppInfo appInfo = new AppInfo("com.formakers.fomes", "포메스", "카테고리ID", "카테고리", "카테고리ID2", "카테고리2", "https://lh3.googleusercontent.com/AHQJwkSC1J602KgQq0d3oMB-waafBrbaw9wAS80HGXQSSaEem4-zMowrGpbHIUuyyw=s360-rw");
        appInfo.setDeveloper("포메스");

        subject.onShowDetailEvent(appInfo);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("com.formakers.fomes");
    }
}
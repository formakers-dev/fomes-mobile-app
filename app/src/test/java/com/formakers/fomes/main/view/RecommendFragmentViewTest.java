package com.formakers.fomes.main.view;

import android.support.v4.app.Fragment;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.main.presenter.RecommendPresenter;
import com.formakers.fomes.model.AppInfo;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RecommendFragmentViewTest {

    @Mock RecommendPresenter mockRecommendPresenter;

    RecommendFragment subject;
    SupportFragmentController<RecommendFragment> controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = SupportFragmentController.of(new RecommendFragment()).create().get();
        subject.setPresenter(mockRecommendPresenter);
    }

    @Test
    public void onShowDetailEvent__디테일정보를_보여달라는_이벤트_발생시__디테일_정보를_보여주는_하단_다이얼로그를_띄운다() {
        AppInfo appInfo = new AppInfo("com.formakers.fomes", "포메스", "카테고리ID", "카테고리", "카테고리ID2", "카테고리2", "https://lh3.googleusercontent.com/AHQJwkSC1J602KgQq0d3oMB-waafBrbaw9wAS80HGXQSSaEem4-zMowrGpbHIUuyyw=s360-rw");
        appInfo.setStar(2.999).setInstallsMin(5000000000L).setContentsRating("contentRating");
        appInfo.setDeveloper("포메스");

        RecommendApp recommendApp = new RecommendApp().setAppInfo(appInfo)
                .setRecommendType(4).setCriteria(Lists.newArrayList("reason1", "reason2"));

        subject.onShowDetailEvent(recommendApp);

        Fragment fragment = subject.getChildFragmentManager().findFragmentByTag(AppInfoDetailDialogFragment.TAG);
        AppInfo actualAppInfo = fragment.getArguments().getParcelable(FomesConstants.EXTRA.APPINFO);
        int recommendType = fragment.getArguments().getInt(FomesConstants.EXTRA.RECOMMEND_TYPE);
        List<String> recommendCriteria = fragment.getArguments().getStringArrayList(FomesConstants.EXTRA.RECOMMEND_CRITERIA);

        assertThat(actualAppInfo).isNotNull();
        assertThat(actualAppInfo.getPackageName()).isEqualTo("com.formakers.fomes");
        assertThat(recommendType).isEqualTo(4);
        assertThat(recommendCriteria.get(0)).isEqualTo("reason1");
        assertThat(recommendCriteria.get(1)).isEqualTo("reason2");
    }
}
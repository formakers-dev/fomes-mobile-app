package com.formakers.fomes.recommend;

import android.content.Intent;
import android.util.SupportFragmentController;

import com.formakers.fomes.common.model.AppInfo;
import com.formakers.fomes.common.network.vo.RecommendApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class RecommendFragmentViewTest {

    @Mock RecommendPresenter mockRecommendPresenter;

    RecommendFragment subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = SupportFragmentController.of(new RecommendFragment()).create().get();
        subject.setPresenter(mockRecommendPresenter);
    }

    @Test
    public void onShowDetailEvent__디테일정보를_보여달라는_이벤트_발생시__플레이스토어로_이동한다() {
        AppInfo appInfo = new AppInfo("com.formakers.fomes", "포메스", "카테고리ID", "카테고리", "카테고리ID2", "카테고리2", "https://lh3.googleusercontent.com/AHQJwkSC1J602KgQq0d3oMB-waafBrbaw9wAS80HGXQSSaEem4-zMowrGpbHIUuyyw=s360-rw");
        RecommendApp recommendApp = new RecommendApp().setAppInfo(appInfo);

        subject.onShowDetailEvent(recommendApp);

        Intent intent = shadowOf(subject.getActivity()).getNextStartedActivity();
        assertThat(intent.getAction()).isEqualTo(Intent.ACTION_VIEW);
        assertThat(intent.getData().toString()).isEqualTo("market://details?id=com.formakers.fomes");
    }
}
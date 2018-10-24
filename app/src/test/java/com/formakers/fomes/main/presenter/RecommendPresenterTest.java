package com.formakers.fomes.main.presenter;


import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.model.AppInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecommendPresenterTest {

    @Mock RecommendContract.View mockView;
    @Mock RecommendService mockRecommendService;

    RecommendPresenter subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new RecommendPresenter(mockView, mockRecommendService);
    }

    @Test
    public void emitShowDetailEvent__디테일화면을_보여주라는_이벤트_발생시__뷰에_콜백을_호출한다() {
        subject.emitShowDetailEvent(new AppInfo("packageName", "appName"));

        ArgumentCaptor<AppInfo> captor = ArgumentCaptor.forClass(AppInfo.class);
        verify(mockView).onShowDetailEvent(captor.capture());
        assertThat(captor.getValue().getPackageName()).isEqualTo("packageName");
        assertThat(captor.getValue().getAppName()).isEqualTo("appName");
    }

    @Test
    public void loadSimilarAppsByDemographic__비슷한_인적사항_기준_리스트_로드시__해당_서버에_요청한다() {
        List<RecommendApp> items = new ArrayList<>();

        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test1"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test2"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test3"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));

        when(mockRecommendService.requestSimilarAppsByDemographic(anyInt(), anyInt())).thenReturn(Observable.just(items));

        TestSubscriber<List<RecommendApp>> testSubscriber = new TestSubscriber<>();
        subject.loadSimilarAppsByDemographic().subscribe(testSubscriber);

        verify(mockRecommendService).requestSimilarAppsByDemographic(anyInt(), anyInt());

        List<RecommendApp> result = testSubscriber.getOnNextEvents().get(0);

        assertThat(result.get(0).getAppInfo().getPackageName()).isEqualTo("com.test1");
        assertThat(result.get(0).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC);
        assertThat(result.get(1).getAppInfo().getPackageName()).isEqualTo("com.test2");
        assertThat(result.get(1).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC);
        assertThat(result.get(2).getAppInfo().getPackageName()).isEqualTo("com.test3");
        assertThat(result.get(2).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC);
    }
}
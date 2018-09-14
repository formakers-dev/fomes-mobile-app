package com.formakers.fomes.main.presenter;


import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.model.AppInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class RecommendPresenterTest {

    @Mock RecommendContract.View mockView;

    RecommendPresenter subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

//        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        subject = new RecommendPresenter(mockView);
    }

    @Test
    public void emitShowDetailEvent__디테일화면을_보여주라는_이벤트_발생시__뷰에_콜백을_호출한다() {
        subject.emitShowDetailEvent(new AppInfo("packageName", "appName"));

        ArgumentCaptor<AppInfo> captor = ArgumentCaptor.forClass(AppInfo.class);
        verify(mockView).onShowDetailEvent(captor.capture());
        assertThat(captor.getValue().getPackageName()).isEqualTo("packageName");
        assertThat(captor.getValue().getAppName()).isEqualTo("appName");
    }
}
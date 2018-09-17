package com.formakers.fomes.analysis.presenter;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.analysis.contract.CurrentAnalysisReportContract;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.network.AppStatService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CurrentAnalysisReportPresenterTest {

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;
    @Inject
    AppStatService mockAppStatService;

    @Mock CurrentAnalysisReportContract.View mockView;

    CurrentAnalysisReportPresenter subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockAppUsageDataHelper.getAppUsagesFor(7)).thenReturn(anyList());

        subject = new CurrentAnalysisReportPresenter(mockView, mockAppUsageDataHelper, mockAppStatService);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void requestPostUsages_호출시__7일간의_앱_누적_사용시간을_서버에_전송한다() {
        subject.requestPostUsages();

        verify(mockAppUsageDataHelper).getAppUsagesFor(eq(7));
        verify(mockAppStatService).sendAppUsages(anyList());
    }
}
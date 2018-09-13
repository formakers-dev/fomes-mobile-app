package com.formakers.fomes.provisioning.view;

import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.analysis.view.CurrentAnalysisReportActivity;
import com.formakers.fomes.common.view.FomesBaseActivityTest;

import org.junit.Before;
import org.robolectric.RuntimeEnvironment;

public class CurrentAnalysisReportActivityTest extends FomesBaseActivityTest {

    public CurrentAnalysisReportActivityTest() {
        super(CurrentAnalysisReportActivity.class);
    }

    @Override
    public void launchActivity() {
        subject = getActivity();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        super.setUp();
    }
}
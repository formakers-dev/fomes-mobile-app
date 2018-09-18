package com.formakers.fomes.provisioning.view;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.analysis.view.CurrentAnalysisReportActivity;
import com.formakers.fomes.common.view.FomesBaseActivityTest;
import com.formakers.fomes.shadow.ShadowCurrentAnalysisReportFragment;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowCurrentAnalysisReportFragment.class})
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
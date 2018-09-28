package com.formakers.fomes.provisioning.view;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.analysis.view.RecentAnalysisReportActivity;
import com.formakers.fomes.common.view.FomesBaseActivityTest;
import com.formakers.fomes.shadow.ShadowRecentAnalysisReportFragment;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowRecentAnalysisReportFragment.class})
public class RecentAnalysisReportActivityTest extends FomesBaseActivityTest {

    public RecentAnalysisReportActivityTest() {
        super(RecentAnalysisReportActivity.class);
    }

    @Override
    public void launchActivity() {
        subject = getActivity();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        ((TestFomesApplication) RuntimeEnvironment.application).getComponent().inject(this);
        super.setUp();
    }
}
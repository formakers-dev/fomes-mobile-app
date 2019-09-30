package com.formakers.fomes.provisioning;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.analysis.RecentAnalysisReportActivity;
import com.formakers.fomes.common.view.FomesBaseActivityTest;
import com.formakers.fomes.shadow.ShadowRecentAnalysisReportFragment;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowRecentAnalysisReportFragment.class})
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
        ((TestFomesApplication) ApplicationProvider.getApplicationContext()).getComponent().inject(this);
        super.setUp();
    }
}
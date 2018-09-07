package com.formakers.fomes.provisioning.view;

import android.view.View;

import com.formakers.fomes.R;
import com.formakers.fomes.activity.BaseActivityTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProvisioningActivityTest extends BaseActivityTest<ProvisioningActivity> {

    private ProvisioningActivity subject;

    public ProvisioningActivityTest() {
        super(ProvisioningActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = getActivity();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void ProvisioningActivity_시작시_프로비저닝화면이_나타난다() {
        assertThat(subject.findViewById(R.id.provision_icon_imageview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.findViewById(R.id.provision_viewpager).getVisibility()).isEqualTo(View.VISIBLE);
    }

}
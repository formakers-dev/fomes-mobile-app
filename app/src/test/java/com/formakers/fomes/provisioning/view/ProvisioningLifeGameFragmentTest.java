package com.formakers.fomes.provisioning.view;


import android.view.View;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.provisioning.contract.ProvisioningContract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ProvisioningLifeGameFragmentTest {

    @Mock ProvisioningContract.Presenter mockPresenter;

    ProvisioningLifeGameFragment subject;
    SupportFragmentController<ProvisioningLifeGameFragment> controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subject = new ProvisioningLifeGameFragment();
        subject.setPresenter(mockPresenter);
        controller = SupportFragmentController.of(subject);

        controller.create().start().resume();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void ProvisioningLifeGameFragmentTest_시작시__프로비저닝_인생게임_화면이_나타난다() {
        assertThat(subject.getView().findViewById(R.id.provision_life_game_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_life_game_subtitle_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_life_game_content_title_textview).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.provision_life_game_content_edittext).getVisibility()).isEqualTo(View.VISIBLE);
        assertThat(subject.getView().findViewById(R.id.next_button).getVisibility()).isEqualTo(View.VISIBLE);
    }
}
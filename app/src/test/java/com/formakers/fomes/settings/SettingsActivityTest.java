package com.formakers.fomes.settings;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.constant.FomesConstants.Settings.Menu;
import com.formakers.fomes.common.view.FomesBaseActivityTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

public class SettingsActivityTest extends FomesBaseActivityTest<SettingsActivity> {

    public SettingsActivityTest() {
        super(SettingsActivity.class);
    }

    @Override
    public void launchActivity() {
        subject = getActivity();
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ((TestFomesApplication) ApplicationProvider.getApplicationContext()).getComponent().inject(this);
        super.setUp();
    }

    @Test
    public void SettingsActivity_시작시_설정화면이_나타난다() throws Exception  {
        launchActivity();

        assertThat(subject.findViewById(R.id.settings_recyclerview).getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void 설정메뉴에__버전정보와_빌드타입이_나타난다() throws Exception  {
        launchActivity();

        SettingsItem settingsItem = ((SettingsListAdapter) ((RecyclerView) subject.findViewById(R.id.settings_recyclerview)).getAdapter()).getItem(0);
        assertThat(settingsItem.getSideInfo()).contains(BuildConfig.VERSION_NAME);
        assertThat(settingsItem.getSideInfo()).contains(BuildConfig.BUILD_TYPE);
    }

    @Test
    public void 설정메뉴의_이용약관_클릭시__이용약관_링크로_넘어간다() throws Exception  {
        subject =  getActivity(LIFECYCLE_TYPE_VISIBLE);

        subject.findViewById(R.id.settings_recyclerview).findViewWithTag(Menu.TNC_USAGE).performClick();

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getAction()).isEqualTo(Intent.ACTION_VIEW);
        assertThat(intent.getData().toString()).isEqualTo("https://docs.google.com/document/d/1hcU3MQvJf0L2ZNl3T3H1n21C4E82FM2ppesDwjo-Wy4/edit?usp=sharing");
    }

    @Test
    public void 설정메뉴의_개인정보약관_클릭시__개인정보약관_링크로_넘어간다() throws Exception  {
        subject =  getActivity(LIFECYCLE_TYPE_VISIBLE);

        subject.findViewById(R.id.settings_recyclerview).findViewWithTag(Menu.TNC_PRIVATE).performClick();

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getAction()).isEqualTo(Intent.ACTION_VIEW);
        assertThat(intent.getData().toString()).isEqualTo("https://docs.google.com/document/d/1QxWuv2zlKYsGYglUTiP5d5IFxZUy6HwKGObXC8x0034/edit?usp=sharing");
    }

    @Test
    public void 설정메뉴의_문의하기_클릭시__포메스측에게_메일전송하는_화면으로_넘어간다() throws Exception  {
        subject =  getActivity(LIFECYCLE_TYPE_VISIBLE);

        subject.findViewById(R.id.settings_recyclerview).findViewWithTag(Menu.CONTACTS_US).performClick();

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getAction()).isEqualTo(Intent.ACTION_SEND);
        assertThat(intent.getStringArrayExtra(Intent.EXTRA_EMAIL)).contains("contact@formakers.net");
    }
}
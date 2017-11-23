package com.appbee.appbeemobile.fragment;

import android.widget.ImageView;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.activity.IFragmentManager;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.network.AppService;
import com.bumptech.glide.Glide;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OnboardingAnalysisFragmentTest {

    @Mock
    private IFragmentManager mockFragmentManager;

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;

    @Inject
    AppService mockAppService;

    private OnboardingAnalysisFragment subject;
    private SupportFragmentController<OnboardingAnalysisFragment> controller;
    private Unbinder unbinder;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        MockitoAnnotations.initMocks(this);
        subject = new OnboardingAnalysisFragment();
        subject.setFragmentManager(mockFragmentManager);

        controller = SupportFragmentController.of(subject);
        unbinder = ButterKnife.bind(this, subject.getView());

        List<String> appList = Arrays.asList("com.kakao.com", "com.facebook.com", "com.naver.com");
        when(mockAppUsageDataHelper.getSortedUsedPackageNames()).thenReturn(Observable.just(appList));
        List<AppInfo> appInfoList = new ArrayList<>();
        appInfoList.add(new AppInfo("packageName1", "appName1", "categoryId1", "categoryName1", "categoryId2", "categoryName2", "www.iconUrl1.com"));
        appInfoList.add(new AppInfo("packageName2", "appName2", "categoryId1", "categoryName1", "categoryId2", "categoryName2", "www.iconUrl2.com"));
        appInfoList.add(new AppInfo("packageName3", "appName3", "categoryId1", "categoryName1", "categoryId2", "categoryName2", "www.iconUrl3.com"));
        when(mockAppService.getAppInfo(appList)).thenReturn(Observable.just(appInfoList));

        controller.create().start().resume();
    }

    @After
    public void tearDown() throws Exception {
        unbinder.unbind();
    }

    @Test
    public void onViewCreated시_가장많이사용한앱3개가_나타난다() throws Exception {

        ImageView expectedImageView = new ImageView(RuntimeEnvironment.application);
        Glide.with(RuntimeEnvironment.application).load("").into(expectedImageView);

        assertThat(subject.mostUsedAppViewGroup.getChildCount()).isEqualTo(3);

        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(0).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName1");
        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(1).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName2");
        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(2).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName3");

        ImageView imageview1 = ((ImageView) subject.mostUsedAppViewGroup.getChildAt(0).findViewById(R.id.app_imageview));
        assertThat(imageview1.getTag(R.string.tag_key_image_url)).isEqualTo("www.iconUrl1.com");
        ImageView imageview2 = ((ImageView) subject.mostUsedAppViewGroup.getChildAt(1).findViewById(R.id.app_imageview));
        assertThat(imageview2.getTag(R.string.tag_key_image_url)).isEqualTo("www.iconUrl2.com");
        ImageView imageview3 = ((ImageView) subject.mostUsedAppViewGroup.getChildAt(2).findViewById(R.id.app_imageview));
        assertThat(imageview3.getTag(R.string.tag_key_image_url)).isEqualTo("www.iconUrl3.com");
    }

    @Test
    public void nextButton클릭시_OnboardingRewardsFragment로_이동하도록하는_메소드를_호출한다() throws Exception {
        subject.getView().findViewById(R.id.next_button).performClick();
        verify(mockFragmentManager).replaceFragment(anyString());
    }
}
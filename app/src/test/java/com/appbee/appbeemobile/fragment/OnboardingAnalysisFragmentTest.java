package com.appbee.appbeemobile.fragment;

import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.NativeAppInfoHelper;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.network.AppService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class OnboardingAnalysisFragmentTest {

    @Inject
    AppService mockAppService;

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;

    @Inject
    NativeAppInfoHelper mockNativeAppInfoHelper;

    private OnboardingAnalysisFragment subject;
    private SupportFragmentController<OnboardingAnalysisFragment> controller;
    private Unbinder unbinder;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();

            }
        });

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        MockitoAnnotations.initMocks(this);

        subject = new OnboardingAnalysisFragment();

        controller = SupportFragmentController.of(subject);
        unbinder = ButterKnife.bind(this, subject.getView());

        List<String> appList = Arrays.asList("packageName1", "packageName2", "packageName3", "packageName4", "packageName5");
        when(mockAppUsageDataHelper.getSortedUsedPackageNames()).thenReturn(Observable.just(appList));

        when(mockNativeAppInfoHelper.getNativeAppInfo("packageName1")).thenReturn(new NativeAppInfo("packageName1", "appName1"));
        when(mockNativeAppInfoHelper.getNativeAppInfo("packageName2")).thenReturn(new NativeAppInfo("packageName2", "appName2"));
        when(mockNativeAppInfoHelper.getNativeAppInfo("packageName3")).thenReturn(new NativeAppInfo("packageName3", "appName3"));
        when(mockNativeAppInfoHelper.getNativeAppInfo("packageName4")).thenReturn(new NativeAppInfo("packageName4", "appName4"));
        when(mockNativeAppInfoHelper.getNativeAppInfo("packageName5")).thenReturn(new NativeAppInfo("packageName5", "appName5"));

        when(mockAppService.getPopularApps()).thenReturn(Arrays.asList("packageName1", "packageName2"));

        controller.create().start().resume();
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
        unbinder.unbind();
    }

    @Test
    public void onViewCreated시_가장인기있는앱을제외하고_가장개성있는앱3개가_나타난다() throws Exception {
        assertThat(subject.mostPersonalityAppViewGroup.getChildCount()).isEqualTo(3);

        assertThat(((TextView) subject.mostPersonalityAppViewGroup.getChildAt(0).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName3");
        assertThat(((TextView) subject.mostPersonalityAppViewGroup.getChildAt(1).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName4");
        assertThat(((TextView) subject.mostPersonalityAppViewGroup.getChildAt(2).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName5");

        assertThat(subject.mostPersonalityAppViewGroup.getChildAt(0).findViewById(R.id.app_imageview).getTag(R.string.tag_key_image_url)).isEqualTo("packageName3");
        assertThat(subject.mostPersonalityAppViewGroup.getChildAt(1).findViewById(R.id.app_imageview).getTag(R.string.tag_key_image_url)).isEqualTo("packageName4");
        assertThat(subject.mostPersonalityAppViewGroup.getChildAt(2).findViewById(R.id.app_imageview).getTag(R.string.tag_key_image_url)).isEqualTo("packageName5");
    }

    @Test
    public void onViewCreated시_가장많이사용한앱3개가_나타난다() throws Exception {
        assertThat(subject.mostUsedAppViewGroup.getChildCount()).isEqualTo(3);

        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(0).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName1");
        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(1).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName2");
        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(2).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName3");

        assertThat(subject.mostUsedAppViewGroup.getChildAt(0).findViewById(R.id.app_imageview).getTag(R.string.tag_key_image_url)).isEqualTo("packageName1");
        assertThat(subject.mostUsedAppViewGroup.getChildAt(1).findViewById(R.id.app_imageview).getTag(R.string.tag_key_image_url)).isEqualTo("packageName2");
        assertThat(subject.mostUsedAppViewGroup.getChildAt(2).findViewById(R.id.app_imageview).getTag(R.string.tag_key_image_url)).isEqualTo("packageName3");
    }
}
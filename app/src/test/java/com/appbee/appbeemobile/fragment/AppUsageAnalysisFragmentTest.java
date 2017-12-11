package com.appbee.appbeemobile.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.widget.TextView;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.NativeAppInfoHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.NativeAppInfo;
import com.appbee.appbeemobile.model.ShortTermStat;
import com.appbee.appbeemobile.network.AppService;
import com.appbee.appbeemobile.network.ConfigService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AppUsageAnalysisFragmentTest {

    @Inject
    AppService mockAppService;

    @Inject
    ConfigService mockConfigService;

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;

    @Inject
    NativeAppInfoHelper mockNativeAppInfoHelper;

    @Inject
    TimeHelper mockTimeHelper;

    private AppUsageAnalysisFragment subject;
    private SupportFragmentController<AppUsageAnalysisFragment> controller;
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

        List<ShortTermStat> mockShortTermStatList = new ArrayList<>();
        mockShortTermStatList.add(new ShortTermStat("packageName1", 0L, 0L, 5000L));     //2017-11-18 03:00:00
        mockShortTermStatList.add(new ShortTermStat("packageName2", 0L, 0L, 4000L));     //2017-11-18 12:00:00
        mockShortTermStatList.add(new ShortTermStat("packageName3", 0L, 0L, 3000L));     //2017-11-19 00:00:00
        mockShortTermStatList.add(new ShortTermStat("packageName4", 0L, 0L, 2000L));     //2017-11-18 12:00:00
        mockShortTermStatList.add(new ShortTermStat("packageName5", 0L, 0L, 1000L));     //2017-11-18 12:00:00

        when(mockAppUsageDataHelper.getWeeklyStatSummaryList()).thenReturn(mockShortTermStatList);
        when(mockConfigService.getExcludePackageNames()).thenReturn(Arrays.asList("packageName1", "packageName2"));

        when(mockNativeAppInfoHelper.getNativeAppInfo("packageName1")).thenReturn(new NativeAppInfo("packageName1", "appName1"));
        when(mockNativeAppInfoHelper.getNativeAppInfo("packageName2")).thenReturn(new NativeAppInfo("packageName2", "appName2"));
        when(mockNativeAppInfoHelper.getNativeAppInfo("packageName3")).thenReturn(new NativeAppInfo("packageName3", "appName3"));
        when(mockNativeAppInfoHelper.getNativeAppInfo("packageName4")).thenReturn(new NativeAppInfo("packageName4", "appName4"));
        when(mockNativeAppInfoHelper.getNativeAppInfo("packageName5")).thenReturn(new NativeAppInfo("packageName5", "appName5"));
    }

    private void setupWithoutDescription() {
        subject = new AppUsageAnalysisFragment();
        controller = SupportFragmentController.of(subject);
        unbinder = ButterKnife.bind(this, subject.getView());

        controller.create().start().resume();
    }

    private void setupWithAnalysisDescription(@StringRes int analysisDescriptionResId) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppUsageAnalysisFragment.EXTRA_DESCRIPTION_RES_ID, analysisDescriptionResId);
        subject = new AppUsageAnalysisFragment();
        subject.setArguments(bundle);

        controller = SupportFragmentController.of(subject);
        unbinder = ButterKnife.bind(this, subject.getView());

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
        setupWithoutDescription();
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
        setupWithoutDescription();
        assertThat(subject.mostUsedAppViewGroup.getChildCount()).isEqualTo(3);

        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(0).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName1");
        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(1).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName2");
        assertThat(((TextView) subject.mostUsedAppViewGroup.getChildAt(2).findViewById(R.id.app_name_textview)).getText()).isEqualTo("appName3");

        assertThat(subject.mostUsedAppViewGroup.getChildAt(0).findViewById(R.id.app_imageview).getTag(R.string.tag_key_image_url)).isEqualTo("packageName1");
        assertThat(subject.mostUsedAppViewGroup.getChildAt(1).findViewById(R.id.app_imageview).getTag(R.string.tag_key_image_url)).isEqualTo("packageName2");
        assertThat(subject.mostUsedAppViewGroup.getChildAt(2).findViewById(R.id.app_imageview).getTag(R.string.tag_key_image_url)).isEqualTo("packageName3");
    }

    @Test
    public void 분석결과설명과함께fragment를생성하는경우_onViewCreated시_해당설명을_표시한다() throws Exception {
        setupWithAnalysisDescription(R.string.analysis_title);
        assertThat(subject.analysisDescription.getText()).isEqualTo(subject.getString(R.string.analysis_title));
    }

}
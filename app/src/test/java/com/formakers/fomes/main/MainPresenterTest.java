package com.formakers.fomes.main;

import android.os.Bundle;
import android.view.View;

import com.formakers.fomes.common.constant.FomesConstants;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.PostService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.network.vo.Post;
import com.formakers.fomes.common.view.FomesNoticeDialog;
import com.google.common.collect.Lists;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import rx.Completable;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class MainPresenterTest {

    @Mock UserService mockUserService;
    @Mock JobManager mockJobManager;
    @Mock EventLogService mockEventLogService;
    @Mock PostService mockPostService;
    @Mock FomesUrlHelper mockFomesUrlHelper;
    @Mock AnalyticsModule.Analytics mockAnalytics;
    @Mock ImageLoader mockImageLoader;
    @Mock FirebaseRemoteConfig mockFirebaseRemoteConfig;
    @Mock SharedPreferencesHelper mockSharedPreferencesHelper;

    @Mock EventPagerAdapterContract.Model mockEventPagerAdapterModel;

    @Mock
    MainContract.View mockView;

    private MainPresenter subject;

    private TestScheduler testScheduler;

    @Before
    public void setUp() throws Exception {
        testScheduler = new TestScheduler();

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnComputationScheduler(scheduler -> testScheduler);

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        MockitoAnnotations.initMocks(this);

        when(mockPostService.getPromotions()).thenReturn(Single.just(Lists.newArrayList(new Post())));
        when(mockEventPagerAdapterModel.getCount()).thenReturn(3);

        subject = new MainPresenter(mockView, Single.just("email"), Single.just("nickName"), mockUserService, mockPostService, mockEventLogService, mockJobManager, mockFomesUrlHelper, mockAnalytics, mockImageLoader, mockFirebaseRemoteConfig, mockSharedPreferencesHelper);

        subject.setEventPagerAdapterModel(mockEventPagerAdapterModel);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void requestVerifyAccessToken_?????????__??????_?????????_????????????() {
        subject.requestVerifyAccessToken();

        verify(mockUserService).verifyToken();
    }

    @Test
    public void registerSendDataJob_?????????__???????????????????????????_?????????_????????????() {
        subject.registerSendDataJob();

        verify(mockJobManager).registerSendDataJob(eq(JobManager.JOB_ID_SEND_DATA));
    }

    @Test
    public void checkRegisteredSendDataJob_?????????__???????????????????????????_?????????_???????????????_????????????() {
        when(mockJobManager.isRegisteredJob(JobManager.JOB_ID_SEND_DATA)).thenReturn(true);

        boolean isRegistered = subject.checkRegisteredSendDataJob();

        verify(mockJobManager).isRegisteredJob(eq(JobManager.JOB_ID_SEND_DATA));
        assertThat(isRegistered).isTrue();
    }

    @Test
    public void checkNeedToShowMigrationDialog_?????????__???????????????_?????????__???????????????_?????????() {
        String migrationNotice = "{\n" +
                "  \"title\": \"????????? ?????????!\",\n" +
                "  \"description\": \"????????????\\n????????????\\n????????????!\",\n" +
                "  \"guide\": \"????????? ???????????????\",\n" +
                "  \"descriptionImages\": [\n" +
                "    \"https://i.pinimg.com/originals/60/f6/e7/60f6e7294309c3ec67855e35eb1912da.gif\",\n" +
                "    \"https://i.imgur.com/SA8ZtA9.jpg\",\n" +
                "    \"https://i.imgur.com/8efXcqY.png\"\n" +
                "  ],\n" +
                "  \"versionCode\": -1,\n" +
                "  \"noticeVersion\": 3\n" +
                "}";

        when(mockFirebaseRemoteConfig.getString(FomesConstants.RemoteConfig.MIGRATION_NOTICE)).thenReturn(migrationNotice);
        when(mockSharedPreferencesHelper.getMigrationNoticeVersion()).thenReturn(1);

        subject.checkNeedToShowMigrationDialog();

        ArgumentCaptor<Bundle> bundleArgumentCaptor = ArgumentCaptor.forClass(Bundle.class);
        verify(mockView).showMigrationNoticeDialog(bundleArgumentCaptor.capture(), any());

        assertThat(bundleArgumentCaptor.getValue().getString(FomesNoticeDialog.EXTRA_TITLE)).isEqualTo("????????? ?????????!");
        assertThat(bundleArgumentCaptor.getValue().getString(FomesNoticeDialog.EXTRA_SUBTITLE)).isEqualTo("????????????\n????????????\n????????????!");
        assertThat(bundleArgumentCaptor.getValue().getString(FomesNoticeDialog.EXTRA_DESCRIPTION)).isEqualTo("????????? ???????????????");
        assertThat(bundleArgumentCaptor.getValue().getStringArrayList(FomesNoticeDialog.EXTRA_IMAGE_URL_LIST).size()).isEqualTo(3);
        assertThat(bundleArgumentCaptor.getValue().getStringArrayList(FomesNoticeDialog.EXTRA_IMAGE_URL_LIST).get(0)).isEqualTo("https://i.pinimg.com/originals/60/f6/e7/60f6e7294309c3ec67855e35eb1912da.gif");
        assertThat(bundleArgumentCaptor.getValue().getStringArrayList(FomesNoticeDialog.EXTRA_IMAGE_URL_LIST).get(1)).isEqualTo("https://i.imgur.com/SA8ZtA9.jpg");
        assertThat(bundleArgumentCaptor.getValue().getStringArrayList(FomesNoticeDialog.EXTRA_IMAGE_URL_LIST).get(2)).isEqualTo("https://i.imgur.com/8efXcqY.png");
    }

    @Test
    public void checkNeedToShowMigrationDialog_?????????__???????????????_????????????_?????????__????????????_?????????_??????????????????() {
        String migrationNotice = "{\n" +
                "  \"title\": \"?????? ?????? ????????? ???????????????\",\n" +
                "  \"versionCode\": -1,\n" +
                "  \"noticeVersion\": 3\n" +
                "}";

        when(mockFirebaseRemoteConfig.getString(FomesConstants.RemoteConfig.MIGRATION_NOTICE)).thenReturn(migrationNotice);
        when(mockSharedPreferencesHelper.getMigrationNoticeVersion()).thenReturn(1);

        subject.checkNeedToShowMigrationDialog();

        ArgumentCaptor<View.OnClickListener> clickListenerArgumentCaptor = ArgumentCaptor.forClass(View.OnClickListener.class);
        verify(mockView).showMigrationNoticeDialog(any(), clickListenerArgumentCaptor.capture());

        clickListenerArgumentCaptor.getValue().onClick(null);

        verify(mockSharedPreferencesHelper).setMigrationNoticeVersion(3);
    }

    @Test
    public void checkNeedToShowMigrationDialog_?????????__???????????????_??????_????????????_?????????__????????????_????????????_????????????() {
        String migrationNotice = "{\n" +
                "  \"title\": \"???????????? ???????????? ???????????????\",\n" +
                "  \"versionCode\": 999999999,\n" +
                "  \"noticeVersion\": 3\n" +
                "}";

        when(mockFirebaseRemoteConfig.getString(FomesConstants.RemoteConfig.MIGRATION_NOTICE)).thenReturn(migrationNotice);
        when(mockSharedPreferencesHelper.getMigrationNoticeVersion()).thenReturn(1);

        subject.checkNeedToShowMigrationDialog();

        ArgumentCaptor<Bundle> bundleArgumentCaptor = ArgumentCaptor.forClass(Bundle.class);
        ArgumentCaptor<View.OnClickListener> clickListenerArgumentCaptor = ArgumentCaptor.forClass(View.OnClickListener.class);
        verify(mockView).showMigrationNoticeDialog(bundleArgumentCaptor.capture(), clickListenerArgumentCaptor.capture());

        assertThat(bundleArgumentCaptor.getValue().getBoolean("IS_NEED_TO_UPDATE")).isTrue();
        assertThat(bundleArgumentCaptor.getValue().getString("POSITIVE_BUTTON_TEXT")).isEqualTo("???????????? ????????????");

        clickListenerArgumentCaptor.getValue().onClick(null);

        verify(mockView).moveToPlayStore();
    }

    @Test
    public void checkNeedToShowMigrationDialog_?????????__???????????????_??????_????????????_?????????_?????????__??????_????????????_????????????() {
        String migrationNotice = "{\n" +
                "  \"title\": \"???????????? ???????????? ???????????????\",\n" +
                "  \"versionCode\": -1,\n" +
                "  \"noticeVersion\": 3\n" +
                "}";

        when(mockFirebaseRemoteConfig.getString(FomesConstants.RemoteConfig.MIGRATION_NOTICE)).thenReturn(migrationNotice);
        when(mockSharedPreferencesHelper.getMigrationNoticeVersion()).thenReturn(1);

        subject.checkNeedToShowMigrationDialog();

        ArgumentCaptor<Bundle> bundleArgumentCaptor = ArgumentCaptor.forClass(Bundle.class);
        ArgumentCaptor<View.OnClickListener> clickListenerArgumentCaptor = ArgumentCaptor.forClass(View.OnClickListener.class);
        verify(mockView).showMigrationNoticeDialog(bundleArgumentCaptor.capture(), clickListenerArgumentCaptor.capture());

        assertThat(bundleArgumentCaptor.getValue().getBoolean("IS_NEED_TO_UPDATE")).isFalse();
        assertThat(bundleArgumentCaptor.getValue().getString("POSITIVE_BUTTON_TEXT")).isEqualTo("??????");

        clickListenerArgumentCaptor.getValue().onClick(null);
        verify(mockView, never()).moveToPlayStore();
    }

    @Test
    public void checkNeedToShowMigrationDialog_?????????__???????????????_?????????__???????????????_?????????_?????????() {
        String migrationNotice = "{\n" +
                "  \"title\": \"?????? ????????????\",\n" +
                "  \"versionCode\": -1,\n" +
                "  \"noticeVersion\": 3\n" +
                "}";

        when(mockFirebaseRemoteConfig.getString(FomesConstants.RemoteConfig.MIGRATION_NOTICE)).thenReturn(migrationNotice);
        when(mockSharedPreferencesHelper.getMigrationNoticeVersion()).thenReturn(4);

        subject.checkNeedToShowMigrationDialog();

        verify(mockView, never()).showMigrationNoticeDialog(any(), any());
    }

    @Test
    public void requestPromotions_?????????__??????????????????_????????????() {
        subject.requestPromotions();

        ArgumentCaptor<List<Post>> postArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockEventPagerAdapterModel).addAll(postArgumentCaptor.capture());
        verify(mockView).refreshEventPager();

        List<Post> actualPosts = postArgumentCaptor.getValue();

    }

    @Test
    public void getPromotionCount_?????????__??????????????????_?????????_????????????() {
        assertThat(subject.getPromotionCount()).isEqualTo(3);
    }

    @Test
    public void sendEventLog_?????????__????????????_?????????_??????_???????????????_?????????_????????????() {
        when(mockEventLogService.sendEventLog(any())).thenReturn(Completable.complete());

        subject.sendEventLog("ANY_CODE");

        ArgumentCaptor<EventLog> eventLogCaptor = ArgumentCaptor.forClass(EventLog.class);

        verify(mockEventLogService).sendEventLog(eventLogCaptor.capture());
        assertEquals(eventLogCaptor.getValue().getCode(), "ANY_CODE");
    }

    @Test
    public void getInterpretedUrl_?????????__????????????_?????????_?????????_URL???_????????????() {
        subject.getInterpretedUrl("http://www.naver.com?email={email}");

        verify(mockFomesUrlHelper).interpretUrlParams(eq("http://www.naver.com?email={email}"));
    }
}
package com.formakers.fomes.betatest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.AppUsageDataHelper;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.network.vo.Mission;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class BetaTestDetailPresenterTest {

    @Mock BetaTestDetailContract.View mockView;
    @Mock AnalyticsModule.Analytics mockAnalytics;
    @Mock EventLogService mockEventLogService;
    @Mock BetaTestService mockBetaTestService;
    @Mock FomesUrlHelper mockFomesUrlHelper;
    @Mock AndroidNativeHelper mockAndroidNativeHelper;
    @Mock AppUsageDataHelper mockAppUsageDataHelper;
    @Mock ImageLoader mockImageLoader;
    @Mock FirebaseRemoteConfig mockRemoteConfig;
    @Mock MissionListAdapterContract.Model mockMissionListAdapterModel;

    BetaTestDetailPresenter subject;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.trampoline());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.trampoline());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.trampoline());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.trampoline();
            }
        });

        MockitoAnnotations.initMocks(this);

        when(mockBetaTestService.getDetailBetaTest("5d1c5e695c20ca481f27a4ab"))
                .thenReturn(Single.just(getDummyBetaTestDetail()));
        when(mockBetaTestService.postAttendBetaTest("5d1c5e695c20ca481f27a4ab"))
                .thenReturn(Completable.complete());
        when(mockBetaTestService.getMissionProgress("5d1c5e695c20ca481f27a4ab", "5d1ec8094400311578e996bc"))
                .thenReturn(Single.just(new Mission()));
        when(mockBetaTestService.postCompleteBetaTest(anyString())).thenReturn(Completable.complete());
        when(mockBetaTestService.postCompleteMission(anyString(), anyString())).thenReturn(Completable.complete());

        when(mockEventLogService.sendEventLog(any(EventLog.class))).thenReturn(Completable.complete());
        when(mockView.getCompositeSubscription()).thenReturn(new CompositeSubscription());

        subject = new BetaTestDetailPresenter(mockView, mockAnalytics, mockEventLogService, mockBetaTestService, mockFomesUrlHelper, mockAndroidNativeHelper, mockAppUsageDataHelper, mockImageLoader, mockRemoteConfig);
        subject.setAdapterModel(mockMissionListAdapterModel);
    }

    @Test
    public void sendEventLog_호출시__이벤트로그를_전송한다() {
        subject.sendEventLog("CODE", "REF");

        ArgumentCaptor<EventLog> argumentCaptor = ArgumentCaptor.forClass(EventLog.class);
        verify(mockEventLogService).sendEventLog(argumentCaptor.capture());
        EventLog actualEventLog = argumentCaptor.getValue();

        assertThat(actualEventLog.getCode()).isEqualTo("CODE");
        assertThat(actualEventLog.getRef()).isEqualTo("REF");
    }

    @Test
    public void load_호출시__해당_베타테스의_상세정보를_요청하고_각_순서에_맞게_정렬한다() {
        subject.load("5d1c5e695c20ca481f27a4ab");

        ArgumentCaptor<BetaTest> argumentCaptor = ArgumentCaptor.forClass(BetaTest.class);

        verify(mockView).bind(argumentCaptor.capture());
        BetaTest actualBetaTest = argumentCaptor.getValue();

        assertThat(actualBetaTest.getRewards().getList().size()).isEqualTo(3);
        assertThat(actualBetaTest.getRewards().getList().get(0).getOrder()).isEqualTo(1);
        assertThat(actualBetaTest.getRewards().getList().get(1).getOrder()).isEqualTo(2);
        assertThat(actualBetaTest.getRewards().getList().get(2).getOrder()).isEqualTo(3);
        assertThat(actualBetaTest.getMissions().size()).isEqualTo(4);
        assertThat(actualBetaTest.getMissions().get(0).getOrder()).isEqualTo(1);
        assertThat(actualBetaTest.getMissions().get(1).getOrder()).isEqualTo(2);
        assertThat(actualBetaTest.getMissions().get(2).getOrder()).isEqualTo(3);
        assertThat(actualBetaTest.getMissions().get(3).getOrder()).isEqualTo(5);
    }

    @Test
    public void refreshMissionProgress_호출시__해당_미션의_진행상태를_요청한다() {
        subject.load("5d1c5e695c20ca481f27a4ab");
        subject.getMissionProgress("5d1ec8094400311578e996bc");

        verify(mockBetaTestService).getMissionProgress(eq("5d1c5e695c20ca481f27a4ab"), eq("5d1ec8094400311578e996bc"));
    }

    @Test
    public void processMissionItemAction_호출시__딥링크를_호출한다() {
        when(mockFomesUrlHelper.interpretUrlParams(eq("https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty"), any(Bundle.class)))
                .thenReturn("https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty");

        // 디폴트
        subject.load("5d1c5e695c20ca481f27a4ab");
        subject.processMissionItemAction(getDummyBetaTestDetail().getMissions().get(1));

        verify(mockView).startByDeeplink(Uri.parse("https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty"));
    }

    @Test
    public void processMissionItemAction_호출시__인앱웹뷰인_경우__인앱웹뷰를_띄우도록_호출한다() {
        when(mockFomesUrlHelper.interpretUrlParams(eq("https://docs.google.com/forms/d/e/1FAIpQLSdxI2s694nLTVk4i7RMkkrtr-K_0s7pSKfUnRusr7348nQpJg/viewform?usp=pp_url&entry.1042588232={email}"), any(Bundle.class)))
                .thenReturn("https://docs.google.com/forms/d/e/1FAIpQLSdxI2s694nLTVk4i7RMkkrtr-K_0s7pSKfUnRusr7348nQpJg/viewform?usp=pp_url&entry.1042588232=test@gmail.com");

        // 인앱웹뷰
        subject.load("5d1c5e695c20ca481f27a4ab");
        subject.processMissionItemAction(getDummyBetaTestDetail().getMissions().get(0));
        System.out.println(getDummyBetaTestDetail().getMissions().get(0));

        verify(mockView).startSurveyWebViewActivity(eq(getDummyBetaTestDetail().getMissions().get(0).getId()), anyString(), eq("https://docs.google.com/forms/d/e/1FAIpQLSdxI2s694nLTVk4i7RMkkrtr-K_0s7pSKfUnRusr7348nQpJg/viewform?usp=pp_url&entry.1042588232=test@gmail.com"));

    }

    @Test
    public void processMissionItemAction_호출시__인스톨_타입인_경우__설치되어있으면__앱을_실행시킨다 () {
        when(mockFomesUrlHelper.interpretUrlParams(eq("https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty"), any(Bundle.class)))
                .thenReturn("https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty");

        // 플레이
        Intent expectedIntent = new Intent();
        when(mockAndroidNativeHelper.getLaunchableIntent("com.goodcircle.comeonkitty"))
                .thenReturn(expectedIntent);

        // 디폴트
        subject.load("5d1c5e695c20ca481f27a4ab");
        subject.processMissionItemAction(getDummyBetaTestDetail().getMissions().get(1));

        verify(mockAndroidNativeHelper).getLaunchableIntent("com.goodcircle.comeonkitty");
        verify(mockView).startActivity(expectedIntent);
    }

    @Test
    public void processMissionItemAction_호출시__인스톨_타입인_경우__설치되어있지않으면__디폴트_플로우를_탄다() {
        when(mockFomesUrlHelper.interpretUrlParams(eq("https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty"), any(Bundle.class)))
                .thenReturn("https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty");

        // 플레이 - 디폴트
        when(mockAndroidNativeHelper.getLaunchableIntent("com.goodcircle.comeonkitty"))
                .thenReturn(null);

        // 디폴트
        subject.load("5d1c5e695c20ca481f27a4ab");
        subject.processMissionItemAction(getDummyBetaTestDetail().getMissions().get(1));

        verify(mockAndroidNativeHelper).getLaunchableIntent("com.goodcircle.comeonkitty");
        verify(mockView).startByDeeplink(Uri.parse("https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty"));
    }

//    @Test
//    public void processMissionItemAction_호출시__플레이_타입인_경우__플레이시간을_측정한다() {
//        when(mockAppUsageDataHelper.getUsageTime("com.goodcircle.comeonkitty", 1562198400000L)) // 2019-07-04T00:00:00.000Z
//                .thenReturn(Observable.just(1000L));
//
//        subject.load("5d1c5e695c20ca481f27a4ab");
//        subject.processMissionItemAction(getDummyBetaTestDetail().getMissions().get(2));
//
//        verify(mockAppUsageDataHelper).getUsageTime(eq("com.goodcircle.comeonkitty"), eq(getDummyBetaTestDetail().getOpenDate().getTime()));
//        verify(mockView).refreshMission(getDummyBetaTestDetail().getMissions().get(2).getId());
//    }
//
//    @Test
//    public void processMissionItemAction_호출시__플레이_타입인_경우__플레이시간을_측정한다_2() {
//        when(mockAppUsageDataHelper.getUsageTime("com.goodcircle.comeonkitty", 1562198400000L)) // 2019-07-04T00:00:00.000Z
//                .thenReturn(Observable.just(0L));
//
//        subject.load("5d1c5e695c20ca481f27a4ab");
//        subject.processMissionItemAction(getDummyBetaTestDetail().getMissions().get(2));
//
//        verify(mockAppUsageDataHelper).getUsageTime(eq("com.goodcircle.comeonkitty"), eq(getDummyBetaTestDetail().getOpenDate().getTime()));
//        verify(mockView).showToast("플레이 시간이 측정되지 않아요!");
//    }

    @Test
    public void getInterpretedUrl_호출시__예약어를_해석한_새로운_URL을_반환한다() {
        subject.getInterpretedUrl("http://www.naver.com?email={email}&ids={b-m-ids}", new Bundle());

        verify(mockFomesUrlHelper).interpretUrlParams(eq("http://www.naver.com?email={email}&ids={b-m-ids}"), any(Bundle.class));
    }

    @Test
    public void getDisplayedMissionList_호출시__뷰에_보여지는_미션리스트를_반환한다() {
        subject.load("5d1c5e695c20ca481f27a4ab");
        List<Mission> displayedMissionList =  subject.getDisplayedMissionList().toBlocking().single();

        System.out.println(displayedMissionList);

        assertDisplayMissionList(displayedMissionList);
    }

    private void assertDisplayMissionList(List<Mission> displayedMissionList) {
        assertThat(displayedMissionList.size()).isEqualTo(4);

        // 순서 정렬
        assertThat(displayedMissionList.get(0).getOrder()).isEqualTo(1);
        assertThat(displayedMissionList.get(1).getOrder()).isEqualTo(2);
        assertThat(displayedMissionList.get(2).getOrder()).isEqualTo(3);
        assertThat(displayedMissionList.get(3).getOrder()).isEqualTo(5);

        // 락 시퀀스
        assertThat(displayedMissionList.get(0).isCompleted()).isEqualTo(true);
        assertThat(displayedMissionList.get(0).isMandatory()).isEqualTo(false);
        assertThat(displayedMissionList.get(0).isRepeatable()).isEqualTo(false);
        assertThat(displayedMissionList.get(0).isLocked()).isEqualTo(false);

        assertThat(displayedMissionList.get(1).isCompleted()).isEqualTo(false);
        assertThat(displayedMissionList.get(1).isMandatory()).isEqualTo(true);
        assertThat(displayedMissionList.get(1).isRepeatable()).isEqualTo(true);
        assertThat(displayedMissionList.get(1).isLocked()).isEqualTo(false);

        // 락 이후에는 전부 락
        assertThat(displayedMissionList.get(2).isLocked()).isEqualTo(true);
        assertThat(displayedMissionList.get(3).isLocked()).isEqualTo(true);
    }

    @Test
    public void requestToAttendBetaTest_호출시__해당_베타테스트에_참여요청을_보낸다() {
        subject.load("5d1c5e695c20ca481f27a4ab");
        subject.requestToAttendBetaTest();

        verify(mockBetaTestService).postAttendBetaTest("5d1c5e695c20ca481f27a4ab");
        assertThat(subject.betaTest.isAttended()).isTrue();
        verify(mockView).refreshMissionList();
    }

    @Test
    public void requestToCompleteMission_호출시__해당_미션_완료요청을_보낸다() {
        when(mockMissionListAdapterModel.getPositionById("5d1ec8194400311578e996bd")).thenReturn(1);

        subject.load("5d1c5e695c20ca481f27a4ab");
        subject.requestToCompleteMission(new Mission().setId("5d1ec8194400311578e996bd"))
                .subscribe(new TestSubscriber<>());

        verify(mockBetaTestService).postCompleteMission("5d1c5e695c20ca481f27a4ab", "5d1ec8194400311578e996bd");
        assertThat(subject.betaTest.getMissions().get(0).isCompleted()).isTrue();
        verify(mockView).refreshMissionBelowAllChanged(1);
    }

    @Test
    public void updatePlayTime_호출시__특정_앱의_플레이시간을_가져온다() {
        // given
        when(mockAppUsageDataHelper.getUsageTime("com.goodcircle.comeonkitty", 1562198400000L)) // 2019-07-04T00:00:00.000Z
                .thenReturn(Observable.just(1000L));

        subject.load("5d1c5e695c20ca481f27a4ab");
        Mission actualMissionItem = Observable.from(subject.betaTest.getMissions()).filter(mission -> "5d1ec8194400311578e996bd".equals(mission.getId())).toBlocking().single();
        actualMissionItem.setTotalPlayTime(0L);

        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();
        subject.updatePlayTime("5d1ec8194400311578e996bd", "com.goodcircle.comeonkitty")
                .subscribe(testSubscriber);

        testSubscriber.assertValue(1000L);
    }

    @Ignore
    @Test
    public void updatePlayTime_호출시__특정_앱의_플레이시간을_가져와_뷰를_업데이트_한다() {
        // given
        when(mockMissionListAdapterModel.getPositionById("5d1ec8194400311578e996bd")).thenReturn(1);
        when(mockAppUsageDataHelper.getUsageTime("com.goodcircle.comeonkitty", 1562198400000L)) // 2019-07-04T00:00:00.000Z
                .thenReturn(Observable.just(1000L));

        subject.load("5d1c5e695c20ca481f27a4ab");
        Mission actualMissionItem = Observable.from(subject.betaTest.getMissions()).filter(mission -> "5d1ec8194400311578e996bd".equals(mission.getId())).toBlocking().single();
        actualMissionItem.setTotalPlayTime(0L);

        // when
        subject.updatePlayTime("5d1ec8194400311578e996bd", "com.goodcircle.comeonkitty")
                .subscribe(new TestSubscriber<>());

        // then
        long actualPlayTime = actualMissionItem.getTotalPlayTime();

        verify(mockAppUsageDataHelper).getUsageTime(eq("com.goodcircle.comeonkitty"), eq(1562198400000L)); // 2019-07-04T00:00:00.000Z
        assertThat(actualPlayTime).isEqualTo(1000L);
        verify(mockView).refreshMissionBelowAllChanged(1);
    }

    @Test
    public void updatePlayTime_호출시__특정_앱의_플레이시간이_0이면_오류처리를_한다() {
        // given
        when(mockAppUsageDataHelper.getUsageTime("com.goodcircle.comeonkitty", 1562198400000L)) // 2019-07-04T00:00:00.000Z
                .thenReturn(Observable.just(0L));

        subject.load("5d1c5e695c20ca481f27a4ab");
        Mission actualMissionItem = Observable.from(subject.betaTest.getMissions()).filter(mission -> "5d1ec8194400311578e996bd".equals(mission.getId())).toBlocking().single();
        long oldTotalPlayTime = actualMissionItem.getTotalPlayTime().longValue();

        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();

        // when
        subject.updatePlayTime("5d1ec8194400311578e996bd", "com.goodcircle.comeonkitty")
                .subscribe(testSubscriber);

        // then
        long actualPlayTime = actualMissionItem.getTotalPlayTime();

        assertThat(actualPlayTime).isEqualTo(oldTotalPlayTime);
        verify(mockView, never()).refreshMissionList();

        testSubscriber.assertError(IllegalStateException.class);
    }

    @Test
    public void updatePlayTime_호출시__패키지명이_없으면_오류처리를_한다() {
        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();

        subject.updatePlayTime("5d1ec8194400311578e996bd", "")
                .subscribe(testSubscriber);

        testSubscriber.assertError(IllegalArgumentException.class);
    }

    @Test
    public void displayMissionList_호출시__화면표시용_미션리스트를_다시_세팅하고__미션_리스트뷰를_새로고침한다() {
        subject.load("5d1c5e695c20ca481f27a4ab");
        subject.displayMissionList();

        verify(mockMissionListAdapterModel).clear();
        ArgumentCaptor<List<Mission>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockMissionListAdapterModel).addAll(argumentCaptor.capture());
        assertDisplayMissionList(argumentCaptor.getValue());
        verify(mockView).refreshMissionList();
    }

    @Test
    public void displayMission_호출시__화면표시용_미션리스트를_다시_세팅하고__해당_미션_이하의_리스트뷰를_새로고침한다() {
        when(mockMissionListAdapterModel.getPositionById("5d1ec8024400311578e996bb")).thenReturn(3);

        subject.load("5d1c5e695c20ca481f27a4ab");
        subject.displayMission("5d1ec8024400311578e996bb"); // 3번째 미션

        verify(mockMissionListAdapterModel).clear();
        ArgumentCaptor<List<Mission>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockMissionListAdapterModel).addAll(argumentCaptor.capture());
        assertDisplayMissionList(argumentCaptor.getValue());
        verify(mockView).refreshMissionBelowAllChanged(3);
    }

    private BetaTest getDummyBetaTestDetail() {
        String json = "{" +
                "  \"_id\": \"5d1c5e695c20ca481f27a4ab\",\n" +
                "  \"title\": \"[이리와 고양아] 게임 테스트\",\n" +
                "  \"description\": \"\uD83D\uDC31 퍼즐을 풀어 냥줍한 고양이들과 함께하는 즐거운 시간!!\",\n" +
                "  \"purpose\": null,\n" +
                "  \"tags\": [\n" +
                "    \"냥줍\",\n" +
                "    \"퍼즐\",\n" +
                "    \"고양이\",\n" +
                "    \"귀여움\",\n" +
                "    \"수집\",\n" +
                "    \"육성\",\n" +
                "    \"시뮬레이션\"\n" +
                "  ],\n" +
                "  \"coverImageUrl\": \"https://i.imgur.com/Savbd4p.png\",\n" +
                "  \"iconImageUrl\": \"https://i.imgur.com/8yd6RCh.png\",\n" +
                "  \"openDate\": \"2019-07-04T00:00:00.000Z\",\n" +
                "  \"closeDate\": \"2119-07-10T14:59:59.998Z\",\n" +
                "  \"rewards\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"order\": 2,\n" +
                "        \"iconImageUrl\": \"https://i.imgur.com/6RaZ7vI.png\",\n" +
                "        \"title\": \"테스트 성실상(20명)\",\n" +
                "        \"content\": \"문상 1천원\",\n" +
                "        \"userIds\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"order\": 3,\n" +
                "        \"iconImageUrl\": \"https://i.imgur.com/6RaZ7vI.png\",\n" +
                "        \"title\": \"테스트 성실상333(30명)\",\n" +
                "        \"content\": \"문상 3천원\",\n" +
                "        \"userIds\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"order\": 1,\n" +
                "        \"iconImageUrl\": \"https://i.imgur.com/ybuI732.png\",\n" +
                "        \"title\": \"테스트 수석(1명)\",\n" +
                "        \"content\": \"문상 5천원\",\n" +
                "        \"userIds\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"missions\": [\n" +
                "    {\n" +
                "      \"order\": 2,\n" +
                "      \"description\": \"[이리와 고양아] 플레이 인증\",\n" +
                "      \"descriptionImageUrl\": \"\",\n" +
                "        \"type\": \"survey\",\n" +
                "      \"guide\": \"* 솔직하고 구체적으로 의견을 적어주시는게 제일 중요합니다!\\n* 불성실한 응답은 보상지급 대상자에서 제외될 수 있습니다.\",\n" +
                "        \"title\": \"스샷 인증하라!\",\n" +
                "        \"actionType\": \"internal_web\",\n" +
                "        \"action\": \"https://docs.google.com/forms/d/e/1FAIpQLSdxI2s694nLTVk4i7RMkkrtr-K_0s7pSKfUnRusr7348nQpJg/viewform?usp=pp_url&entry.1042588232={email}\",\n" +
                "        \"_id\": \"5d1ec8254400311578e996be\",\n" +
                "        \"isCompleted\": false,\n" +
                "        \"isRepeatable\": true,\n" +
                "        \"isMandatory\": true\n" +
                "    },\n" +
                "    {\n" +
                "      \"order\": 1,\n" +
                "      \"description\": \"[이리와 고양아] 게임을 30분 이상 플레이해주세요.\",\n" +
                "      \"descriptionImageUrl\": \"\",\n" +
                "      \"guide\": \"* 위 버튼을 누르면, 테스트 대상 게임 무단배포 금지에 동의로 간주합니다.\",\n" +
                "      \"_id\": \"5d1ec8024400311578e996bb\",\n" +
                "        \"type\": \"install\",\n" +
                "        \"title\": \"게임을 플레이 하라!\",\n" +
                "        \"actionType\": \"link\",\n" +
                "        \"action\": \"https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty\",\n" +
                "       \"packageName\": \"com.goodcircle.comeonkitty\"," +
                "        \"_id\": \"5d1ec8194400311578e996bd\",\n" +
                "        \"isCompleted\": true\n" +
                "    },\n" +
                "    {\n" +
                "      \"order\": 3,\n" +
                "      \"title\": \"3단계 미션\",\n" +
                "      \"description\": \"[이리와 고양아] 두번째 설문\",\n" +
                "      \"descriptionImageUrl\": \"\",\n" +
                "      \"guide\": \"* 위 버튼을 누르면, 테스트 대상 게임 무단배포 금지에 동의로 간주합니다.\",\n" +
                "      \"_id\": \"5d1ec8024400311578e996bb\",\n" +
                "        \"type\": \"play\",\n" +
                "        \"title\": \"게임을 플레이 하라!\",\n" +
                "        \"actionType\": \"link\",\n" +
                "        \"action\": \"https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty\",\n" +
                "        \"_id\": \"5d1ec8194400311578e996b1\",\n" +
                "        \"isCompleted\": false\n" +
                "    },\n" +
                "    {\n" +
                "      \"order\": 5,\n" +
                "      \"description\": \"[이리와 고양아] 세번째 설문\",\n" +
                "      \"descriptionImageUrl\": \"\",\n" +
                "      \"guide\": \"* 솔직하고 구체적으로 의견을 적어주시는게 제일 중요합니다!\\n* 불성실한 응답은 보상지급 대상자에서 제외될 수 있습니다.\",\n" +
                "      \"_id\": \"5d1ec8094400311578e996bc\",\n" +
                "        \"title\": \"의견을 작성하라!\",\n" +
                "        \"actionType\": \"link\",\n" +
                "        \"action\": \"https://docs.google.com/forms/d/e/1FAIpQLSdxI2s694nLTVk4i7RMkkrtr-K_0s7pSKfUnRusr7348nQpJg/viewform?usp=pp_url&internal_web=true&entry.1042588232={email}\",\n" +
                "        \"_id\": \"5d1ec8254400311578e996b3\",\n" +
                "        \"isCompleted\": false,\n" +
                "        \"isRepeatable\": true,\n" +
                "        \"isMandatory\": true\n" +
                "    }\n" +
                "  ],\n" +
                "  \"currentDate\": \"2019-08-14T09:52:23.879Z\",\n" +
                "  \"isAttended\": true\n" +
                "}";

        return new Gson().fromJson(json, BetaTest.class);
    }
}
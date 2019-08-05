package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.network.vo.Mission;
import com.formakers.fomes.helper.FomesUrlHelper;
import com.formakers.fomes.main.contract.BetaTestDetailContract;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BetaTestDetailPresenterTest {

    @Mock BetaTestDetailContract.View mockView;
    @Mock AnalyticsModule.Analytics mockAnalytics;
    @Mock EventLogService mockEventLogService;
    @Mock BetaTestService mockBetaTestService;
    @Mock FomesUrlHelper mockFomesUrlHelper;

    BetaTestDetailPresenter subject;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
        RxJavaHooks.setOnComputationScheduler(scheduler -> Schedulers.immediate());

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        MockitoAnnotations.initMocks(this);

        when(mockBetaTestService.getDetailBetaTest("5d1c5e695c20ca481f27a4ab"))
                .thenReturn(Single.just(getDummyBetaTestDetail()));
        when(mockBetaTestService.postCompleteBetaTest("5d1ec8194400311578e996bd"))
                .thenReturn(Completable.complete());
        when(mockBetaTestService.getMissionProgress("5d1ec8094400311578e996bc"))
                .thenReturn(Observable.just(new Mission.MissionItem()));

        when(mockEventLogService.sendEventLog(any(EventLog.class))).thenReturn(Completable.complete());

        subject = new BetaTestDetailPresenter(mockView, mockAnalytics, mockEventLogService, mockBetaTestService, mockFomesUrlHelper);
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

        assertThat(actualBetaTest.getTotalItemCount()).isEqualTo(2);
        assertThat(actualBetaTest.getCompletedItemCount()).isEqualTo(1);
        assertThat(actualBetaTest.getRewards().getList().size()).isEqualTo(3);
        assertThat(actualBetaTest.getRewards().getList().get(0).getOrder()).isEqualTo(1);
        assertThat(actualBetaTest.getRewards().getList().get(1).getOrder()).isEqualTo(2);
        assertThat(actualBetaTest.getRewards().getList().get(2).getOrder()).isEqualTo(3);
        assertThat(actualBetaTest.getMissions().size()).isEqualTo(2);
        assertThat(actualBetaTest.getMissions().get(0).getOrder()).isEqualTo(1);
        assertThat(actualBetaTest.getMissions().get(1).getOrder()).isEqualTo(2);
    }

    @Test
    public void requestCompleteMissionItem_호출시__해당_미션_아이템에_대한_완료처리를_요청한다() {
        subject.requestCompleteMissionItem("5d1ec8194400311578e996bd");

        verify(mockView).unlockMissions();
    }

    @Test
    public void refreshMissionProgress_호출시__해당_미션의_진행상태를_요청한다() {
        subject.refreshMissionProgress("5d1ec8094400311578e996bc");

        verify(mockBetaTestService).getMissionProgress(eq("5d1ec8094400311578e996bc"));
    }

    @Test
    public void getInterpretedUrl_호출시__예약어를_해석한_새로운_URL을_반환한다() {
        subject.getInterpretedUrl("http://www.naver.com?email={email}");

        verify(mockFomesUrlHelper).interpretUrlParams(eq("http://www.naver.com?email={email}"));
    }

    private BetaTest getDummyBetaTestDetail() {
        String json = "{" +
                "\"_id\":\"5d1c5e695c20ca481f27a4ab\",\n" +
                "\"tags\":[\n" +
                "    \"냥줍\",\n" +
                "    \"퍼즐\",\n" +
                "    \"고양이\",\n" +
                "    \"귀여움\",\n" +
                "    \"수집\",\n" +
                "    \"육성\",\n" +
                "    \"시뮬레이션\"\n" +
                "],\n" +
                "\"title\":\"[이리와 고양아] 게임 테스트\",\n" +
                "\"description\":\"\uD83D\uDC31 퍼즐을 풀어 냥줍한 고양이들과 함께하는 즐거운 시간!!\",\n" +
                "\"overviewImageUrl\":\"https://i.imgur.com/Savbd4p.png\",\n" +
                "\"iconImageUrl\":\"https://i.imgur.com/8yd6RCh.png\",\n" +
                "\"openDate\":\"2019-07-04T00:00:00.000Z\",\n" +
                "\"closeDate\":\"2119-07-10T14:59:59.998Z\",\n" +
                "\"rewards\":{\n" +
                "    \"list\":[\n" +
                "        {\n" +
                "            \"order\":2,\n" +
                "            \"iconImageUrl\":\"https://i.imgur.com/6RaZ7vI.png\",\n" +
                "            \"title\":\"테스트 성실상(20명)\",\n" +
                "            \"content\":\"문상 1천원\",\n" +
                "            \"userIds\":[\n" +
                "\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"order\":3,\n" +
                "            \"iconImageUrl\":\"https://i.imgur.com/6RaZ7vI.png\",\n" +
                "            \"title\":\"테스트 성실상333(30명)\",\n" +
                "            \"content\":\"문상 3천원\",\n" +
                "            \"userIds\":[\n" +
                "\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"order\":1,\n" +
                "            \"iconImageUrl\":\"https://i.imgur.com/ybuI732.png\",\n" +
                "            \"title\":\"테스트 수석(1명)\",\n" +
                "            \"content\":\"문상 5천원\",\n" +
                "            \"userIds\":[\n" +
                "\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "},\n" +
                "\"missions\":[\n" +
                "    {\n" +
                "        \"order\":2,\n" +
                "        \"title\":\"2단계 미션\",\n" +
                "        \"description\":\"[이리와 고양아] 에 대한 구체적인 의견을 작성해주세요.\",\n" +
                "        \"descriptionImageUrl\":\"\",\n" +
                "        \"iconImageUrl\":\"https://i.imgur.com/Gk9byou.png\",\n" +
                "        \"items\":[\n" +
                "            {\n" +
                "                \"order\":1,\n" +
                "                \"title\":\"의견 작성\",\n" +
                "                \"actionType\":\"link\",\n" +
                "                \"action\":\"https://docs.google.com/forms/d/e/1FAIpQLSdxI2s694nLTVk4i7RMkkrtr-K_0s7pSKfUnRusr7348nQpJg/viewform?usp=pp_url&internal_web=true&entry.1042588232=\",\n" +
                "                \"_id\":\"5d1ec8254400311578e996be\",\n" +
                "                \"isCompleted\":true\n" +
                "            }\n" +
                "        ],\n" +
                "        \"guide\":\"* 솔직하고 구체적으로 의견을 적어주시는게 제일 중요합니다!\\n* 불성실한 응답은 보상지급 대상자에서 제외될 수 있습니다.\",\n" +
                "        \"_id\":\"5d1ec8094400311578e996bc\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"order\":1,\n" +
                "        \"title\":\"1단계 미션\",\n" +
                "        \"description\":\"[이리와 고양아] 게임을 30분 이상 플레이해주세요.\",\n" +
                "        \"descriptionImageUrl\":\"\",\n" +
                "        \"iconImageUrl\":\"https://i.imgur.com/0ZMdxPO.png\",\n" +
                "        \"items\":[\n" +
                "            {\n" +
                "                \"type\":\"play\",\n" +
                "                \"order\":1,\n" +
                "                \"title\":\"게임 플레이\",\n" +
                "                \"actionType\":\"link\",\n" +
                "                \"action\":\"https://play.google.com/store/apps/details?id=com.goodcircle.comeonkitty&email=\",\n" +
                "                \"postCondition\":{\n" +
                "                    \"packageName\":\"com.goodcircle.comeonkitty\",\n" +
                "                    \"playTime\":1800000\n" +
                "                },\n" +
                "                \"_id\":\"5d1ec8194400311578e996bd\",\n" +
                "                \"isCompleted\":false\n" +
                "            }\n" +
                "        ],\n" +
                "        \"guide\":\"\\n* 위 버튼을 누르면, 테스트 대상 게임 무단배포 금지에 동의로 간주합니다.\",\n" +
                "        \"_id\":\"5d1ec8024400311578e996bb\"\n" +
                "    }\n" +
                "],\n" +
                "\"currentDate\":\"2019-07-17T08:40:52.362Z\"\n" +
                "}";

        return new Gson().fromJson(json, BetaTest.class);
    }
}
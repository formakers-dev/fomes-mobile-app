package com.formakers.fomes.betatest;

import com.formakers.fomes.R;
import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.AndroidNativeHelper;
import com.formakers.fomes.common.helper.FomesUrlHelper;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;
import com.formakers.fomes.common.network.vo.Mission;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.NoSuchElementException;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class FinishedBetaTestDetailPresenterTest {

    @Mock FinishedBetaTestDetailContract.View mockView;
    @Mock AnalyticsModule.Analytics mockAnalytics;
    @Mock ImageLoader mockImageLoader;
    @Mock BetaTestService mockBetaTestService;
    @Mock FomesUrlHelper mockFomesUrlHelper;
    @Mock AndroidNativeHelper mockAndroidNativeHelper;
    @Mock FirebaseRemoteConfig mockRemoteConfig;

    @Mock FinishedBetaTestAwardPagerAdapterContract.Model mockFinishedBetaTestAwardPagerModel;

    BetaTest betaTest;
    FinishedBetaTestDetailPresenter subject;

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

        BetaTest.Epilogue epilogue = new BetaTest.Epilogue().setCompanyName("게임사이름")
                .setCompanySays("게임사소감").setCompanyImageUrl("게임사이미지링크").setDeeplink("에필로그링크");
        when(mockBetaTestService.getEpilogue("betaTestId")).thenReturn(Single.just(epilogue));
        when(mockBetaTestService.getCompletedMissions("betaTestId"))
                .thenReturn(Single.just(
                        Lists.newArrayList(
                                new Mission().setTitle("미션1").setRecheckable(true).setCompleted(true),
                                new Mission().setTitle("미션2").setRecheckable(false).setCompleted(true))));

        List<AwardRecord> awardRecords = Lists.newArrayList(
                new AwardRecord().setNickName("닉네임1").setTypeCode(9000).setRewards(new AwardRecord.Reward().setDescription("문화상품권 30000원")),
                new AwardRecord().setNickName("닉네임2").setTypeCode(7000).setRewards(new AwardRecord.Reward().setDescription("문화상품권 5000원")),
                new AwardRecord().setNickName("닉네임3").setTypeCode(5000).setRewards(new AwardRecord.Reward().setDescription("문화상품권 1000원")),
                new AwardRecord().setNickName("닉네임4").setTypeCode(5000).setRewards(new AwardRecord.Reward().setDescription("문화상품권 1000원"))
        );
        when(mockBetaTestService.getAwardRecords("betaTestId")).thenReturn(Single.just(awardRecords));

        subject = new FinishedBetaTestDetailPresenter(mockView, mockAnalytics, mockImageLoader, mockBetaTestService, mockFomesUrlHelper, mockAndroidNativeHelper, mockRemoteConfig);

        betaTest = getDummyBetaTestDetail(); // TODO :종료된 테스트 데이터로 변경 필요
        subject.setBetaTest(betaTest);
        subject.setFinishedBetaTestAwardPagerAdapterModel(mockFinishedBetaTestAwardPagerModel);
    }

    @Test
    public void requestEpilogueAndAwards_호출시__해당_베타테스트의_에필로그_정보가_있으면__뷰에_바인딩한다() {
        subject.requestEpilogueAndAwards("betaTestId");

        verify(mockBetaTestService).getEpilogue("betaTestId");

        ArgumentCaptor<BetaTest.Epilogue> epilogueArgumentCaptor = ArgumentCaptor.forClass(BetaTest.Epilogue.class);
        verify(mockView).bindEpilogueView(epilogueArgumentCaptor.capture());
        BetaTest.Epilogue actualEpilogue = epilogueArgumentCaptor.getValue();

        assertThat(actualEpilogue.getCompanyName()).isEqualTo("게임사이름");
        assertThat(actualEpilogue.getCompanySays()).isEqualTo("게임사소감");
        assertThat(actualEpilogue.getCompanyImageUrl()).isEqualTo("게임사이미지링크");
        assertThat(actualEpilogue.getDeeplink()).isEqualTo("에필로그링크");
    }

    @Test
    public void requestEpilogueAndAwards_호출시__해당_베타테스트의_에필로그_정보가_있으면__시상식정보를_요청하고_뷰에_셋팅한다() {
        subject.requestEpilogueAndAwards("betaTestId");

        verify(mockBetaTestService).getAwardRecords("betaTestId");

        ArgumentCaptor<List<AwardRecord>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockFinishedBetaTestAwardPagerModel).addAll(argumentCaptor.capture());
        verify(mockView).refreshAwardPagerView();

        List<AwardRecord> actual = argumentCaptor.getValue();
        assertThat(actual.size()).isEqualTo(4);

        assertThat(actual.get(0).getNickName()).isEqualTo("닉네임1");
        assertThat(actual.get(0).getTypeCode()).isEqualTo(9000);
        assertThat(actual.get(0).getReward().getDescription()).isEqualTo("문화상품권 30000원");

        assertThat(actual.get(1).getNickName()).isEqualTo("닉네임2");
        assertThat(actual.get(1).getTypeCode()).isEqualTo(7000);
        assertThat(actual.get(1).getReward().getDescription()).isEqualTo("문화상품권 5000원");

        assertThat(actual.get(2).getNickName()).isEqualTo("닉네임3");
        assertThat(actual.get(2).getTypeCode()).isEqualTo(5000);
        assertThat(actual.get(2).getReward().getDescription()).isEqualTo("문화상품권 1000원");

        assertThat(actual.get(3).getNickName()).isEqualTo("닉네임4");
        assertThat(actual.get(3).getTypeCode()).isEqualTo(5000);
        assertThat(actual.get(3).getReward().getDescription()).isEqualTo("문화상품권 1000원");
    }

    @Test
    public void requestEpilogueAndAwards_호출시__해당_베타테스트의_에필로그_정보가_없으면__뷰를_비활성화한다() {
        when(mockBetaTestService.getEpilogue("betaTestId"))
                .thenReturn(Single.error(new HttpException(Response.error(404, ResponseBody.create(null, "")))));

        subject.requestEpilogueAndAwards("betaTestId");

        verify(mockBetaTestService).getEpilogue("betaTestId");
        verify(mockView).disableEpilogueView();
    }

    @Test
    public void requestEpilogueAndAwards_호출시__해당_베타테스트의_에필로그_정보가_없으면__시상식대신_리워드정보를_띄운다() {
        when(mockBetaTestService.getEpilogue("betaTestId"))
                .thenReturn(Single.error(new HttpException(Response.error(404, ResponseBody.create(null, "")))));

        subject.requestEpilogueAndAwards("betaTestId");

        verify(mockView).bindAwardRecordsWithRewardItems(anyList());
    }

    @Test
    public void requestAwardRecord_호출시__NoSuchElementException이_발생하면__수상_정보를_뷰에서_숨긴다() {
        when(mockBetaTestService.getAwardRecords("betaTestId")).thenReturn(Single.error(new NoSuchElementException()));
        List<BetaTest.Rewards.RewardItem> mockRewardItems = Mockito.mock(List.class);

        subject.requestEpilogueAndAwards("betaTestId");

        verify(mockBetaTestService).getAwardRecords("betaTestId");
        verify(mockView).hideAwardsView();
    }

    @Test
    public void requestRecheckableMissions_호출시__완료한_미션리스트를_요청하고_확인가능한_답변들만__뷰에_바인딩한다() {
        subject.requestRecheckableMissions("betaTestId");

        verify(mockBetaTestService).getCompletedMissions("betaTestId");

        ArgumentCaptor<List<Mission>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView).bindMyAnswersView(argumentCaptor.capture());
        List<Mission> actual = argumentCaptor.getValue();

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(0).getTitle()).isEqualTo("미션1");
        assertThat(actual.get(0).isRecheckable()).isEqualTo(true);
        assertThat(actual.get(0).isCompleted()).isEqualTo(true);
    }

    @Test
    public void requestRecheckableMissions_호출시__완료한_미션리스트가_없으면__뷰에_바인딩하지않는다() {
        when(mockBetaTestService.getCompletedMissions("betaTestId"))
                .thenReturn(Single.just(Lists.emptyList()));

        subject.requestRecheckableMissions("betaTestId");

        verify(mockBetaTestService).getCompletedMissions("betaTestId");
        verify(mockView, never()).bindMyAnswersView(any());
    }

    @Test
    public void emitRecheckMyAnswer_호출시__공지팝업을_띄운다() {
        Mission missionItem = new Mission().setTitle("test");

        subject.emitRecheckMyAnswer(missionItem);

        verify(mockView).showNoticePopupView(eq(R.string.finished_betatest_recheck_my_answer_title),
                eq(R.string.finished_betatest_recheck_my_answer_popup_subtitle),
                eq(R.drawable.notice_recheck_my_answer),
                eq(R.string.finished_betatest_recheck_my_answer_popup_positive_button_text),
                any());
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
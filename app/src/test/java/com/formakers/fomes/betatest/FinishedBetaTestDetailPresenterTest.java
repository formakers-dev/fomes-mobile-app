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

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

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
        when(mockBetaTestService.getAwardRecords("betaTestId")).thenReturn(Single.just(Lists.newArrayList(new AwardRecord().setNickName("닉네임").setType("best").setRewards(new AwardRecord.Reward().setDescription("description")))));
        when(mockBetaTestService.getCompletedMissions("betaTestId"))
                .thenReturn(Single.just(
                        Lists.newArrayList(
                                new Mission().setTitle("미션1").setRecheckable(true).setCompleted(true),
                                new Mission().setTitle("미션2").setRecheckable(false).setCompleted(true))));

        subject = new FinishedBetaTestDetailPresenter(mockView, mockAnalytics, mockImageLoader, mockBetaTestService, mockFomesUrlHelper, mockAndroidNativeHelper);
    }

    @Test
    public void requestEpilogue_호출시__해당_베타테스트의_에필로그_정보를_요청하고__뷰에_바인딩한다() {
        subject.requestEpilogue("betaTestId");

        verify(mockBetaTestService).getEpilogue("betaTestId");

        ArgumentCaptor<BetaTest.Epilogue> epilogueArgumentCaptor = ArgumentCaptor.forClass(BetaTest.Epilogue.class);
        verify(mockView).bindEpilogue(epilogueArgumentCaptor.capture());
        BetaTest.Epilogue actualEpilogue = epilogueArgumentCaptor.getValue();

        assertThat(actualEpilogue.getCompanyName()).isEqualTo("게임사이름");
        assertThat(actualEpilogue.getCompanySays()).isEqualTo("게임사소감");
        assertThat(actualEpilogue.getCompanyImageUrl()).isEqualTo("게임사이미지링크");
        assertThat(actualEpilogue.getDeeplink()).isEqualTo("에필로그링크");
    }

    @Test
    public void requestEpilogue_호출시__해당_베타테스트의_에필로그_정보가_없으면__뷰를_비활성화한다() {
        when(mockBetaTestService.getEpilogue("betaTestId"))
                .thenReturn(Single.error(new HttpException(Response.error(404, ResponseBody.create(null, "")))));

        subject.requestEpilogue("betaTestId");

        verify(mockBetaTestService).getEpilogue("betaTestId");
        verify(mockView).disableEpilogue();
    }

    @Test
    public void requestAwardRecordOfBest_호출시__해당_베타테스트의_최고수상자_정보를_요청하고__뷰에_바인딩한다() {
        subject.requestAwardRecordOfBest("betaTestId");

        verify(mockBetaTestService).getAwardRecords("betaTestId");

        ArgumentCaptor<AwardRecord> argumentCaptor = ArgumentCaptor.forClass(AwardRecord.class);
        verify(mockView).bindAwards(argumentCaptor.capture());
        AwardRecord actual = argumentCaptor.getValue();

        assertThat(actual.getNickName()).isEqualTo("닉네임");
        assertThat(actual.getType()).isEqualTo("best");
        assertThat(actual.getReward().getDescription()).isEqualTo("description");
    }

    @Test
    public void requestRecheckableMissions_호출시__완료한_미션리스트를_요청하고_확인가능한_답변들만__뷰에_바인딩한다() {
        subject.requestRecheckableMissions("betaTestId");

        verify(mockBetaTestService).getCompletedMissions("betaTestId");

        ArgumentCaptor<List<Mission>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockView).bindMyAnswers(argumentCaptor.capture());
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
        verify(mockView, never()).bindMyAnswers(any());
    }

    @Test
    public void emitRecheckMyAnswer_호출시__공지팝업을_띄운다() {
        Mission missionItem = new Mission().setTitle("test");

        subject.emitRecheckMyAnswer(missionItem);

        verify(mockView).showNoticePopup(eq(R.string.finished_betatest_recheck_my_answer_title),
                eq(R.string.finished_betatest_recheck_my_answer_popup_subtitle),
                eq(R.drawable.notice_recheck_my_answer),
                eq(R.string.finished_betatest_recheck_my_answer_popup_positive_button_text),
                any());
    }
}
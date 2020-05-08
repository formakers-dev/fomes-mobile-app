package com.formakers.fomes.betatest;

import com.formakers.fomes.common.dagger.AnalyticsModule;
import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.vo.AwardRecord;
import com.formakers.fomes.common.network.vo.BetaTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class BetaTestCertificatePresenterTest {

    @Mock BetaTestCertificateContract.View mockView;
    @Mock AnalyticsModule.Analytics mockAnalytics;
    @Mock ImageLoader mockImageLoader;
    @Mock BetaTestService mockBetaTestService;

    BetaTestCertificatePresenter subject;

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

        BetaTest betaTest = new BetaTest()
                .setTitle("[테스트] 게임 테스트")
                .setIconImageUrl("아이콘링크")
                .setCompleted(true);

        AwardRecord awardRecord = new AwardRecord()
                .setType("best");

        when(mockBetaTestService.getDetailBetaTest("betaTestId")).thenReturn(Single.just(betaTest));
        when(mockBetaTestService.getAwardRecord("betaTestId")).thenReturn(Single.just(awardRecord));

        subject = new BetaTestCertificatePresenter(mockView, Single.just("dummyNickName"), mockAnalytics, mockImageLoader, mockBetaTestService);
    }

    @Test
    public void requestBetaTestDetail_호출시__베타테스트의_상세_정보와_수상_정보를_요청하고__뷰에_바인딩한다() {
        subject.requestBetaTestDetail("betaTestId");

        verify(mockBetaTestService).getDetailBetaTest("betaTestId");
        verify(mockBetaTestService).getAwardRecord("betaTestId");

        ArgumentCaptor<BetaTest> betaTestArgumentCaptor = ArgumentCaptor.forClass(BetaTest.class);
        verify(mockView).bindBetaTestDetail(betaTestArgumentCaptor.capture());

        ArgumentCaptor<AwardRecord> awardRecordArgumentCaptor = ArgumentCaptor.forClass(AwardRecord.class);
        verify(mockView).bindCertificate(betaTestArgumentCaptor.capture(), awardRecordArgumentCaptor.capture());

        BetaTest actualBetaTest = betaTestArgumentCaptor.getValue();
        AwardRecord actualAwardRecord = awardRecordArgumentCaptor.getValue();

        assertThat(actualBetaTest.getTitle()).isEqualTo("[테스트] 게임 테스트");
        assertThat(actualBetaTest.getIconImageUrl()).isEqualTo("아이콘링크");
        assertThat(actualAwardRecord.getType()).isEqualTo("best");
    }

    @Test
    public void requestBetaTestDetail_호출시__해당_베타테스트를_완료하지_않은_경우_뷰에_바인딩하지_않는다() {
        when(mockBetaTestService.getDetailBetaTest("betaTestId")).thenReturn(Single.just(new BetaTest().setCompleted(false)));

        subject.requestBetaTestDetail("betaTestId");

        verify(mockBetaTestService).getDetailBetaTest("betaTestId");
        verify(mockBetaTestService).getAwardRecord("betaTestId");

        verify(mockView, never()).bindBetaTestDetail(any());
        verify(mockView, never()).bindCertificate(any(), any());
    }

    @Test
    public void requestUserNickName_호출시__유저_닉네임_정보를_요청하고__뷰에_바인딩한다() {
        subject.requestUserNickName();

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockView).bindUserNickName(stringArgumentCaptor.capture());
        String actualNickName = stringArgumentCaptor.getValue();

        assertThat(actualNickName).isEqualTo("dummyNickName");
    }
}
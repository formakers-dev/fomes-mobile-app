package com.appbee.appbeemobile.activity;

import android.content.Intent;

import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.network.UserService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

public class LoadingActivityTest extends BaseActivityTest<LoadingActivity> {

    private LoadingActivity subject;
    private Unbinder binder;

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;

    @Inject
    UserService mockUserService;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;


    public LoadingActivityTest() {
        super(LoadingActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        subject = getActivity();
        binder = ButterKnife.bind(this, subject);
    }

    @After
    public void tearDown() throws Exception {
        if (binder != null) {
            binder.unbind();
        }
        RxJavaHooks.reset();
        super.tearDown();
    }

    @Test
    public void onPostCreate호출시_AppUsage데이터를_전송한다() throws Exception {
        ArgumentCaptor<AppUsageDataHelper.SendDataCallback> sendDataCallbackArgumentCaptor = ArgumentCaptor.forClass(AppUsageDataHelper.SendDataCallback.class);

        verify(mockAppUsageDataHelper).sendAppUsages(sendDataCallbackArgumentCaptor.capture());
        assertThat(sendDataCallbackArgumentCaptor.getValue()).isEqualTo(subject.appUsageDataHelperSendDataCallback);
    }

    @Test
    public void 통계데이터_서버전송_완료콜백호출시_분셕결과화면으로_이동한다() throws Exception {
        subject.appUsageDataHelperSendDataCallback.onSuccess();

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(OnboardingAnalysisActivity.class.getSimpleName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void 통계데이터_서버전송_실패콜백호출시_에러문구를_출력한다() throws Exception {
        subject.appUsageDataHelperSendDataCallback.onFail();

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("데이터 전송에 실패하였습니다.");
    }
}
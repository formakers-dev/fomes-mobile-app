package com.appbee.appbeemobile.activity;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.AppUsageDataHelper;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.UserService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Completable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LoadingActivityTest extends ActivityTest {

    private ActivityController<LoadingActivity> activityController;

    private Unbinder binder;

    @Inject
    AppUsageDataHelper mockAppUsageDataHelper;

    @Inject
    UserService mockUserService;

    @Inject
    LocalStorageHelper mockLocalStorageHelper;

    @Before
    public void setUp() throws Exception {
        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());

        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        activityController = Robolectric.buildActivity(LoadingActivity.class);

        when(mockUserService.sendUser(any(User.class))).thenReturn(Completable.complete());
    }

    @After
    public void tearDown() throws Exception {
        if (binder != null) {
            binder.unbind();
        }
        RxJavaHooks.reset();
    }

    private LoadingActivity createSubjectWithPostCreateLifecycle() {
        LoadingActivity subject = activityController.create().postCreate(null).get();
        binder = ButterKnife.bind(this, subject);
        return subject;
    }

    @Test
    public void onPostCreate호출시_통계데이터를_전송한다() throws Exception {
        LoadingActivity subject = createSubjectWithPostCreateLifecycle();

        ArgumentCaptor<AppUsageDataHelper.SendDataCallback> sendDataCallbackArgumentCaptor = ArgumentCaptor.forClass(AppUsageDataHelper.SendDataCallback.class);

        verify(mockAppUsageDataHelper).sendShortTermStatAndAppUsages(sendDataCallbackArgumentCaptor.capture());
        assertThat(sendDataCallbackArgumentCaptor.getValue()).isEqualTo(subject.appUsageDataHelperSendDataCallback);
    }

    @Test
    public void 통계데이터_서버전송_완료콜백호출시_분셕결과화면으로_이동한다() throws Exception {
        LoadingActivity subject = createSubjectWithPostCreateLifecycle();
        subject.appUsageDataHelperSendDataCallback.onSuccess();

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(OnboardingAnalysisActivity.class.getSimpleName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void 통계데이터_서버전송_실패콜백호출시_에러문구를_출력한다() throws Exception {
        LoadingActivity subject = createSubjectWithPostCreateLifecycle();
        subject.appUsageDataHelperSendDataCallback.onFail();

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("데이터 전송에 실패하였습니다.");
    }
}
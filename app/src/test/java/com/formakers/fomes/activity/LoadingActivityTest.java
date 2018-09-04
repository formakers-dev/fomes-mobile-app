package com.formakers.fomes.activity;

import android.content.Intent;

import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.helper.AppUsageDataHelper;
import com.formakers.fomes.helper.LocalStorageHelper;
import com.formakers.fomes.network.UserService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Completable;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public class LoadingActivityTest extends BaseActivityTest<LoadingActivity> {

    private LoadingActivity subject;

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

    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        super.tearDown();
    }

    @Test
    public void onPostCreate호출시_AppUsage데이터를_전송한다() throws Exception {
        when(mockAppUsageDataHelper.sendAppUsages()).thenReturn(Completable.complete());

        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        verify(mockAppUsageDataHelper).sendAppUsages();
    }

    @Test
    public void 통계데이터_서버전송_완료콜백호출시_분셕결과화면으로_이동한다() throws Exception {
        when(mockAppUsageDataHelper.sendAppUsages()).thenReturn(Completable.complete());

        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        Intent intent = shadowOf(subject).getNextStartedActivity();
        assertThat(intent.getComponent().getClassName()).contains(OnboardingAnalysisActivity.class.getSimpleName());
        assertThat(shadowOf(subject).isFinishing()).isTrue();
    }

    @Test
    public void 통계데이터_서버전송_실패콜백호출시_에러문구를_출력한다() throws Exception {
        when(mockAppUsageDataHelper.sendAppUsages()).thenReturn(Completable.error(new RuntimeException()));

        subject = getActivity(LIFECYCLE_TYPE_POST_CREATE);

        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("데이터 전송에 실패하였습니다.");
    }
}
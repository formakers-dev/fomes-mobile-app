package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.AppService;
import com.formakers.fomes.common.network.UserService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;

public class AppInfoDetailPresenterTest {

    @Mock private UserService mockUserService;
    @Mock private AppService mockAppService;

    private AppInfoDetailPresenter subject;


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

        subject = new AppInfoDetailPresenter(mockUserService, mockAppService);
    }

    @Test
    public void emitSaveToWishList_호출시__앱을_즐겨찾기에_추가하도록_서버에_요청한다() {
        subject.requestSaveToWishList("com.test");

        verify(mockUserService).requestSaveAppToWishList("com.test");
    }

    @Test
    public void emitRemoveFromWishList_호출시__앱을_즐겨찾기에서_삭제하도록_서버에_요청한다() {
        subject.requestRemoveFromWishList("com.test");

        verify(mockUserService).requestRemoveAppFromWishList("com.test");
    }

    @Test
    public void requestAppInfo_호출시__서버에_앱_정보를_요청한다() {
        subject.requestAppInfo("com.test.com");

        verify(mockAppService).requestAppInfo("com.test.com");
    }
}
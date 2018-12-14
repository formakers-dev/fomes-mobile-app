package com.formakers.fomes.wishList.presenter;

import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.wishList.contract.WishListContract;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Completable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WishListPresenterTest {

    @Mock WishListContract.View mockView;
    @Mock UserService mockUserService;

    private WishListPresenter subject;

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

        subject = new WishListPresenter(mockView, mockUserService);
    }

    @Test
    public void requestWishList__호출시_위시리스트를_서버에_요청한다() {
        subject.requestWishList();

        verify(mockUserService).requestWishList();
    }

    @Test
    public void requestRemoveFromWishList_호출시__특정앱을_위시리스트에서_삭제해줄것을_서버에_요청한다() {
        when(mockUserService.requestRemoveAppFromWishList(anyString()))
                .thenReturn(Completable.complete());

        subject.requestRemoveFromWishList("com.test");

        verify(mockUserService).requestRemoveAppFromWishList(eq("com.test"));
    }

    @Test
    public void requestRemoveFromWishList_호출시_서버로부터_성공응답을_받으면_뷰에서_해당앱을_삭제한다() {
        when(mockUserService.requestRemoveAppFromWishList(anyString()))
                .thenReturn(Completable.complete());

        subject.requestRemoveFromWishList("com.test");

        verify(mockView).removeApp(eq("com.test"));
    }

    @Test
    public void requestRemoveFromWishList_호출시_서버로부터_실패응답을_받으면_뷰에서_실패메시지를_띄운다() {
        when(mockUserService.requestRemoveAppFromWishList(anyString()))
                .thenReturn(Completable.error(new Throwable()));

        subject.requestRemoveFromWishList("com.test");

        verify(mockView).showToast(anyString());
    }
}
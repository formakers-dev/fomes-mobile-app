package com.formakers.fomes.wishList.presenter;

import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.wishList.contract.WishListContract;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import rx.Completable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WishListPresenterTest {

    @Mock private WishListContract.View mockView;
    @Mock private UserService mockUserService;

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

        when(mockUserService.requestRemoveAppFromWishList(anyString()))
                .thenReturn(Completable.complete());
    }

    @Test
    public void requestWishList__호출시_위시리스트를_서버에_요청한다() {
        subject.requestWishList();

        verify(mockUserService).requestWishList();
    }

    @Test
    public void requestRemoveFromWishList_호출시__특정앱을_위시리스트에서_제거해줄것을_서버에_요청한다() {
        subject.requestRemoveFromWishList("com.test");

        verify(mockUserService).requestRemoveAppFromWishList(eq("com.test"));
    }

    @Test
    public void requestRemoveFromWishList_호출시__제거할_앱의_packageName을_제거된_앱_리스트에_추가한다() {
        subject.requestRemoveFromWishList("com.test");

        ArrayList<String> removedPackageNames = subject.getRemovedPackageNames();

        assertThat(removedPackageNames.contains("com.test")).isTrue();
    }

    @Test
    public void requestRemoveFromWishList_호출시__서버로부터_성공응답을_받으면_뷰에서_해당앱을_화면에서_제거한다() {
        subject.requestRemoveFromWishList("com.test");

        verify(mockView).removeApp(eq("com.test"));
    }

    @Test
    public void requestRemoveFromWishList_호출시__서버로부터_실패응답을_받으면_뷰에서_실패메시지를_띄운다() {
        when(mockUserService.requestRemoveAppFromWishList(anyString()))
                .thenReturn(Completable.error(new Throwable()));

        subject.requestRemoveFromWishList("com.test");

        verify(mockView).showToast(anyString());
    }

    @Test
    public void getRemovedPackageNames_호출시__위시리스트에서_제거된_앱들의_packageName을_반환한다() {
        subject.requestRemoveFromWishList("com.test1");
        subject.requestRemoveFromWishList("com.test2");
        subject.requestRemoveFromWishList("com.test3");

        ArrayList<String> removedPackageNames = subject.getRemovedPackageNames();

        assertThat(removedPackageNames.contains("com.test1")).isTrue();
        assertThat(removedPackageNames.contains("com.test2")).isTrue();
        assertThat(removedPackageNames.contains("com.test3")).isTrue();
    }
}
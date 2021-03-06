package com.formakers.fomes.wishList;

import com.formakers.fomes.common.helper.ImageLoader;
import com.formakers.fomes.common.model.AppInfo;
import com.formakers.fomes.common.network.UserService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WishListPresenterTest {

    @Mock private WishListContract.View mockView;
    @Mock private WishListAdapterContract.Model mockAdapterModel;
    @Mock private UserService mockUserService;
    @Mock private ImageLoader mockImageLoader;

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

        subject = new WishListPresenter(mockView, mockUserService, mockImageLoader);
        subject.setAdapterModel(mockAdapterModel);

        List<AppInfo> appInfos = Arrays.asList(new AppInfo("com.test.1"), new AppInfo("com.test.2"), new AppInfo("com.test.3"), new AppInfo("com.test.4"));

        when(mockUserService.requestRemoveAppFromWishList(anyString()))
                .thenReturn(Completable.complete());
        when(mockUserService.requestWishList()).thenReturn(Observable.just(appInfos));

        when(mockAdapterModel.getItem(0)).thenReturn(appInfos.get(0));
        when(mockAdapterModel.getItem(1)).thenReturn(appInfos.get(1));
        when(mockAdapterModel.getItem(2)).thenReturn(appInfos.get(2));
        when(mockAdapterModel.getItem(3)).thenReturn(appInfos.get(3));

        when(mockView.getCompositeSubscription()).thenReturn(new CompositeSubscription());
    }

    @Test
    public void loadingWishList__?????????_???????????????_?????????_????????????() {
        subject.loadingWishList();

        verify(mockUserService).requestWishList();
    }

    @Test
    public void loadingWishList__?????????__????????????_??????????????????_??????_????????????() {
        subject.loadingWishList();

        ArgumentCaptor<List<AppInfo>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockAdapterModel).addAll(captor.capture());
        assertThat(captor.getValue().get(0).getPackageName()).isEqualTo("com.test.1");
        assertThat(captor.getValue().get(1).getPackageName()).isEqualTo("com.test.2");
        assertThat(captor.getValue().get(2).getPackageName()).isEqualTo("com.test.3");
        assertThat(captor.getValue().get(3).getPackageName()).isEqualTo("com.test.4");

        verify(mockView).refresh();
        verify(mockView).showWishList(eq(true));
    }

    @Test
    public void loadingWishList__?????????__???????????????_????????????() {
        when(mockUserService.requestWishList())
                .thenReturn(Observable.error(new Throwable()));

        subject.loadingWishList();

        verify(mockView).showWishList(eq(false));
    }

    @Test
    public void requestRemoveFromWishList_?????????__????????????_??????????????????_??????????????????_?????????_????????????() {
        subject.requestRemoveFromWishList(0);

        verify(mockUserService).requestRemoveAppFromWishList(eq("com.test.1"));
    }

    @Test
    public void requestRemoveFromWishList__?????????__?????????_??????_packageName???_?????????_???_????????????_????????????_????????????_????????????_????????????() {
        subject.requestRemoveFromWishList(0);

        List<String> removedPackageNames = subject.getRemovedPackageNames();

        assertThat(removedPackageNames.contains("com.test.1")).isTrue();
        verify(mockAdapterModel).remove(eq(0));
        verify(mockView).refresh(eq(0));
    }

    @Test
    public void requestRemoveFromWishList_?????????_?????????_??????????????????_?????????() {
        when(mockUserService.requestRemoveAppFromWishList(anyString()))
                .thenReturn(Completable.error(new Throwable()));

        subject.requestRemoveFromWishList(0);

        verify(mockView).showToast(anyInt());
    }

    @Test
    public void getItemPackageName_?????????__??????_????????????_???????????????_??????_???????????????_????????????() {
        when(mockAdapterModel.getItem(0)).thenReturn(new AppInfo("com.package.name"));

        String actualPackageName = subject.getItemPackageName(0);

        verify(mockAdapterModel).getItem(0);
        assertThat(actualPackageName).isEqualTo("com.package.name");
    }

    @Test
    public void getRemovedPackageNames_?????????__??????????????????_?????????_?????????_packageName???_????????????() {
        subject.requestRemoveFromWishList(0);
        subject.requestRemoveFromWishList(1);
        subject.requestRemoveFromWishList(2);

        List<String> removedPackageNames = subject.getRemovedPackageNames();

        assertThat(removedPackageNames.contains("com.test.1")).isTrue();
        assertThat(removedPackageNames.contains("com.test.2")).isTrue();
        assertThat(removedPackageNames.contains("com.test.3")).isTrue();
    }
}
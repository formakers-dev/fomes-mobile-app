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
    public void loadingWishList__호출시_즐겨찾기를_서버에_요청한다() {
        subject.loadingWishList();

        verify(mockUserService).requestWishList();
    }

    @Test
    public void loadingWishList__성공시__데이터를_업데이트하고_뷰에_보여준다() {
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
    public void loadingWishList__실패시__오류화면을_보여준다() {
        when(mockUserService.requestWishList())
                .thenReturn(Observable.error(new Throwable()));

        subject.loadingWishList();

        verify(mockView).showWishList(eq(false));
    }

    @Test
    public void requestRemoveFromWishList_호출시__특정앱을_즐겨찾기에서_제거해줄것을_서버에_요청한다() {
        subject.requestRemoveFromWishList(0);

        verify(mockUserService).requestRemoveAppFromWishList(eq("com.test.1"));
    }

    @Test
    public void requestRemoveFromWishList__성공시__제거할_앱의_packageName을_제거된_앱_리스트에_추가하고_해당앱을_화면에서_제거한다() {
        subject.requestRemoveFromWishList(0);

        List<String> removedPackageNames = subject.getRemovedPackageNames();

        assertThat(removedPackageNames.contains("com.test.1")).isTrue();
        verify(mockAdapterModel).remove(eq(0));
        verify(mockView).refresh(eq(0));
    }

    @Test
    public void requestRemoveFromWishList_실패시_뷰에서_실패메시지를_띄운다() {
        when(mockUserService.requestRemoveAppFromWishList(anyString()))
                .thenReturn(Completable.error(new Throwable()));

        subject.requestRemoveFromWishList(0);

        verify(mockView).showToast(anyInt());
    }

    @Test
    public void getItemPackageName_호출시__해당_인덱스의_위시리스트_앱의_패키지명을_리턴한다() {
        when(mockAdapterModel.getItem(0)).thenReturn(new AppInfo("com.package.name"));

        String actualPackageName = subject.getItemPackageName(0);

        verify(mockAdapterModel).getItem(0);
        assertThat(actualPackageName).isEqualTo("com.package.name");
    }

    @Test
    public void getRemovedPackageNames_호출시__즐겨찾기에서_제거된_앱들의_packageName을_반환한다() {
        subject.requestRemoveFromWishList(0);
        subject.requestRemoveFromWishList(1);
        subject.requestRemoveFromWishList(2);

        List<String> removedPackageNames = subject.getRemovedPackageNames();

        assertThat(removedPackageNames.contains("com.test.1")).isTrue();
        assertThat(removedPackageNames.contains("com.test.2")).isTrue();
        assertThat(removedPackageNames.contains("com.test.3")).isTrue();
    }
}
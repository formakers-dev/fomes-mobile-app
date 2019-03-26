package com.formakers.fomes.main.presenter;


import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.helper.AndroidNativeHelper;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.main.contract.RecommendListAdapterContract;
import com.formakers.fomes.model.AppInfo;
import com.formakers.fomes.model.NativeAppInfo;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecommendPresenterTest {

    @Mock private RecommendContract.View mockView;
    @Mock private RecommendListAdapterContract.Model mockAdapterModel;
    @Mock private RecommendService mockRecommendService;
    @Mock private UserService mockUserService;
    @Mock private AndroidNativeHelper mockAndroidNativeHelper;

    private RecommendPresenter subject;

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

        List<RecommendApp> list = new ArrayList<>();
        list.add(new RecommendApp().setAppInfo(new AppInfo("com.test1")));
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(list));
        when(mockAndroidNativeHelper.getInstalledLaunchableApps()).thenReturn(Observable.from(new ArrayList<>()));
        when(mockView.isEndOfRecommendList()).thenReturn(false);

        subject = new RecommendPresenter(mockView, mockRecommendService, mockUserService, mockAndroidNativeHelper);
        subject.setAdapterModel(mockAdapterModel);
    }

    @Test
    public void emitShowDetailEvent__디테일화면을_보여주라는_이벤트_발생시__뷰에_콜백을_호출한다() {
        RecommendApp app = new RecommendApp().setAppInfo(new AppInfo("packageName").setAppName("appName"));
        subject.emitShowDetailEvent(app);

        ArgumentCaptor<RecommendApp> captor = ArgumentCaptor.forClass(RecommendApp.class);
        verify(mockView).onShowDetailEvent(captor.capture());
        assertThat(captor.getValue().getAppInfo().getPackageName()).isEqualTo("packageName");
        assertThat(captor.getValue().getAppInfo().getAppName()).isEqualTo("appName");
    }

    @Test
    public void loadRecommendApps__호출시__설치한_앱_리스트를_요청한다() {
        subject.loadRecommendApps("GAME");

        verify(mockAndroidNativeHelper).getInstalledLaunchableApps();
    }

    @Test
    public void loadRecommendApps__호출시__로딩화면_표시를_요청한다() {
        subject.loadRecommendApps("GAME");

        verify(mockView).showLoading();
    }

    @Test
    public void loadRecommendApps__호출시__서버결과_수신후_로딩화면을_숨긴다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(Lists.newArrayList(new RecommendApp().setAppInfo(new AppInfo("com.test1")).setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC))));

        subject.loadRecommendApps("GAME");

        verify(mockView).hideLoading();
    }

    @Test
    public void loadRecommendApps__추천_앱_리스트_로드시__현재페이지를_1로_초기화하고_해당_서버에_첫번째_페이지를_요청한다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(Lists.newArrayList(new RecommendApp().setAppInfo(new AppInfo("com.test1")).setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC))));

        subject.loadRecommendApps("GAME");

        assertEquals(subject.getCurrentPage(), 1);
        verify(mockRecommendService).requestRecommendApps("GAME", 1);
    }

    @Test
    public void loadRecommendApps_성공시__중복제거_및_설치됨처리_후_추천앱_목록을_화면에_표시한다() {
        when(mockAdapterModel.contains("com.test1")).thenReturn(true);

        List<RecommendApp> items = new ArrayList<>();
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test1"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test2"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test3"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_FAVORITE_APP));
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(items));

        List<NativeAppInfo> installedApps = new ArrayList<>();
        installedApps.add(new NativeAppInfo("com.test1", "테스트1"));
        installedApps.add(new NativeAppInfo("com.test3", "테스트3"));
        when(mockAndroidNativeHelper.getInstalledLaunchableApps()).thenReturn(Observable.from(installedApps));

        subject.loadRecommendApps("GAME");

        ArgumentCaptor<RecommendApp> captor = ArgumentCaptor.forClass(RecommendApp.class);
        verify(mockAdapterModel, atLeast(1)).add(captor.capture());
        assertThat(captor.getAllValues().get(0).getAppInfo().getPackageName()).isEqualTo("com.test2");
        assertThat(captor.getAllValues().get(0).getAppInfo().isInstalled()).isEqualTo(false);
        assertThat(captor.getAllValues().get(1).getAppInfo().getPackageName()).isEqualTo("com.test3");
        assertThat(captor.getAllValues().get(1).getAppInfo().isInstalled()).isEqualTo(true);
        verify(mockView, atLeast(1)).refreshRecommendList();
    }

    @Test
    public void loadRecommendApps_성공시__추천앱_로딩이_더_필요하다면__재호출_한다() {
        when(mockView.isEndOfRecommendList()).thenReturn(true, false);
        when(mockAdapterModel.contains("com.test1")).thenReturn(true);

        subject.loadRecommendApps("GAME");

        verify(mockRecommendService, times(2)).requestRecommendApps(eq("GAME"), anyInt());
    }

    @Test
    public void loadRecommendApps__재호출시__서버에_다음_페이지의_추천앱_목록을_요청한다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(Lists.newArrayList(new RecommendApp().setAppInfo(new AppInfo("com.test1")).setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC))));

        subject.loadRecommendApps("GAME");
        subject.loadRecommendApps("GAME");

        verify(mockRecommendService).requestRecommendApps("GAME", 2);
    }

    @Test
    public void loadRecommendApps__호출결과_서버로_부터_정상응답을_받으면_현재페이지_값을_1만큼_증가시킨다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(Lists.newArrayList(new RecommendApp().setAppInfo(new AppInfo("com.test1")).setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC))));

        subject.setCurrentPage(2);
        subject.loadRecommendApps("GAME");

        assertThat(subject.getCurrentPage()).isEqualTo(3);
    }

    @Test
    public void loadRecommendApps__호출결과_추천앱_목록이_없는_경우__에러페이지를_표시한다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(new ArrayList<>()));
        when(mockAdapterModel.getItemCount()).thenReturn(0);

        subject.loadRecommendApps("GAME");

        verify(mockView).showErrorPage();
    }

    @Test
    public void loadRecommendApps__호출시_예외가_발생한_경우__에러페이지를_표시한다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.error(new Exception()));

        subject.loadRecommendApps("GAME");

        verify(mockView).showErrorPage();
    }

    @Test
    public void loadRecommendApps__이미_추천앱리스트에_데이터가_있는_경우__이전_데이터와의_중복을_제거한다() {
        List<RecommendApp> originalItems = new ArrayList<>();

        originalItems.add(new RecommendApp().setAppInfo(new AppInfo("com.test2"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));

        List<RecommendApp> newItems = new ArrayList<>();

        newItems.add(new RecommendApp().setAppInfo(new AppInfo("com.test1"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));
        newItems.add(new RecommendApp().setAppInfo(new AppInfo("com.test2"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));
        newItems.add(new RecommendApp().setAppInfo(new AppInfo("com.test1"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_FAVORITE_DEVELOPER));
        newItems.add(new RecommendApp().setAppInfo(new AppInfo("com.test3"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_FAVORITE_APP));

        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(newItems));
        when(mockAdapterModel.getAllItems()).thenReturn(originalItems);

        subject.loadRecommendApps("GAME");

        verify(mockView, atLeast(1)).refreshRecommendList();
    }

    @Test
    public void loadRecommendApps__기존_추천앱리스트_데이터가_있고_호출결과_추천앱_목록이_없는_경우__아무_동작도_하지않는다() {
        when(mockAdapterModel.getItemCount()).thenReturn(1);
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(new ArrayList<>()));

        subject.loadRecommendApps("GAME");

        verify(mockView, never()).showErrorPage();
    }

    @Test
    public void loadRecommendApps__기존_추천앱리스트_데이터가_있고_호출시_예외가_발생한_경우__아무_동작도_하지않는다() {
        when(mockAdapterModel.getItemCount()).thenReturn(1);
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.error(new Exception()));

        subject.loadRecommendApps("GAME");

        verify(mockView, never()).showErrorPage();
    }

    @Test
    public void requestSaveToWishList_호출시__앱을_즐겨찾기에_추가하도록_서버에_요청한다() {
        subject.requestSaveToWishList("com.test");

        verify(mockUserService).requestSaveAppToWishList("com.test");
    }

    @Test
    public void requestRemoveFromWishList_호출시__앱을_즐겨찾기에서_삭제하도록_서버에_요청한다() {
        subject.requestRemoveFromWishList("com.test");

        verify(mockUserService).requestRemoveAppFromWishList("com.test");
    }

    @Test
    public void updateWishedStatus_호출시__해당_앱의_즐겨찾기_포함여부를_업데이트한다() {
        subject.updateWishedStatus("com.test.pkg", true);

        verify(mockAdapterModel).updateWishedStatus(eq("com.test.pkg"), eq(true));
    }
}
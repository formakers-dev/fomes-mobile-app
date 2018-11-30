package com.formakers.fomes.main.presenter;


import com.bumptech.glide.RequestManager;
import com.formakers.fomes.common.network.RecommendService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.RecommendApp;
import com.formakers.fomes.main.contract.RecommendContract;
import com.formakers.fomes.model.AppInfo;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecommendPresenterTest {

    @Mock private RecommendContract.View mockView;
    @Mock private RecommendService mockRecommendService;
    @Mock private UserService mockUserService;
    @Mock private RequestManager mockRequestManager;

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

        subject = new RecommendPresenter(mockView, mockRecommendService, mockUserService, mockRequestManager);
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
    public void loadRecommendApps__호출시__로딩화면_표시를_요청한다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(new ArrayList<>()));

        subject.loadRecommendApps("GAME");

        verify(mockView).showLoading();
    }

    @Test
    public void loadRecommendApps__추천_앱_리스트_로드시__현재페이지를_1로_초기화하고_해당_서버에_첫번째_페이지를_요청한다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(new ArrayList<>()));

        subject.loadRecommendApps("GAME");

        assertEquals(subject.getCurrentPage(), 1);
        verify(mockRecommendService).requestRecommendApps("GAME", 1);
    }

    @Test
    public void loadRecommendApps_호출결과목록이_있으면__추천앱_목록을_화면에_표시한다() {
        List<RecommendApp> items = new ArrayList<>();

        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test1"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));

        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(items));

        subject.loadRecommendApps("GAME");

        ArgumentCaptor<List<RecommendApp>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).bindRecommendList(captor.capture());
        assertThat(captor.getValue().get(0).getAppInfo().getPackageName()).isEqualTo("com.test1");
        assertThat(captor.getValue().get(0).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC);
    }

    @Test
    public void loadRecommendApps_호출결과_중복목록이_있으면__중복된_앱_제거후_추천앱_목록을_화면에_표시한다() {
        List<RecommendApp> items = new ArrayList<>();

        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test1"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test2"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test1"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_FAVORITE_DEVELOPER));
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test3"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_FAVORITE_APP));

        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(items));

        subject.loadRecommendApps("GAME");

        ArgumentCaptor<List<RecommendApp>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).bindRecommendList(captor.capture());

        List<RecommendApp> recommendApps = captor.getValue();
        assertThat(recommendApps.get(0).getAppInfo().getPackageName()).isEqualTo("com.test1");
        assertThat(recommendApps.get(0).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC);
        assertThat(recommendApps.get(1).getAppInfo().getPackageName()).isEqualTo("com.test2");
        assertThat(recommendApps.get(1).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC);
        assertThat(recommendApps.get(2).getAppInfo().getPackageName()).isEqualTo("com.test3");
        assertThat(recommendApps.get(2).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_FAVORITE_APP);
    }

    @Test
    public void loadRecommendAppsMore__호출시__서버에_다음_페이지의_추천앱_목록을_요청한다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(new ArrayList<>()));

        subject.setCurrentPage(1);
        subject.loadRecommendAppsMore("GAME");

        verify(mockRecommendService).requestRecommendApps("GAME", 2);
    }

    @Test
    public void loadRecommendAppsMore__호출시__loadRecommendApps가_먼저_호출되지_않은_경우_서버에_추천앱_목록을_요청하지_않는다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(new ArrayList<>()));

        subject.loadRecommendAppsMore("GAME");

        verify(mockRecommendService, never()).requestRecommendApps(anyString(), anyInt());
    }

    @Test
    public void loadRecommendAppsMore_호출결과목록이_있으면__추천앱_목록을_화면에_표시한다() {
        List<RecommendApp> items = new ArrayList<>();

        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test1"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));

        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(items));

        subject.setCurrentPage(1);
        subject.loadRecommendAppsMore("GAME");

        ArgumentCaptor<List<RecommendApp>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).bindRecommendListMore(captor.capture());
        assertThat(captor.getValue().get(0).getAppInfo().getPackageName()).isEqualTo("com.test1");
        assertThat(captor.getValue().get(0).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC);
    }

    @Test
    public void loadRecommendAppsMore_호출결과_서버로_부터_정상응답을_받으면_현재페이지_값을_1만큼_증가시킨다() {
        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(new ArrayList<>()));

        subject.setCurrentPage(2);
        subject.loadRecommendAppsMore("GAME");

        assertThat(subject.getCurrentPage()).isEqualTo(3);
    }

    @Test
    public void loadRecommendAppsMore_호출결과_중복목록이_있으면__중복된_앱_제거후_추천앱_목록을_화면에_표시한다() {
        List<RecommendApp> items = new ArrayList<>();

        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test1"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test2"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC));
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test1"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_FAVORITE_DEVELOPER));
        items.add(new RecommendApp().setAppInfo(new AppInfo("com.test3"))
                .setRecommendType(RecommendApp.RECOMMEND_TYPE_FAVORITE_APP));

        when(mockRecommendService.requestRecommendApps(anyString(), anyInt())).thenReturn(Observable.just(items));

        subject.setCurrentPage(1);
        subject.loadRecommendAppsMore("GAME");

        ArgumentCaptor<List<RecommendApp>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).bindRecommendListMore(captor.capture());

        List<RecommendApp> recommendApps = captor.getValue();
        assertThat(recommendApps.get(0).getAppInfo().getPackageName()).isEqualTo("com.test1");
        assertThat(recommendApps.get(0).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC);
        assertThat(recommendApps.get(1).getAppInfo().getPackageName()).isEqualTo("com.test2");
        assertThat(recommendApps.get(1).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_SIMILAR_DEMOGRAPHIC);
        assertThat(recommendApps.get(2).getAppInfo().getPackageName()).isEqualTo("com.test3");
        assertThat(recommendApps.get(2).getRecommendType()).isEqualTo(RecommendApp.RECOMMEND_TYPE_FAVORITE_APP);
    }

    @Test
    public void emitSaveToWishList_호출시__앱을_위시리스트에_추가하도록_서버에_요청한다() {
        subject.emitSaveToWishList("com.test");

        verify(mockUserService).requestSaveAppToWishList("com.test");
    }

    @Test
    public void emitRemoveFromWishList_호출시__앱을_위시리스트에서_삭제하도록_서버에_요청한다() {
        subject.emitRemoveFromWishList("com.test");

        verify(mockUserService).requestRemoveAppFromWishList("com.test");
    }

    @Test
    public void emitRefreshWishedByMe_호출시__특정_앱의_위시리스트_아이콘을_변경한다() {
        subject.emitRefreshWishedByMe("com.test", true);

        verify(mockView).refreshWishedByMe("com.test", true);
    }
}
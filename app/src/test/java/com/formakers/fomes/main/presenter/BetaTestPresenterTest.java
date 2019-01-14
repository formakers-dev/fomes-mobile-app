package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.RequestService;
import com.formakers.fomes.common.network.vo.BetaTestRequest;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.main.adapter.BetaTestListAdapter;
import com.formakers.fomes.main.contract.BetaTestContract;
import com.formakers.fomes.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BetaTestPresenterTest {

    @Mock private BetaTestContract.View mockView;
    @Mock private BetaTestListAdapter mockAdapterModel;
    @Mock private RequestService mockRequestservice;
    @Mock private UserDAO mockUserDAO;

    private User dummyUser;
    private BetaTestPresenter subject;

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

        dummyUser = new User().setEmail("user@gmail.com");
        when(mockUserDAO.getUserInfo()).thenReturn(Single.just(dummyUser));

        subject = new BetaTestPresenter(mockView, mockRequestservice, mockUserDAO);
        subject.setAdapterModel(mockAdapterModel);
    }

    @Test
    public void load__호출시__테스트존_리스트를_요청하고_유저정보를_가져온다() {
        List<BetaTestRequest> betaTestRequests = new ArrayList<>();
        when(mockRequestservice.getFeedbackRequest()).thenReturn(Single.just(betaTestRequests));

        subject.load();

        verify(mockUserDAO).getUserInfo();
        verify(mockRequestservice).getFeedbackRequest();
        verify(mockAdapterModel).addAll(eq(betaTestRequests));
        verify(mockView).refreshBetaTestList();
    }

    @Test
    public void getURL__호출시__해당_테스트의_URL정보와_유저의_이메일을_붙인다() {
        BetaTestRequest request = new BetaTestRequest()
                .setAction("google.com?link=")
                .setActionType("link");

        when(mockAdapterModel.getItem(0)).thenReturn(request);

        subject.load();
        String url = subject.getSurveyURL(0);

        assertThat(url).isEqualTo("google.com?link=" + "user@gmail.com");
    }
}
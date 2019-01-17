package com.formakers.fomes.main.presenter;

import com.formakers.fomes.common.network.BetaTestService;
import com.formakers.fomes.common.network.vo.BetaTest;
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
    @Mock private BetaTestService mockRequestservice;
    @Mock private UserDAO mockUserDAO;

    private User dummyUser;
    private List<BetaTest> betaTests = new ArrayList<>();
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

        betaTests.add(new BetaTest().setTitle("베타테스트1"));
        betaTests.add(new BetaTest().setTitle("베타테스트2"));
        when(mockRequestservice.getBetaTestList()).thenReturn(Single.just(betaTests));

        when(mockAdapterModel.getItem(0)).thenReturn(betaTests.get(0));
        when(mockAdapterModel.getItem(1)).thenReturn(betaTests.get(1));

        subject = new BetaTestPresenter(mockView, mockRequestservice, mockUserDAO);
        subject.setAdapterModel(mockAdapterModel);
    }

    @Test
    public void load__호출시__테스트존_리스트를_요청하고_유저정보를_가져온다() {
        subject.load();

        verify(mockUserDAO).getUserInfo();
        verify(mockRequestservice).getBetaTestList();
        verify(mockAdapterModel).addAll(eq(betaTests));
        verify(mockView).refreshBetaTestList();
    }

    @Test
    public void getBetaTestItem__호출시__해당_위치의_베타테스트를_리턴한다() {
        subject.load();
        BetaTest betaTest = subject.getBetaTestItem(0);

        assertThat(betaTest.getTitle()).isEqualTo("베타테스트1");
    }

    @Test
    public void getUserEmail__호출시__유저의_이메일을_리턴한다() {
        subject.load();
        String userEmail = subject.getUserEmail();

        assertThat(userEmail).isEqualTo("user@gmail.com");
    }
}
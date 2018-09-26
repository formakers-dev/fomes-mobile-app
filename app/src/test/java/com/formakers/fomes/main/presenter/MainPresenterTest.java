package com.formakers.fomes.main.presenter;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.main.contract.MainContract;
import com.formakers.fomes.repository.dao.UserDAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainPresenterTest {

    @Inject UserDAO mockUserDAO;

    @Mock
    MainContract.View mockView;

    MainPresenter subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);
        subject = new MainPresenter(mockView, mockUserDAO);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void requestUserInfo_호출시__DB에_유저정보를_요청한다() {
        subject.requestUserInfo();

        verify(mockUserDAO).getUserInfo();
    }
}
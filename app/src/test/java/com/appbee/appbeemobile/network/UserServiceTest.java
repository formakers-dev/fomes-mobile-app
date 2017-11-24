package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class UserServiceTest {
    private UserService subject;

    @Mock
    private UserAPI mockUserAPI;

    @Mock
    private LocalStorageHelper mockLocalStorageHelper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new UserService(mockUserAPI, mockLocalStorageHelper);
        when(mockLocalStorageHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");

        RxJavaHooks.reset();
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    }

    @After
    public void tearDown() {
        RxJavaHooks.reset();
    }

    @Test
    public void signIn호출시_로그인_요청을_서버에_전송한다() throws Exception {
        when(mockUserAPI.signInUser("GOOGLE_TOKEN")).thenReturn(mock(Observable.class));

        subject.signIn("GOOGLE_TOKEN");

        verify(mockUserAPI).signInUser(eq("GOOGLE_TOKEN"));
    }

    @Test
    public void sendUser호출시_유저정보를_서버로_전송한다() throws Exception {
        when(mockUserAPI.updateUser(eq("TEST_ACCESS_TOKEN"), any(User.class))).thenReturn(mock(Observable.class));

        User mockUser = mock(User.class);
        subject.sendUser(mockUser);

        verify(mockUserAPI).updateUser("TEST_ACCESS_TOKEN", mockUser);
    }

    @Test
    public void verifyRegistrationCode호출시_코드확인_요청을_한다() throws Exception {
        when(mockUserAPI.verifyInvitationCode("REGISTRATION_CODE")).thenReturn(mock(Observable.class));

        subject.verifyInvitationCode("REGISTRATION_CODE");

        verify(mockUserAPI).verifyInvitationCode(eq("REGISTRATION_CODE"));
    }
}
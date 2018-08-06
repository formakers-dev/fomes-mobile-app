package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class UserServiceTest extends AbstractServiceTest {
    private UserService subject;

    @Mock
    private UserAPI mockUserAPI;

    @Mock
    private LocalStorageHelper mockLocalStorageHelper;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        subject = new UserService(mockUserAPI, mockLocalStorageHelper, getMockAppBeeAPIHelper());
        when(mockLocalStorageHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");
    }

    @Test
    public void signIn호출시_로그인_요청을_서버에_전송한다() throws Exception {
        when(mockUserAPI.signIn(anyString(), any(User.class))).thenReturn(mock(Observable.class));

        User mockUser = mock(User.class);
        subject.signIn("GOOGLE_TOKEN", mockUser);

        verify(mockUserAPI).signIn("GOOGLE_TOKEN", mockUser);
    }

    @Test
    public void updateRegistrationToken호출시_푸시토큰정보를_서버에_전송한다() throws Exception {
        when(mockUserAPI.update(anyString(), any(User.class))).thenReturn(mock(Observable.class));

        subject.updateRegistrationToken("REFRESHED_PUSH_TOKEN").subscribe(new TestSubscriber<>());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserAPI).update(eq("TEST_ACCESS_TOKEN"), userCaptor.capture());

        User userArgument = userCaptor.getValue();
        assertThat(userArgument.getRegistrationToken()).isEqualTo("REFRESHED_PUSH_TOKEN");
        assertThat(userArgument.getName()).isNull();
        assertThat(userArgument.getUserId()).isNull();
        assertThat(userArgument.getEmail()).isNull();
        assertThat(userArgument.getGender()).isNull();
        assertThat(userArgument.getBirthday()).isNull();
    }

    @Test
    public void updateRegistrationToken호출시_토큰_만료_여부를_확인한다() throws Exception {
        verifyToCheckExpiredToken(subject.updateRegistrationToken("REFRESHED_PUSH_TOKEN").toObservable());
    }

    @Test
    public void verifyRegistrationCode호출시_코드확인_요청을_한다() throws Exception {
        when(mockUserAPI.verifyInvitationCode(anyString())).thenReturn(mock(Observable.class));

        subject.verifyInvitationCode("REGISTRATION_CODE");

        verify(mockUserAPI).verifyInvitationCode(eq("REGISTRATION_CODE"));
    }

    @Test
    public void verifyToken호출시_토큰확인_요청을_한다() throws Exception {
        when(mockUserAPI.verifyToken(anyString())).thenReturn(mock(Observable.class));

        subject.verifyToken();

        verify(mockUserAPI).verifyToken(eq("TEST_ACCESS_TOKEN"));
    }
}
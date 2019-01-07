package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.UserAPI;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;

import rx.Observable;
import rx.Single;
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
    private SharedPreferencesHelper mockSharedPreferencesHelper;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        subject = new UserService(mockUserAPI, mockSharedPreferencesHelper, getMockAPIHelper());
        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");
    }

    @Test
    public void signUp호출시_가입_요청을_서버에_전송한다() {
        when(mockUserAPI.signUp(anyString(), any(User.class))).thenReturn(mock(Single.class));

        User mockUser = mock(User.class);
        subject.signUp("GOOGLE_TOKEN", mockUser);

        verify(mockUserAPI).signUp("GOOGLE_TOKEN", mockUser);
    }

    @Test
    public void signIn호출시_로그인_요청을_서버에_전송한다() {
        when(mockUserAPI.signIn(anyString(), any(User.class))).thenReturn(mock(Observable.class));

        User mockUser = mock(User.class);
        subject.signIn("GOOGLE_TOKEN", mockUser);

        verify(mockUserAPI).signIn("GOOGLE_TOKEN", mockUser);
    }

    @Test
    public void updateRegistrationToken호출시_푸시토큰정보를_서버에_전송한다() {
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
    }

    @Test
    public void updateRegistrationToken호출시_토큰_만료_여부를_확인한다() {
        verifyToCheckExpiredToken(subject.updateRegistrationToken("REFRESHED_PUSH_TOKEN").toObservable());
    }

    @Test
    public void requestSaveAppToWishList_호출시__앱을_즐겨찾기에_추가하는_요청을_한다() {
        when(mockUserAPI.postWishList(anyString(), any(HashMap.class))).thenReturn(mock(Observable.class));

        subject.requestSaveAppToWishList("com.test.app1").subscribe(new TestSubscriber<>());

        ArgumentCaptor<HashMap> wishListMapCaptor = ArgumentCaptor.forClass(HashMap.class);

        verify(mockUserAPI).postWishList(anyString(), wishListMapCaptor.capture());

        HashMap<String, Object> wishListMap = wishListMapCaptor.getValue();
        assertThat(wishListMap.get("packageName")).isEqualTo("com.test.app1");
    }

    @Test
    public void requestRemoveAppFromWishList_호출시__앱을_즐겨찾기에서_삭제하는_요청을_한다() {
        when(mockUserAPI.deleteWishList(anyString(), anyString())).thenReturn(mock(Observable.class));

        subject.requestRemoveAppFromWishList("com.test.app").subscribe(new TestSubscriber<>());

        verify(mockUserAPI).deleteWishList(anyString(), eq("com.test.app"));
    }

    @Test
    public void verifyRegistrationCode호출시_코드확인_요청을_한다() {
        when(mockUserAPI.verifyInvitationCode(anyString())).thenReturn(mock(Observable.class));

        subject.verifyInvitationCode("REGISTRATION_CODE");

        verify(mockUserAPI).verifyInvitationCode(eq("REGISTRATION_CODE"));
    }

    @Test
    public void verifyToken호출시_토큰확인_요청을_한다() {
        when(mockUserAPI.verifyToken(anyString())).thenReturn(mock(Observable.class));

        subject.verifyToken();

        verify(mockUserAPI).verifyToken(eq("TEST_ACCESS_TOKEN"));
    }

    @Test
    public void verifyNickName_호출시__닉네임_체크_요청을_한다() {
        when(mockUserAPI.verifyNickName(anyString(), anyString())).thenReturn(mock(Observable.class));

        subject.verifyNickName("닉네임").subscribe(new TestSubscriber<>());;

        verify(mockUserAPI).verifyNickName(eq("TEST_ACCESS_TOKEN"), eq("닉네임"));
    }
}
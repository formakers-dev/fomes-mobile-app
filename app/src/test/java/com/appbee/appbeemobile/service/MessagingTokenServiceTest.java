package com.appbee.appbeemobile.service;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.TestAppBeeApplication;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.MessagingHelper;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import rx.Completable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MessagingTokenServiceTest {
    private MessagingTokenService subject;

    @Inject
    MessagingHelper messagingHelper;

    @Inject
    LocalStorageHelper localStorageHelper;

    @Inject
    UserService mockUserService;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(messagingHelper.getMessagingToken()).thenReturn("NEW_TOKEN");
        when(localStorageHelper.getRegistrationToken()).thenReturn("OLD_TOKEN");
        when(localStorageHelper.getEmail()).thenReturn("email");
        when(mockUserService.sendUser(any(User.class))).thenReturn(Completable.complete());
        subject = Robolectric.setupService(MessagingTokenService.class);
    }

    @Test
    public void 가입한_유저가_아닌_경우_갱신된_토큰정보를_로컬에_저장한다() throws Exception {
        when(localStorageHelper.getEmail()).thenReturn("");

        subject.onTokenRefresh();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(localStorageHelper).setRegistrationToken(captor.capture());
        assertThat(captor.getValue()).isEqualTo("NEW_TOKEN");
    }

    @Test
    public void 가입한_유저가_아닌_경우_사용자_업데이트API를_호출하지_않는다() throws Exception {
        when(localStorageHelper.getEmail()).thenReturn("");
        subject.onTokenRefresh();
        verify(mockUserService, never()).sendUser(any(User.class));
    }

    @Test
    public void 토큰이_갱신되었을_경우_사용자_업데이트API를_호출한다() throws Exception {
        subject.onTokenRefresh();
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).sendUser(captor.capture());
        User user = captor.getValue();
        assertThat(user.getRegistrationToken()).isEqualTo("NEW_TOKEN");
    }
}
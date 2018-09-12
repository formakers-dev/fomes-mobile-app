package com.formakers.fomes.service;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.helper.MessagingHelper;
import com.formakers.fomes.model.User;
import com.formakers.fomes.network.UserService;

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
import static org.mockito.ArgumentMatchers.anyString;
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
    SharedPreferencesHelper SharedPreferencesHelper;

    @Inject
    UserService mockUserService;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(messagingHelper.getMessagingToken()).thenReturn("NEW_TOKEN");
        when(SharedPreferencesHelper.getRegistrationToken()).thenReturn("OLD_TOKEN");
        when(SharedPreferencesHelper.isLoggedIn()).thenReturn(true);
        when(mockUserService.updateRegistrationToken(anyString())).thenReturn(Completable.complete());
        subject = Robolectric.setupService(MessagingTokenService.class);
    }

    @Test
    public void 가입한_유저가_아닌_경우_갱신된_토큰정보를_로컬에_저장한다() throws Exception {
        when(SharedPreferencesHelper.isLoggedIn()).thenReturn(false);

        subject.onTokenRefresh();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(SharedPreferencesHelper).setRegistrationToken(captor.capture());
        assertThat(captor.getValue()).isEqualTo("NEW_TOKEN");
    }

    @Test
    public void 가입한_유저가_아닌_경우_사용자푸시토큰_업데이트API를_호출하지_않는다() throws Exception {
        when(SharedPreferencesHelper.isLoggedIn()).thenReturn(false);
        subject.onTokenRefresh();
        verify(mockUserService, never()).updateRegistrationToken(anyString());
    }

    @Test
    public void 토큰이_갱신되었을_경우_사용자푸시토큰_업데이트API를_호출한다() throws Exception {
        subject.onTokenRefresh();
        verify(mockUserService).updateRegistrationToken("NEW_TOKEN");
    }
}
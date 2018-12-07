package com.formakers.fomes.common.noti;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.helper.SharedPreferencesHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import rx.Completable;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MessagingServiceTest {
    private MessagingService subject;

    @Inject SharedPreferencesHelper mockSharedPreferenceHelper;
    @Inject UserService mockUserService;

    @Before
    public void setUp() throws Exception {
        ((TestFomesApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockSharedPreferenceHelper.hasAccessToken()).thenReturn(true);
        when(mockSharedPreferenceHelper.getRegistrationToken()).thenReturn("OLD_TOKEN");
        when(mockUserService.updateRegistrationToken(anyString())).thenReturn(Completable.complete());

        subject = Robolectric.setupService(MessagingService.class);
    }

    @Test
    public void 토큰이_갱신되었을_경우__로그인한_유저인_경우__사용자푸시토큰_업데이트API를_호출한다() {
        when(mockSharedPreferenceHelper.getRegistrationToken()).thenReturn("NEW_TOKEN");

        subject.onNewToken("NEW_TOKEN");

        verify(mockSharedPreferenceHelper).setRegistrationToken("NEW_TOKEN");
        verify(mockUserService).updateRegistrationToken("NEW_TOKEN");
    }

    @Test
    public void 토큰이_갱신되었을_경우__로그인한_유저가_아닌_경우__갱신된_토큰정보를_로컬에만_저장한다() {
        when(mockSharedPreferenceHelper.hasAccessToken()).thenReturn(false);
        when(mockSharedPreferenceHelper.getRegistrationToken()).thenReturn("NEW_TOKEN");

        subject.onNewToken("NEW_TOKEN");

        verify(mockSharedPreferenceHelper).setRegistrationToken("NEW_TOKEN");
        verify(mockUserService, never()).updateRegistrationToken(anyString());
    }
}
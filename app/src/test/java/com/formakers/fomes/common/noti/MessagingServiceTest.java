package com.formakers.fomes.common.noti;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import rx.Completable;
import rx.Single;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MessagingServiceTest {
    private MessagingService subject;

    @Inject SharedPreferencesHelper mockSharedPreferenceHelper;
    @Inject UserService mockUserService;
    @Inject UserDAO mockUserDAO;

    User mockUser = mock(User.class);

    @Before
    public void setUp() throws Exception {
        ((TestFomesApplication) RuntimeEnvironment.application).getComponent().inject(this);

        when(mockSharedPreferenceHelper.hasAccessToken()).thenReturn(true);
        when(mockSharedPreferenceHelper.getRegistrationToken()).thenReturn("OLD_TOKEN");
        when(mockUserService.updateUser(any(User.class))).thenReturn(Completable.complete());
        when(mockUserDAO.getUserInfo()).thenReturn(Single.just(mockUser));

        subject = Robolectric.setupService(MessagingService.class);
    }

    @Test
    public void 토큰이_갱신되었을_경우__로그인한_유저인_경우__사용자푸시토큰_업데이트API를_호출한다() {
        when(mockSharedPreferenceHelper.getRegistrationToken()).thenReturn("NEW_TOKEN");

        subject.onNewToken("NEW_TOKEN");

        verify(mockSharedPreferenceHelper).setRegistrationToken("NEW_TOKEN");
        verify(mockUserService).updateUser(eq(mockUser));
    }

    @Test
    public void 토큰이_갱신되었을_경우__로그인한_유저가_아닌_경우__갱신된_토큰정보를_로컬에만_저장한다() {
        when(mockSharedPreferenceHelper.hasAccessToken()).thenReturn(false);
        when(mockSharedPreferenceHelper.getRegistrationToken()).thenReturn("NEW_TOKEN");

        subject.onNewToken("NEW_TOKEN");

        verify(mockSharedPreferenceHelper).setRegistrationToken("NEW_TOKEN");
        verify(mockUserService, never()).updateUser(any());
    }
}
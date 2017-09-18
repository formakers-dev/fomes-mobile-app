package com.appbee.appbeemobile.receiver;

import android.content.Intent;

import com.appbee.appbeemobile.BuildConfig;
import com.appbee.appbeemobile.helper.LocalStorageHelper;
import com.appbee.appbeemobile.helper.TimeHelper;
import com.appbee.appbeemobile.model.User;
import com.appbee.appbeemobile.network.UserService;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class PowerConnectedReceiverTest {

    private PowerConnectedReceiver subject;

    @Mock
    UserService mockUserService;

    @Mock
    TimeHelper mockTimeHelper;

    @Mock
    LocalStorageHelper mockLocalStorageHelper;

    private static final String UUID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new PowerConnectedReceiver(mockUserService, mockTimeHelper, mockLocalStorageHelper);
    }

    @Ignore
    @Test
    public void onReceive에서_PowerConnect되었을때_userService의_sendUser를_호출한다() throws Exception {
        //TODO : API 변경 후 아래 로직 반영 필요 & Ignore제거
        when(mockTimeHelper.getFormattedCurrentTime(TimeHelper.DATE_FORMAT)).thenReturn("2017-08-30");

        subject.onReceive(RuntimeEnvironment.application.getApplicationContext(), new Intent(Intent.ACTION_POWER_CONNECTED));

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserService).sendUser(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue().getUserId().matches(UUID_REGEX)).isTrue();
        assertThat(userArgumentCaptor.getValue().getFirstUsedDate()).isNull();
        assertThat(userArgumentCaptor.getValue().getLastUsedDate()).isEqualTo("2017-08-30");
    }
}
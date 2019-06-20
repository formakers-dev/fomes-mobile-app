package com.formakers.fomes.common.noti;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.TestFomesApplication;
import com.formakers.fomes.common.FomesConstants;
import com.formakers.fomes.common.job.JobManager;
import com.formakers.fomes.common.network.EventLogService;
import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.repository.dao.UserDAO;
import com.formakers.fomes.helper.SharedPreferencesHelper;
import com.formakers.fomes.main.view.MainActivity;
import com.google.firebase.messaging.RemoteMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Completable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class MessagingServiceTest {
    private MessagingService subject;

    @Inject SharedPreferencesHelper mockSharedPreferenceHelper;
    @Inject UserService mockUserService;
    @Inject UserDAO mockUserDAO;
    @Inject ChannelManager mockChannelManager;
    @Inject EventLogService mockEventLogService;
    @Inject JobManager mockJobManager;

    @Before
    public void setUp() throws Exception {
        ((TestFomesApplication) ApplicationProvider.getApplicationContext()).getComponent().inject(this);

        when(mockSharedPreferenceHelper.hasAccessToken()).thenReturn(true);
        when(mockSharedPreferenceHelper.getUserRegistrationToken()).thenReturn("OLD_TOKEN");
        when(mockUserService.updateRegistrationToken(anyString())).thenReturn(Completable.complete());
        when(mockEventLogService.sendEventLog(any())).thenReturn(Completable.complete());

        subject = Robolectric.setupService(MessagingService.class);
    }

    @Test
    public void 노티메세지가_전송되었을_경우__로그인한_상태면__알림을_띄우고_이벤토로그를_전송한다() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("아무거나키", "아무거나 값");
        RemoteMessage remoteMessage = new RemoteMessage.Builder("noti")
                .setData(dataMap)
                .build();

        subject.onMessageReceived(remoteMessage);

        verify(mockChannelManager).sendNotification(eq(dataMap), eq(MainActivity.class));

        ArgumentCaptor<EventLog> eventLogArgumentCaptor = ArgumentCaptor.forClass(EventLog.class);
        verify(mockEventLogService).sendEventLog(eventLogArgumentCaptor.capture());
        assertThat(eventLogArgumentCaptor.getValue().getCode()).isEqualTo(FomesConstants.FomesEventLog.Code.NOTIFICATION_RECEIVED);
    }

    @Test
    public void 노티메세지가_전송되었을_경우__메세지가_없으면__아무것도_하지않는다() {
        RemoteMessage remoteMessage = new RemoteMessage.Builder("noti")
                .build();

        subject.onMessageReceived(remoteMessage);

        verify(mockChannelManager, never()).sendNotification(any(), any());
        verify(mockEventLogService, never()).sendEventLog(any());
    }

    @Test
    public void 노티메세지가_전송되었을_경우__로그인한_상태가_아니면__아무것도_하지않는다() {
        when(mockSharedPreferenceHelper.hasAccessToken()).thenReturn(false);

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("아무거나키", "아무거나 값");
        RemoteMessage remoteMessage = new RemoteMessage.Builder("noti")
                .setData(dataMap)
                .build();

        subject.onMessageReceived(remoteMessage);

        verify(mockChannelManager, never()).sendNotification(any(), any());
        verify(mockEventLogService, never()).sendEventLog(any());
    }

    @Test
    public void 단기통계데이터전송Job_등록_시그널_노티가_전송되었을_경우__단기통계데이터전송Job을_등록한다() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "signal");
        dataMap.put("signal", "register_send_data_job");
        RemoteMessage remoteMessage = new RemoteMessage.Builder("noti")
                .setData(dataMap)
                .build();

        subject.onMessageReceived(remoteMessage);

        verify(mockJobManager).registerSendDataJob(1001);
    }

    @Test
    public void 토큰이_갱신되었을_경우__로그인한_유저인_경우__사용자푸시토큰_업데이트API를_호출한다() {
        when(mockSharedPreferenceHelper.getUserRegistrationToken()).thenReturn("NEW_TOKEN");

        subject.onNewToken("NEW_TOKEN");

        verify(mockSharedPreferenceHelper).setUserRegistrationToken("NEW_TOKEN");
        verify(mockUserService).updateRegistrationToken(eq("NEW_TOKEN"));
    }

    @Test
    public void 토큰이_갱신되었을_경우__로그인한_유저가_아닌_경우__갱신된_토큰정보를_로컬에만_저장한다() {
        when(mockSharedPreferenceHelper.hasAccessToken()).thenReturn(false);
        when(mockSharedPreferenceHelper.getUserRegistrationToken()).thenReturn("NEW_TOKEN");

        subject.onNewToken("NEW_TOKEN");

        verify(mockSharedPreferenceHelper).setUserRegistrationToken("NEW_TOKEN");
        verify(mockUserService, never()).updateRegistrationToken(any());
    }
}
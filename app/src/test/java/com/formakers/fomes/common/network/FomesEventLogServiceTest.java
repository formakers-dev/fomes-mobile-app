package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.EventLogAPI;
import com.formakers.fomes.common.network.vo.EventLog;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FomesEventLogServiceTest extends AbstractServiceTest {

    @Mock EventLogAPI mockEventLogAPI;
    @Mock SharedPreferencesHelper mockSharedPreferencesHelper;

    EventLogService subject;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        subject = new EventLogService(mockEventLogAPI, mockSharedPreferencesHelper);

        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");
    }

    @Test
    public void sendEventLogs_호출시__이벤트로그_등록을_요청한다() {
        EventLog eventLog = mock(EventLog.class);
        subject.sendEventLog(eventLog).subscribe(new TestSubscriber<>());

        verify(mockEventLogAPI).postEventLog(eq("TEST_ACCESS_TOKEN"), eq(eventLog));
    }
}
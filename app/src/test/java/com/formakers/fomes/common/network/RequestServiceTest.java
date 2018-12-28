package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.RequestAPI;
import com.formakers.fomes.helper.SharedPreferencesHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestServiceTest extends AbstractServiceTest {

    @Mock RequestAPI mockRequestAPI;
    @Mock SharedPreferencesHelper mockSharedPreferencesHelper;

    RequestService subject;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        subject = new RequestService(mockRequestAPI, mockSharedPreferencesHelper);

        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");
    }

    @Test
    public void getFeedbackRequest_호출시__참여가능한_리스트를_요청한다() {
        subject.getFeedbackRequest().subscribe(new TestSubscriber<>());

        verify(mockRequestAPI).getRequests(eq("TEST_ACCESS_TOKEN"));
    }
}
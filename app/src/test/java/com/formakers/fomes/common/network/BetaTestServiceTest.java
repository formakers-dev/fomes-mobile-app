package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.BetaTestAPI;
import com.formakers.fomes.common.helper.SharedPreferencesHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BetaTestServiceTest extends AbstractServiceTest {

    @Mock BetaTestAPI mockBetaTestAPI;
    @Mock SharedPreferencesHelper mockSharedPreferencesHelper;

    BetaTestService subject;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        subject = new BetaTestService(mockBetaTestAPI, mockSharedPreferencesHelper, getMockAPIHelper());

        when(mockSharedPreferencesHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");
    }

    @Test
    public void getBetaTests_호출시__참여가능한_리스트를_요청한다() {
        subject.getBetaTestList().subscribe(new TestSubscriber<>());

        verify(mockBetaTestAPI).getBetaTests(eq("TEST_ACCESS_TOKEN"));
    }

    @Test
    public void getFinishedBetaTestList_호출시__참여가능한_리스트를_요청한다() {
        subject.getFinishedBetaTestList().subscribe(new TestSubscriber<>());

        verify(mockBetaTestAPI).getFinishedBetaTests(eq("TEST_ACCESS_TOKEN"), eq(false));
    }

    @Test
    public void getDetailBetaTest_호출시__해당테스트의_상세정보를_요청한다() {
        subject.getDetailBetaTest("1234id").subscribe(new TestSubscriber<>());

        verify(mockBetaTestAPI).getDetailBetaTest(eq("TEST_ACCESS_TOKEN"), eq("1234id"));
    }

    @Test
    public void postCompleteBetaTest_호출시__해당테스트의_상세정보를_요청한다() {
        subject.postCompleteBetaTest("1234id").subscribe(new TestSubscriber<>());

        verify(mockBetaTestAPI).postCompleteBetaTest(eq("TEST_ACCESS_TOKEN"), eq("1234id"));
    }

}
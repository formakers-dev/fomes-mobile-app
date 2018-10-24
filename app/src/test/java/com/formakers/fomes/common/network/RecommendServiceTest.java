package com.formakers.fomes.common.network;

import com.formakers.fomes.common.network.api.RecommendAPI;
import com.formakers.fomes.helper.SharedPreferencesHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.observers.TestSubscriber;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecommendServiceTest extends AbstractServiceTest {    
    @Mock private SharedPreferencesHelper mockSharedPreferenceHelper;
    @Mock private RecommendAPI mockRecommendAPI;

    private RecommendService subject;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        subject = new RecommendService(mockRecommendAPI, mockSharedPreferenceHelper, getMockAPIHelper());
        when(mockSharedPreferenceHelper.getAccessToken()).thenReturn("TEST_ACCESS_TOKEN");
    }

    @Test
    public void requestSimilarAppsByDemographic_호출시__비슷한_인적사항_기준_추천_리스트_API를_호출한다() {
        subject.requestSimilarAppsByDemographic(1, 2).subscribe(new TestSubscriber<>());

        verify(mockRecommendAPI).getSimilarAppsByDemographic(anyString(), eq(1), eq(2));
    }
}
package com.appbee.appbeemobile.network;

import com.appbee.appbeemobile.model.DetailUsageStat;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HTTPServiceTest {

    @Before
    public void setup() throws Exception {

    }

    @Test
    public void sendDetailUsageStat_서버에_요청을_보내면_응답을_받는다() throws Exception {
        // Given
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("true"));
        mockWebServer.play(8080);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mockWebServer.getUrl("").toString())
                .build();

        HTTPService service = retrofit.create(HTTPService.class);

        List<DetailUsageStat> detailUsageStats = new ArrayList<>();
        detailUsageStats.add(new DetailUsageStat("testPackage", 100L, 200L, 100L));

        // When
        Call<Boolean> call = service.sendDetailUsageStat(detailUsageStats);
        Response<Boolean> response = call.execute();

        mockWebServer.shutdown();

        // Then
        Assert.assertTrue(response != null);
        Assert.assertTrue(response.isSuccessful());
        Assert.assertTrue(response.body());
    }

    @Test
    public void sendDetailUsageStat_서버에_요청을_보냈는데_404에러가_떴다() throws Exception {
        // 404 test

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        mockWebServer.play(8080);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mockWebServer.getUrl("").toString())
                .build();

        HTTPService service = retrofit.create(HTTPService.class);

        List<DetailUsageStat> detailUsageStats = new ArrayList<>();
        detailUsageStats.add(new DetailUsageStat("testPackage", 100L, 200L, 100L));

        // When
        Call<Boolean> call = service.sendDetailUsageStat(detailUsageStats);
        Response<Boolean> response = call.execute();

        mockWebServer.shutdown();

        // Then
        Assert.assertTrue(response != null);
        Assert.assertFalse(response.isSuccessful());
    }

//    @Test(expected = Exception.class)
//    public void sendDetailUsageStat_서버에_요청을_보냈는데_오류가_발생했다() throws Exception {
//        // failure test
//
//        MockWebServer mockWebServer = new MockWebServer();
//        mockWebServer.enqueue(new MockResponse().);
//        mockWebServer.play(8080);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(mockWebServer.getUrl("").toString())
//                .build();
//
//        HTTPService service = retrofit.create(HTTPService.class);
//
//        List<DetailUsageStat> detailUsageStats = new ArrayList<>();
//        detailUsageStats.add(new DetailUsageStat("testPackage", 100L, 200L, 100L));
//
//        // When
//        Call<Boolean> call = service.sendDetailUsageStat(detailUsageStats);
//        Response<Boolean> response = call.execute();
//
//        mockWebServer.shutdown();
//
//        // Then
//        Assert.assertTrue(response != null);
//        Assert.assertFalse(response.isSuccessful());
//    }

    @Test(expected = SocketTimeoutException.class)
    public void sendDetailUsageStat_서버에_요청을_보냈는데_응답이_안온다() throws Exception {
        // socket timeout test
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setResponseCode(505));
        mockWebServer.play(8080);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mockWebServer.getUrl("").toString())
                .build();

        HTTPService service = retrofit.create(HTTPService.class);

        List<DetailUsageStat> detailUsageStats = new ArrayList<>();
        detailUsageStats.add(new DetailUsageStat("testPackage", 100L, 200L, 100L));

        // When
        mockWebServer.shutdown();

        // Then
        service.sendDetailUsageStat(detailUsageStats).execute();
    }
}

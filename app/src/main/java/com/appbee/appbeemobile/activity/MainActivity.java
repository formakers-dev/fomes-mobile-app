package com.appbee.appbeemobile.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.manager.StatManager;
import com.appbee.appbeemobile.model.AppInfo;
import com.appbee.appbeemobile.model.DailyUsageStat;
import com.appbee.appbeemobile.model.DetailUsageStat;
import com.appbee.appbeemobile.network.HTTPService;
import com.appbee.appbeemobile.receiver.ScreenOffReceiver;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    StatManager statManager;

    ScreenOffReceiver screenOffReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((AppBeeApplication)getApplication()).getComponent().inject(this);

        confirmAuth();
        loadData();

        registerScreenOffReceiver();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(screenOffReceiver);
        super.onDestroy();
    }

    private void registerScreenOffReceiver() {
        screenOffReceiver = new ScreenOffReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOffReceiver, intentFilter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void confirmAuth() {
        boolean granted;
        AppOpsManager appOps = (AppOpsManager) getSystemService(APP_OPS_SERVICE);

        if(appOps != null) {
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
            if(mode == AppOpsManager.MODE_DEFAULT) {
                granted = checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS)== PackageManager.PERMISSION_GRANTED;
            }else {
                granted = (mode == AppOpsManager.MODE_ALLOWED);
            }
            if(!granted) {
                // 마시멜로 이상 버전에서는, 사용정보 접근 허용창에서 해당 어플리케이션의 시스템 접근을 허용해야 함
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intent, 0);
            }
        }
    }

    public void loadData() {
        // 앱 리스트
        for (AppInfo appInfo : statManager.getAppList()) {
            Log.d(TAG, "[AppInfo] " + appInfo.getPakageName() + ", " + appInfo.getAppName());
        }

        // 연간 통계정보
        Map<String, DailyUsageStat> userAppDailyUsageStatsForYear = statManager.getUserAppDailyUsageStatsForYear();
        for(String key : userAppDailyUsageStatsForYear.keySet()) {
            DailyUsageStat dailyUsageStat = userAppDailyUsageStatsForYear.get(key);
            Log.d(TAG, "[YearlyStats] " + dailyUsageStat.getPackageName() + "," + dailyUsageStat.getLastUsedDate() + "," + dailyUsageStat.getTotalUsedTime());
        }

        final List<DetailUsageStat> detailUsageStatList = statManager.getDetailUsageStats();
        for (DetailUsageStat detailUsageStat : detailUsageStatList) {
            Log.d(TAG, "[DetailUsageStats] " + detailUsageStat.getPackageName() + ", " + detailUsageStat.getStartTimeStamp() + ", " + detailUsageStat.getEndTimeStamp() + ", " + detailUsageStat.getTotalUsedTime());
        }

        sendDetailUsageStats(detailUsageStatList);
    }

    private void sendDetailUsageStats(final List<DetailUsageStat> detailUsageStatList) {
        // TODO : will move to netModule
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        OkHttpClient okHttpClient = builder.build();

        // TODO : will move to netModule
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.16.0.164:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)   // for logs
                .build();

        retrofit.create(HTTPService.class).sendDetailUsageStat(detailUsageStatList).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {


                if (response.isSuccessful()) {
                    Log.d(TAG, "Success to send DetailUsageStats");
                } else {
                    Log.d(TAG, "Fail to send DetailUsageStats");
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "failure!!! t=" + t.toString());
            }
        });
    }
}

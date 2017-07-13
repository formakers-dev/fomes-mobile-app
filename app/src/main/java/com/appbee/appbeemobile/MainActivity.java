package com.appbee.appbeemobile;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final SimpleDateFormat year = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREA);
    private static final SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss.SSS", Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        confirmAuth();
        init();
    }

    public void confirmAuth() {
        boolean granted;
        AppOpsManager appOps = (AppOpsManager) getSystemService(APP_OPS_SERVICE);
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

    public void init() {
        // TODO: 일년간 통계정보 가져오기
        getUserAppUsageStats();
        // TODO: 일주일동안 사용정보 가져오기
        getUserAppUsage();
    }

    public void getUserAppUsage() {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        long startTime = endTime - 1000*60*60;

        UsageStatsManager usm = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        UsageEvents usageEvents = usm.queryEvents(startTime, endTime);

        while(usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);

            if(event != null) {
                Log.d(TAG, "[" + event.getEventType() +"]" +year.format(event.getTimeStamp()) +"\t"+ event.getPackageName());
            }
        }
    }

    public void getUserAppUsageStats() {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        UsageStatsManager usm = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, startTime, endTime);

        Log.d(TAG, "size : " + usageStatsList.size());
        for (UsageStats stats : usageStatsList) {
            if(stats.getTotalTimeInForeground() > 0) {
                Log.d(TAG, "[YEAR] " + stats.getPackageName() + " / " + year.format(stats.getFirstTimeStamp())
                        + " / "  + year.format(stats.getLastTimeStamp())
                        + " / " + year.format(stats.getLastTimeUsed())
                        + " / " + hour.format(stats.getTotalTimeInForeground()));
            }
        }
    }
}

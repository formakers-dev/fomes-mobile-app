package com.appbee.appbeemobile.manager;


import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import com.appbee.appbeemobile.model.UsageStatEvent;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.USAGE_STATS_SERVICE;

public class SystemServiceBridge {
    private Context context;

    public SystemServiceBridge(Context context) {
        this.context = context;
    }

    public List<UsageStatEvent> getUsageStatEvents(long startTime, long endTime) {
        List<UsageStatEvent> usageStatEvents = new ArrayList<>();

        UsageStatsManager usageStatsManger = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

        if (usageStatsManger != null) {
            UsageEvents usageEvents = usageStatsManger.queryEvents(startTime, endTime);

            while (usageEvents.hasNextEvent()) {
                UsageEvents.Event event = new UsageEvents.Event();
                boolean hasNextEvent = usageEvents.getNextEvent(event);

                if (hasNextEvent) {
                    usageStatEvents.add(new UsageStatEvent(event.getPackageName(), event.getEventType(), event.getTimeStamp()));
                }
            }
        }

        return usageStatEvents;
    }
}

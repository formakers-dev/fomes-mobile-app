package com.formakers.fomes.common.job;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.formakers.fomes.BuildConfig;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JobManager {

    public static int JOB_ID_SEND_DATA = 1001;

    private Context context;

    private JobScheduler jobScheduler;

    @Inject
    public JobManager(Context context) {
        this.context = context;
        jobScheduler = (JobScheduler) this.context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    public int registerSendDataJob(int jobId) {
        return jobScheduler.schedule(new JobInfo.Builder(jobId, new ComponentName(context, SendDataJobService.class))
                .setMinimumLatency(BuildConfig.DEBUG ? 1L : 21600000L)   // 6 hours
                .setOverrideDeadline(BuildConfig.DEBUG ? 1L :28800000L) // 8 hours
                .setPersisted(true)
                .setRequiresCharging(true)
                .build());
    }

    public void cancelJob(int jobId) {
        jobScheduler.cancel(jobId);
    }
}

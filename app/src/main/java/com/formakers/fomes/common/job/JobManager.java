package com.formakers.fomes.common.job;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.common.util.Log;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JobManager {

    public static String TAG = "JobManager";
    public static int JOB_ID_SEND_DATA = 1001;

    private Context context;

    private JobScheduler jobScheduler;

    @Inject
    public JobManager(Context context) {
        this.context = context;
        jobScheduler = (JobScheduler) this.context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    public int registerSendDataJob(int jobId) {
        Log.d(TAG, "registerSendDataJob(" + jobId + ")");
        return jobScheduler.schedule(new JobInfo.Builder(jobId, new ComponentName(context, SendDataJobService.class))
                .setPeriodic(BuildConfig.DEBUG ? 1L : 21600000L) // 6 hours
                .setPersisted(true)
                .setRequiresCharging(true)
                .build());
    }

    public void cancelJob(int jobId) {
        Log.d(TAG, "cancelJob(" + jobId + ")");
        jobScheduler.cancel(jobId);
    }

    public boolean isRegisteredJob(int jobId) {
        Log.v(TAG, "isRegisteredJob(" + jobId + ")");

        List<JobInfo> registeredJobs = jobScheduler.getAllPendingJobs();
        Log.i(TAG, "registeredJobs size = " + registeredJobs.size());

        for (JobInfo jobInfo : registeredJobs) {
            if (jobInfo.getId() == jobId) {
                Log.i(TAG, String.valueOf(jobInfo));
                return true;
            }
        }

        return false;
    }
}

package com.example.android.moviesapplication.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Professor on 2/8/2018.
 */

public class DownloadMoviesJobService extends JobService {

    public static final String ACTION_DOWNLOAD_FINISHED = "actionDownloadFinished";
    private JobParameters job;
    private Intent fetchMoviesIntent;
    private static final String TAG = DownloadMoviesJobService.class.getSimpleName();
    private BroadcastReceiver downloadFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                context.unregisterReceiver(this); //Unregister receiver to avoid receiver leaks exception
            }catch (IllegalArgumentException e) {
                Log.e(TAG, "onReceive: ERROR\n"+e.toString());
            }
                boolean isSuccesfull =
                        intent.getBooleanExtra(FetchMoviesIntentService.KEY_JOB_SUCCESFULL, true);
                jobFinished(job, isSuccesfull);
        }
    };
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: JOB STARTED");
        job = jobParameters;
        fetchMoviesIntent = new Intent(this, FetchMoviesIntentService.class);
        startService(fetchMoviesIntent);
        IntentFilter filter = new IntentFilter(ACTION_DOWNLOAD_FINISHED);
        //Use LocalBroadcastManager to catch the intents only from your app
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadFinishedReceiver , filter);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(fetchMoviesIntent!= null) stopService(fetchMoviesIntent);
        return true;
    }
}

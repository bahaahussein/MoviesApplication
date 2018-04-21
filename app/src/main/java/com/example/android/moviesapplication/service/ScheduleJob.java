package com.example.android.moviesapplication.service;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

/**
 * Created by Professor on 2/9/2018.
 */

public class ScheduleJob {

    private static final int PERIODOCITY = (int)(TimeUnit.HOURS.toSeconds(24));
    private static final int INTERVAL =  (int)(TimeUnit.HOURS.toSeconds(1));

    private static final String JOB_TAG = "download_job_tag";
    private static boolean sInitialized;

    synchronized public static void scheduleDownloadMovies(Context context) {
        if(sInitialized) return;
        Log.d(TAG, "scheduleDownloadMovies: JOB STARTED");
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job job = dispatcher.newJobBuilder()
                .setTag(JOB_TAG)
                .setService(DownloadMoviesJobService.class)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(PERIODOCITY, PERIODOCITY + INTERVAL))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(job);
        sInitialized = true;
    }
}

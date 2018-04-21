package com.example.android.moviesapplication.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.android.moviesapplication.Activity.MainActivity;
import com.example.android.moviesapplication.R;
import com.example.android.moviesapplication.database.MoviesContract;
import com.example.android.moviesapplication.database.MoviesDbHelper;
import com.example.android.moviesapplication.model.Movie;
import com.example.android.moviesapplication.model.MovieResponse;
import com.example.android.moviesapplication.rest.ApiClient;
import com.example.android.moviesapplication.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Professor on 2/8/2018.
 */

public class FetchMoviesIntentService extends IntentService {

    private final static String API_KEY = "859dfc13219d03ce6c41670353955aa8";
    private final static String TAG = FetchMoviesIntentService.class.getSimpleName();
    public final static String KEY_JOB_SUCCESFULL = "succesfull";
    private final static int NOTIFICATION_ID = 07;

    public FetchMoviesIntentService() {
        super("FetchMoviesIntentService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: SERVICE STARTED");
        String[] sortPreferences = getResources().getStringArray(R.array.pref_sort_values);
        getMovies(sortPreferences[0]);
        getMovies(sortPreferences[1]);
    }

    private void getMovies(final String sortPreferences) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = apiService.getMovies(sortPreferences,API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse>call, Response<MovieResponse> response) {
                List<Movie> movies =  response.body().getMovies();
                updateDatabase(movies, sortPreferences);
                if(!sortPreferences.equals(getString(R.string.pref_sort_default))) {
                    finishJob(false);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, "error in retrofit\n" + t.toString());
                finishJob(true);
            }
        });
    }

    private void updateDatabase(List<Movie> movies, String type) {
        MoviesDbHelper dbHelper = new MoviesDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        deleteFromDatabase(db, type);
        insertIntoDatabse(db, movies, type);
        Cursor cursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME, null,
                MoviesContract.MoviesEntry.COLUMN_TYPE + " = ?",
                new String[]{type}, null, null, null);
        db.close();
        cursor.close();
        Log.d(TAG, "updateDatabase: DATABSE COUNT: " + cursor.getCount());
    }

    private void insertIntoDatabse(SQLiteDatabase db, List<Movie> movies, String type) {
        int size = movies.size();
        for (int i=0; i<size; i++) {
            Movie movie = movies.get(i);
            ContentValues cv = new ContentValues();
            cv.put(MoviesContract.MoviesEntry._ID, movie.getId());
            cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_TITLE, movie.getTitle());
            cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_POSTER, movie.getPoster());
            cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_OVERVIEW, movie.getOverview());
            cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_DATE, movie.getDate());
            cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_VOTE, movie.getVoteAverage());
            cv.put(MoviesContract.MoviesEntry.COLUMN_TYPE, type);
            long rowId = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, cv);
            if(rowId==-1) {
                Log.e(TAG, "addToDatabase: " + movie.getTitle());
            }
        }
    }


    private void deleteFromDatabase(SQLiteDatabase db,String type) {
        long x = db.delete(MoviesContract.MoviesEntry.TABLE_NAME,
                MoviesContract.MoviesEntry.COLUMN_TYPE + " = ?", new String[]{type});
        if(x<=0) {
            Log.d(TAG, "deleteFromDatabase: ERROR " + type);
        }
    }


    private void finishJob(boolean isSuccesfull) {
        // ...downloading stuff
        Intent downloadFinishedIntent = new Intent(DownloadMoviesJobService.ACTION_DOWNLOAD_FINISHED);
        downloadFinishedIntent.putExtra(KEY_JOB_SUCCESFULL, isSuccesfull);
        //Use LocalBroadcastManager to broadcast intent only within your app
        LocalBroadcastManager.getInstance(this).sendBroadcast(downloadFinishedIntent);
        if(isSuccesfull)
            buildNotification();
    }

    private void buildNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_local_movies)
                        .setContentTitle("NEW MOVIES !")
                        .setContentText("Check new movies");
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        // Gets an instance of the NotificationManager service
        NotificationManager notifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notifyMgr.notify(NOTIFICATION_ID, builder.build());
    }
}

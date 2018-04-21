package com.example.android.moviesapplication.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.moviesapplication.R;
import com.example.android.moviesapplication.adapter.MoviesAdapter;
import com.example.android.moviesapplication.database.MoviesContract;
import com.example.android.moviesapplication.database.MoviesDbHelper;
import com.example.android.moviesapplication.model.Movie;
import com.example.android.moviesapplication.model.MovieResponse;
import com.example.android.moviesapplication.rest.ApiClient;
import com.example.android.moviesapplication.rest.ApiInterface;
import com.example.android.moviesapplication.service.ScheduleJob;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener
        , MoviesAdapter.MovieOnClickHandler, LoaderManager.LoaderCallbacks<List<Movie>>{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int FORECAST_LOADER_ID = 20;

    private final static String API_KEY = "859dfc13219d03ce6c41670353955aa8";
    private static final String KEY_MOVIE_TYPE = "movie_type";
    public static final String KEY_DETAIL_ACTIVITY = "detail_activity";

    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private List<Movie> moviesList;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        ScheduleJob.scheduleDownloadMovies(this);
        mRecyclerView = findViewById(R.id.movies_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        loadMoviesFromDatabase();
    }

    private void loadMoviesFromDatabase() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MOVIE_TYPE, getSortPreferences());
        initLoader(FORECAST_LOADER_ID, bundle, this, getSupportLoaderManager());
    }

    private void getMovies(String sortPreferences) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = apiService.getMovies(sortPreferences,API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse>call, Response<MovieResponse> response) {
                moviesList = response.body().getMovies();
                Log.d(TAG, "onResponse: " + moviesList.size());
                mAdapter = new MoviesAdapter(getApplicationContext(), MainActivity.this, moviesList);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<MovieResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    private String getSortPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            loadMoviesFromDatabase();
        }
    }

    private void initLoader(final int loaderId, final Bundle args,
                                  final LoaderManager.LoaderCallbacks<List<Movie>> callbacks,
                                  final LoaderManager loaderManager) {
        final Loader<List<Movie>> loader = loaderManager.getLoader(loaderId);
        if (loader != null) {
            loaderManager.destroyLoader(loaderId);
        }
        loaderManager.restartLoader(loaderId, args, callbacks);
    }

    @Override
    public void onClick(Movie movie) {
        Intent startDetailActivityIntent = new Intent(this, DetailActivity.class);
        startDetailActivityIntent.putExtra(KEY_DETAIL_ACTIVITY, movie);
        startActivity(startDetailActivityIntent);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public List<Movie> loadInBackground() {
                String type = args.getString(KEY_MOVIE_TYPE);
                if(type==null || type.length()<=0) return null;
                Log.d(TAG, "loadInBackground: type is " + type);
                MoviesDbHelper dbHelper = new MoviesDbHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String[] values = getResources().getStringArray(R.array.pref_sort_values);
                Log.d(TAG, "loadInBackground: should be favorite " + values[2]);
                String selection = "";
                String[] selectionArgs = new String[1];
                if(type.equals(values[2])) {
                    selection = MoviesContract.MoviesEntry.COLUMN_NAME_FAVORITE + " = ?";
                    selectionArgs[0] = "1";
                } else {
                    selection = MoviesContract.MoviesEntry.COLUMN_TYPE + " = ?";
                    selectionArgs[0] = type;
                }
                Cursor cursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME, null,
                        selection, selectionArgs, null, null, null);
                int size = cursor.getCount();
                List<Movie> data = new ArrayList<Movie>(size);
                try {
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry._ID));
                        double vote = cursor.getDouble(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_VOTE));
                        String title = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_TITLE));
                        String overview = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_OVERVIEW));
                        String date = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_DATE));
                        String poster = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_POSTER));
                        boolean isFavorite = cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_FAVORITE))==1;
                        data.add(new Movie(id, vote, title, overview, date, poster, isFavorite));
                    }
                } finally {
                    cursor.close();
                }
                return data;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if(data==null || data.size()<=0) return;
        moviesList = data;
        mAdapter.setData(moviesList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }
}

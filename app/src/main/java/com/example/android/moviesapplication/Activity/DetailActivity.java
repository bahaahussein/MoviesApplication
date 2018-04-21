package com.example.android.moviesapplication.Activity;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviesapplication.R;
import com.example.android.moviesapplication.adapter.MoviesAdapter;
import com.example.android.moviesapplication.adapter.TrailerAdapter;
import com.example.android.moviesapplication.database.MoviesContract;
import com.example.android.moviesapplication.database.MoviesDbHelper;
import com.example.android.moviesapplication.model.Movie;
import com.example.android.moviesapplication.model.MovieResponse;
import com.example.android.moviesapplication.model.ReviewResponse;
import com.example.android.moviesapplication.model.TrailerResponse;
import com.example.android.moviesapplication.rest.ApiClient;
import com.example.android.moviesapplication.rest.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerOnClickHandler,
        LoaderManager.LoaderCallbacks<Boolean> {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private final static String API_KEY = "859dfc13219d03ce6c41670353955aa8";
    private final static String FAVORITE_KEY = "favorite-key";
    private static final int FAVORITE_LOADER_ID = 20;
    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    private ShareActionProvider mShareActionProvider;
    private String firstTrailerKey;

    private Movie movie;
    private TrailerAdapter mAdapter;
    @BindView(R.id.button_favorite) Button mFavoriteButton;
    @BindView(R.id.iv_detail_poster) ImageView mPosterView;
    @BindView(R.id.tv_date) TextView mDateView;
    @BindView(R.id.tv_title) TextView mTitleView;
    @BindView(R.id.ratingbar) RatingBar mRatingBar;
    @BindView(R.id.tv_overview) TextView mOverview;
    @BindView(R.id.tv_review) TextView mReview;
    @BindView(R.id.trailers_recycler_view) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        movie = getIntent().getExtras().getParcelable(MainActivity.KEY_DETAIL_ACTIVITY);
        showBackButtonInActionBar();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TrailerAdapter(this);
        setViews();
        getReviews();
        getTrailers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent(createShareIntent());

        // Return true to display menu
        return true;
    }

    private Intent createShareIntent() {
        String extra = "";
        if(firstTrailerKey==null) {
            extra = "I LIKE THIS MOVIE " + movie.getTitle().toUpperCase() + " !";
        } else {
            extra = YOUTUBE_BASE_URL + firstTrailerKey;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                extra);
        return shareIntent;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void showBackButtonInActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if(id==R.id.action_settings) {
            startActivity(new Intent(DetailActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTrailers() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<TrailerResponse> call = apiService.getTrailers(movie.getId(),API_KEY);
        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse>call, Response<TrailerResponse> response) {
                List<TrailerResponse.Trailer> trailersList = response.body().getTrailers();
                if(trailersList.size()>0)
                    firstTrailerKey = trailersList.get(0).getKey();
                setShareIntent(createShareIntent());
                mAdapter.setTrailerList(trailersList);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<TrailerResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, "ERROR IN LOADING TRAILERS" + t.toString());
            }
        });
    }

    private void getReviews() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ReviewResponse> call = apiService.getReviews(movie.getId(),API_KEY);
        Log.d(TAG, "REVIEWS ENTERED FUNCTION " + movie.getId() );
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse>call, Response<ReviewResponse> response) {
                List<ReviewResponse.Review> reviewList = response.body().getReviews();
                Log.d(TAG, "REVIEWS SIZE: " + reviewList.size());
                for (ReviewResponse.Review review : reviewList) {
                    Log.d(TAG, "REVIEW: " + review.getContent());
                    mReview.append(review.getContent()+"\n\n");
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, "REVIEWS FAILURE: " + t.toString());
            }
        });
    }

    private void setViews() {
        loadPoster();
        setButtonText();
        mTitleView.setText(movie.getTitle());
        mDateView.setText(movie.getDate());
        mRatingBar.setRating((float)(movie.getVoteAverage()/2));
        mOverview.setText(movie.getOverview());
    }

    private void setButtonText() {
        if (movie.isFavortie()) {
            mFavoriteButton.setText("REMOVE FROM FAVORITE");
        } else {
            mFavoriteButton.setText("ADD TO FAVORITE");
        }
    }

    private void initLoader(final int loaderId, final Bundle args,
                            final LoaderManager.LoaderCallbacks<Boolean> callbacks,
                            final LoaderManager loaderManager) {
        final Loader<Boolean> loader = loaderManager.getLoader(loaderId);
        if (loader != null) {
            loaderManager.destroyLoader(loaderId);
        }
        loaderManager.restartLoader(loaderId, args, callbacks);
    }

    @OnClick(R.id.button_favorite)
    public void favoriteClicked(View view) {
        Bundle args = new Bundle();
        args.putBoolean(FAVORITE_KEY, movie.isFavortie());
        initLoader(FAVORITE_LOADER_ID, args, this, getSupportLoaderManager());
    }

    private void loadPoster() {
        String baseUrl = "http://image.tmdb.org/t/p/w342/";
        String url = baseUrl + movie.getPoster();
        Picasso.with(this).load(url).into(mPosterView);
    }

    @Override
    public void onClick(String trailerContent) {
        String url = YOUTUBE_BASE_URL + trailerContent;
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(youtubeIntent);
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Boolean>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Boolean loadInBackground() {
                boolean isFavorite = args.getBoolean(FAVORITE_KEY);
                MoviesDbHelper dbHelper = new MoviesDbHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if(isFavorite) {
                    // make it NOT favorite
                    ContentValues cv = new ContentValues();
                    cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_FAVORITE, 0);
                    int test = db.update(MoviesContract.MoviesEntry.TABLE_NAME, cv,
                            MoviesContract.MoviesEntry._ID + " = ?",
                            new String[]{movie.getId()+""});
                    Log.d(TAG, "loadInBackground: " + test);
                    if (test>0) return false;
                } else {
                    // make the movie favorite
                    ContentValues cv = new ContentValues();
                    cv.put(MoviesContract.MoviesEntry.COLUMN_NAME_FAVORITE, 1);
                    int test = db.update(MoviesContract.MoviesEntry.TABLE_NAME, cv,
                            MoviesContract.MoviesEntry._ID + " = ?",
                            new String[]{movie.getId()+""});
                    Log.d(TAG, "loadInBackground: " + test);
                    if(test>0) return true;
                }
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        if(data==null) return;
        movie.setFavortie(data);
        setButtonText();
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }
}

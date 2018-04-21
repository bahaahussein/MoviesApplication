package com.example.android.moviesapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.moviesapplication.R;
import com.example.android.moviesapplication.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private final static String TAG = MoviesAdapter.class.getSimpleName();
    private final static String BASE_URL = "http://image.tmdb.org/t/p/w185/";

    private List<Movie> moviesList;
    private Context context;
    private MovieOnClickHandler mClickHandler;

    public interface MovieOnClickHandler {
        void onClick(Movie movie);
    }


    public MoviesAdapter(Context context, MovieOnClickHandler clickHandler, List<Movie> moviesList) {
        this.moviesList = moviesList;
        this.context = context;
        this.mClickHandler = clickHandler;
    }

    public MoviesAdapter(Context context, MovieOnClickHandler clickHandler) {
        this.context = context;
        this.mClickHandler = clickHandler;
    }

    public void setData(List<Movie> movies) {
        moviesList = movies;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        String url = BASE_URL + movie.getPoster();
        Picasso.with(context).load(url).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        if (moviesList==null) return 0;
        return moviesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_list_poster) public ImageView poster;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Log.d(TAG, "onClick: move is clicked " + adapterPosition);
            Movie movie = moviesList.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }
}

package com.example.android.moviesapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.moviesapplication.R;
import com.example.android.moviesapplication.model.Movie;
import com.example.android.moviesapplication.model.TrailerResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<TrailerResponse.Trailer> trailerList;
    private TrailerOnClickHandler mClickHandler;

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_item, parent, false);

        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.trailerTextView.setText("Trailer " + (position+1));
    }

    @Override
    public int getItemCount() {
        if(trailerList==null) return 0;
        return trailerList.size();
    }

    public interface TrailerOnClickHandler {
        void onClick(String trailerContent);
    }

    public void setTrailerList(List<TrailerResponse.Trailer> trailerList) {
        this.trailerList = trailerList;
    }

    public TrailerAdapter(TrailerOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_trailer_item) public TextView trailerTextView;

        public TrailerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(trailerList.get(adapterPosition).getKey());
        }
    }
}

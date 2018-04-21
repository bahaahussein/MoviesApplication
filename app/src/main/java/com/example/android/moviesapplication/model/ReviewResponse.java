package com.example.android.moviesapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Professor on 2/13/2018.
 */

public class ReviewResponse {

    @SerializedName("results")
    private List<Review> reviews;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public class Review {
        @SerializedName("content")
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

package com.example.android.moviesapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable{

    @SerializedName("id")
    private int id;
    @SerializedName("vote_average")
    private double voteAverage;
    @SerializedName("title")
    private String title;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String date;
    @SerializedName("poster_path")
    private String poster;
    private boolean isFavortie;

    public Movie(int id, double voteAverage, String title, String overview, String date,
                 String poster, boolean isFavortie) {
        this.id = id;
        this.voteAverage = voteAverage;
        this.title = title;
        this.overview = overview;
        this.date = date;
        this.poster = poster;
        this.isFavortie = isFavortie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public boolean isFavortie() {
        return isFavortie;
    }

    public void setFavortie(boolean favortie) {
        isFavortie = favortie;
    }

    @Override
    public String toString() {
        return "id: " + id + ", average: " + voteAverage + ", tile: " + title +
                ", poster: " + poster + ", date: " + date + ", overview: " +
                overview + ", favorite: " + isFavortie + "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeDouble(this.voteAverage);
        parcel.writeString(this.title);
        parcel.writeString(this.overview);
        parcel.writeString(this.date);
        parcel.writeString(this.poster);
        parcel.writeByte((byte) (isFavortie ? 1 : 0));     //if isFavorite == true, byte == 1
    }

    public Movie(Parcel in) {
        this.id = in.readInt();
        this.voteAverage = in.readDouble();
        this.title = in.readString();
        this.overview = in.readString();
        this.date = in.readString();
        this.poster = in.readString();
        this.isFavortie = in.readByte() != 0;     //isFavorite == true if byte != 0
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

package com.blogspot.garvitdelhi.hotmovies;

import java.io.Serializable;

/**
 * Created by garvitdelhi on 6/2/16.
 */
public class SingleMovie implements Serializable {
    public SingleMovie(
            String title,
            String posterPath,
            String poserPathLarge,
            String overview,
            String releaseDate,
            String voteAverage) {

        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.poserPathLarge = poserPathLarge;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return this.overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return this.voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPoserPathLarge() {
        return poserPathLarge;
    }

    public void setPoserPathLarge(String poserPathLarge) {
        this.poserPathLarge = poserPathLarge;
    }

    private String title;
    private String posterPath;
    private String overview;
    private String releaseDate;
    private String poserPathLarge;
    private String voteAverage;
}

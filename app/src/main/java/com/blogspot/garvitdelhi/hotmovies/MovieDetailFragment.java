package com.blogspot.garvitdelhi.hotmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("SingleMovie")) {
            SingleMovie singleMovie = (SingleMovie)intent.getSerializableExtra("SingleMovie");
            TextView movieTitle = (TextView) view.findViewById(R.id.movie_name);
            movieTitle.setText(singleMovie.getTitle());
            ImageView moviePoster = (ImageView) view.findViewById(R.id.movie_poster);
            Picasso.with(getContext()).load(singleMovie.getPoserPathLarge()).into(moviePoster);
            TextView movieVoteAvg = (TextView) view.findViewById(R.id.movie_vote_average);
            movieVoteAvg.setText(
                    getContext().getString(R.string.movie_vote_avg) + " "
                    + singleMovie.getVoteAverage());

            TextView movieReleaseDate = (TextView) view.findViewById(R.id.movie_release_date);
            movieReleaseDate.setText(
                    getContext().getString(R.string.movie_release_date) + " "
                            + singleMovie.getReleaseDate());

            TextView movieOverview = (TextView) view.findViewById(R.id.movie_overview);
            movieOverview.setText(
                    getContext().getString(R.string.movie_overview) + " "
                            + singleMovie.getOverview());
        }

        return view;
    }
}

package com.blogspot.garvitdelhi.hotmovies;

import android.app.ActionBar;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by garvitdelhi on 3/2/16.
 */
public class ImageAdapter extends ArrayAdapter<SingleMovie> {

    public ImageAdapter(Context context, List<SingleMovie> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SingleMovie movie = getItem(position);
        String ImageURL = movie.getPosterPath();
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_poster, parent, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.grid_item_poster_imageview);
        TextView movie_name = (TextView) rootView.findViewById(R.id.grid_item_movie_name_textview);
        movie_name.setText(movie.getTitle());
        Picasso.with(getContext()).load(ImageURL).into(imageView);
        return rootView;
    }
}

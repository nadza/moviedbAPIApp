package com.nadza.anapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadza.anapp.Models.Movie;
import com.nadza.anapp.R;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

public class MovieAdapter extends ArrayAdapter<Movie> {
    Context context;
    ArrayList<Movie> movieList;

    public MovieAdapter(Context context, ArrayList<Movie> movieList) {
        super(context, 0, movieList);
        this.context=context;
        this.movieList=movieList;
    }

    private static class ViewHolder{
        private TextView title;
        private ImageView thumbnailImage;
    }

    @Override
    public Movie getItem(int position) {
        return movieList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) listItemView.findViewById(R.id.title);
            holder.thumbnailImage = (ImageView) listItemView.findViewById(R.id.image);
            listItemView.setTag(holder);
        } else {

            holder = (ViewHolder) listItemView.getTag();
        }

        Movie currentMovie = getItem(position);

        assert currentMovie != null;
        holder.title.setText(currentMovie.getTitle());

        if (currentMovie.getPosterPath() == null) {
            holder.thumbnailImage.setImageResource(R.mipmap.no_image);
        } else {
            URL url = null;
            try {
                url = new URL("https://image.tmdb.org/t/p/w500/"+currentMovie.getPosterPath());
            } catch (MalformedURLException e) {

            }
            Picasso.get().load(String.valueOf(url)).into(holder.thumbnailImage);
        }
        return listItemView;
    }
}
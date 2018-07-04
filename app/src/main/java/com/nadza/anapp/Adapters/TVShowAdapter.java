package com.nadza.anapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadza.anapp.Models.Movie;
import com.nadza.anapp.Models.TVShow;
import com.nadza.anapp.R;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

public class TVShowAdapter extends ArrayAdapter<TVShow> {
    Context context;
    ArrayList<TVShow> tvlist;

    public TVShowAdapter(Context context, ArrayList<TVShow> showList) {
        super(context, 0, showList);
        this.context=context;
        this.tvlist=showList;
    }

    private static class ViewHolder{
        private TextView title;
        private ImageView thumbnailImage;
    }
    @Override
    public TVShow getItem(int position) {
        return tvlist.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        TVShowAdapter.ViewHolder holder;
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item, parent, false);
            holder = new TVShowAdapter.ViewHolder();
            holder.title = (TextView) listItemView.findViewById(R.id.title);
            holder.thumbnailImage = (ImageView) listItemView.findViewById(R.id.image);
            listItemView.setTag(holder);
        } else {

            holder = (TVShowAdapter.ViewHolder) listItemView.getTag();
        }

        TVShow currentShow = getItem(position);

        assert currentShow != null;
        holder.title.setText(currentShow.getName());

        if (currentShow.getPosterPath() == null) {
            holder.thumbnailImage.setImageResource(R.mipmap.no_image);
        } else {
            URL url = null;
            try {
                url = new URL("https://image.tmdb.org/t/p/w500/"+currentShow.getPosterPath());
            } catch (MalformedURLException e) {

            }
            Picasso.get().load(String.valueOf(url)).into(holder.thumbnailImage);
        }
        return listItemView;
    }
}
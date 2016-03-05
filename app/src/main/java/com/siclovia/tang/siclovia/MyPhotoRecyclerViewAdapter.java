package com.siclovia.tang.siclovia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;


public class MyPhotoRecyclerViewAdapter extends RecyclerView.Adapter<MyPhotoRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private final List<Photo> photoList;

    public MyPhotoRecyclerViewAdapter(Context context, List<Photo> sponsers) {
        this.context = context;
        this.photoList = sponsers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position < photoList.size()) {
            Picasso.with(context)
                    .load(photoList.get(position).uri)
                    .resize(200, 0)
                    .config(Bitmap.Config.RGB_565)
                    .into(holder.imgLogo);
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imgLogo;

        public ViewHolder(View view) {
            super(view);
            imgLogo = (ImageView) view.findViewById(R.id.gallery_ivPhoto);
        }

    }

}

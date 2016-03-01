package com.siclovia.tang.siclovia;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.List;


public class MyPhotoRecyclerViewAdapter extends RecyclerView.Adapter<MyPhotoRecyclerViewAdapter.ViewHolder> {

    private final List<Photo> photoList;

    public MyPhotoRecyclerViewAdapter(List<Photo> sponsers) {
        photoList = sponsers;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.imgLogo.setImageBitmap(photoList.get(position).Image);

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

package com.siclovia.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.Photo;
import com.PhotoActivity;
import com.siclovia.R;
import com.siclovia.RouteActivity;
import com.squareup.picasso.Picasso;

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

            holder.imgLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PhotoActivity.class);
                    intent.putExtra("position", position);
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    intent.putExtra("locationX", location[0]);//必须
                    intent.putExtra("locationY", location[1]);//必须
                    intent.putExtra("uri", photoList.get(position).uri);
                    intent.putExtra("name", photoList.get(position).fileName);
                    intent.putExtra("width", v.getWidth());//必须
                    intent.putExtra("height", v.getHeight());//必须
                    v.getContext().startActivity(intent);
                    ((RouteActivity)v.getContext()).overridePendingTransition(0, 0);
                }
            });
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

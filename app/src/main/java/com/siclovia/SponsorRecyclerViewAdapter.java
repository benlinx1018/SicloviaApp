package com.siclovia;



import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Utilities;


import java.util.List;

public class SponsorRecyclerViewAdapter extends RecyclerView.Adapter<SponsorRecyclerViewAdapter.ViewHolder> {

    private final List<Sponsor> sponserList;

    public SponsorRecyclerViewAdapter(List<Sponsor> sponsers) {
        sponserList = sponsers;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sponsor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtName.setText(sponserList.get(position).name);
        holder.txtType.setText(sponserList.get(position).type + " Sponsor");
        holder.imgLogo.setImageBitmap(sponserList.get(position).logoImage);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.openActionView(v.getContext(), Uri.parse(sponserList.get(position).webLink));
            }
        });
    }

    @Override
    public int getItemCount() {
        return sponserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtName,txtType;
        public final ImageView imgLogo;
        public View mView;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtName  = (TextView) view.findViewById(R.id.tv_sponsor_name);
            txtType = (TextView) view.findViewById(R.id.tv_sponsor_type);
            imgLogo = (ImageView) view.findViewById(R.id.iv_sponsor_logo);
        }

    }
}

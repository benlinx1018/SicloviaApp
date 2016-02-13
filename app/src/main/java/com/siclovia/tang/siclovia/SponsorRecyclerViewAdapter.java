package com.siclovia.tang.siclovia;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.siclovia.tang.siclovia.dummy.DummyContent.DummyItem;

import java.util.List;

public class SponsorRecyclerViewAdapter extends RecyclerView.Adapter<SponsorRecyclerViewAdapter.ViewHolder> {

    private final List<Sponsor> mValues;


    public SponsorRecyclerViewAdapter(List<Sponsor> items) {
        mValues = items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sponsor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txtName.setText(mValues.get(position).name);
        holder.txtType.setText(mValues.get(position).type+" Sponsor");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Hi,"+holder.txtName,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView txtName,txtType;
        public Sponsor sp;
        public View mView;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtName  = (TextView) view.findViewById(R.id.tv_sponsor_name);
            txtType = (TextView) view.findViewById(R.id.tv_sponsor_type);
        }

 }
    public class Sponsor {
        @SerializedName("name")
        public String name;
        @SerializedName("link")
        public String link;
        @SerializedName("logo")
        public String logo;
        @SerializedName("type")
        public String type;
    }
}

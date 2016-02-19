package com.siclovia.tang.siclovia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.siclovia.tang.siclovia.dummy.DummyContent.DummyItem;

import java.io.File;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SponsorRecyclerViewAdapter extends RecyclerView.Adapter<SponsorRecyclerViewAdapter.ViewHolder> {

    private final List<Sponsor> sponserList;


    public SponsorRecyclerViewAdapter(List<Sponsor> items) {
        sponserList = items;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sponsor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.txtName.setText(sponserList.get(position).name);
        holder.txtType.setText(sponserList.get(position).type+" Sponsor");
        AsyncHttpClient client = new AsyncHttpClient();
        String logoUri = "http://www.joinymca.org/siclovia/images/sponsors/"+sponserList.get(position).logo;
        client.get(logoUri, new FileAsyncHttpResponseHandler(holder.mView.getContext()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                holder.imgLogo.setImageBitmap(BitmapFactory.decodeFile(response.getPath()));
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Hi,"+holder.txtName,Toast.LENGTH_SHORT).show();
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
        public Sponsor sp;
        public View mView;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtName  = (TextView) view.findViewById(R.id.tv_sponsor_name);
            txtType = (TextView) view.findViewById(R.id.tv_sponsor_type);
            imgLogo = (ImageView) view.findViewById(R.id.iv_sponsor_logo);
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

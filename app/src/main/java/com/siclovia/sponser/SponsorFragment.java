package com.siclovia.sponser;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.siclovia.R;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class SponsorFragment extends Fragment {
    SponsorRecyclerViewAdapter sponsorAdapter;
    public final List<Sponsor> sponsors = new ArrayList<>();

    public SponsorFragment() {
    }

    @SuppressWarnings("unused")
    public static SponsorFragment newInstance() {
    
        return new SponsorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize adapter
        sponsorAdapter =new SponsorRecyclerViewAdapter(sponsors);
        //get all sponsors
        new AsyncHttpClient().get("http://joinymca.org/siclovia/json/sponsors.php", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Gson gson = new GsonBuilder().create();
                        //Serialize Json Data
                        Sponsors sponsorsObj = gson.fromJson(res, Sponsors.class);
                        //add to adapter list
                        sponsors.addAll(sponsorsObj.sponsors);
                        //get each sponsor's web logo Image
                        for (final Sponsor sponsor : sponsors) {
                            AsyncHttpClient client = new AsyncHttpClient();
                            String logoUri = "http://www.joinymca.org/siclovia/images/sponsors/" + sponsor.logoFileName;
                            client.get(logoUri, new FileAsyncHttpResponseHandler(getActivity().getBaseContext()) {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, File response) {
                                    sponsor.logoImage = BitmapFactory.decodeFile(response.getPath());
                                    sponsorAdapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );
    }
    class Sponsors {

        public List<Sponsor> sponsors;

        public Sponsors() {
            sponsors = new ArrayList<>();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_sponsor_list, container, false);
        View view = layoutView.findViewById(R.id.sponsor_list);

        if (view instanceof RecyclerView) {
            Context context = layoutView.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            // Set the adapter
            recyclerView.setAdapter(sponsorAdapter);
        }
        return layoutView;
    }

}

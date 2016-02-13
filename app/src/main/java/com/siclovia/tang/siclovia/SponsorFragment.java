package com.siclovia.tang.siclovia;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.siclovia.tang.siclovia.dummy.DummyContent;
import com.siclovia.tang.siclovia.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class SponsorFragment extends Fragment {
    SponsorRecyclerViewAdapter sponsorAdapter;
    public List<SponsorRecyclerViewAdapter.Sponsor> sponsors ;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SponsorFragment() {
        sponsors= new ArrayList<>();
        sponsorAdapter =new SponsorRecyclerViewAdapter(sponsors);

    }

    @SuppressWarnings("unused")
    public static SponsorFragment newInstance() {
        SponsorFragment fragment = new SponsorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://joinymca.org/siclovia/json/sponsors.php", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Gson gson = new GsonBuilder().create();

                        // Define Response class to correspond to the JSON response returned
                        Sponsors sponsorsObj = gson.fromJson(res, Sponsors.class);
                        sponsors.addAll(sponsorsObj.sponsors);
                        sponsorAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );
    }
    class Sponsors {

        public List<SponsorRecyclerViewAdapter.Sponsor> sponsors;

        public Sponsors() {
            sponsors = new ArrayList<>();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sponsor_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setAdapter(sponsorAdapter);
        }
        return view;
    }

}

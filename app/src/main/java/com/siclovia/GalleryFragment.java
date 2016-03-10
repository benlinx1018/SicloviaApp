package com.siclovia;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.siclovia.gallery.MyPhotoRecyclerViewAdapter;


import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class GalleryFragment extends Fragment {

    MyPhotoRecyclerViewAdapter photoAdapter;
    public final List<Photo> photos = new ArrayList<>();

    public GalleryFragment() {
    }

    @SuppressWarnings("unused")
    public static GalleryFragment newInstance() {


        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoAdapter = new MyPhotoRecyclerViewAdapter(getActivity().getBaseContext(),photos);
        //get all sponsors
        new AsyncHttpClient().get("http://joinymca.org/siclovia/json/gallery.php", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Gson gson = new GsonBuilder().create();
                        //Serialize Json Data
                        Photos photosObj = gson.fromJson(res, Photos.class);
                        //add to adapter list
                        photos.addAll(photosObj.photos);
                        photoAdapter.notifyDataSetChanged();
                        //get each sponsor's web logo Image
//                        for(int i=0;i<photos.size();i++)
//                     {

//                            AsyncHttpClient client = new AsyncHttpClient();
//
//                            client.get(photo.uri, new FileAsyncHttpResponseHandler(getActivity().getBaseContext()) {
//                                @Override
//                                public void onSuccess(int statusCode, Header[] headers, File response) {
//                                    photo.Image = BitmapFactory.decodeFile(response.getPath());
//                                    photoAdapter.notifyDataSetChanged();
//                                }
//
//                                @Override
//                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
//
//                                }
//                            });
//                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );

    }

    class Photos {
        @SerializedName("photo")
        public List<Photo> photos;

        public Photos() {
            photos = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_photo_list, container, false);
        View view = layoutView.findViewById(R.id.gallery_list);

        if (view instanceof RecyclerView) {
            Context context = layoutView.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            // Set the adapter
            recyclerView.setAdapter(photoAdapter);
        }
        return layoutView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}

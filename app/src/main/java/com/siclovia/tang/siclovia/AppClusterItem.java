package com.siclovia.tang.siclovia;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class AppClusterItem implements ClusterItem {

    private final LatLng mPosition;
    private int mIcon;
    private String mSnippet;
    private String mTitle;

    public AppClusterItem(double latitude, double longitude,int Icon,String Title,String Snippet) {
        mPosition = new LatLng(latitude, longitude);
        mIcon = Icon;
        mSnippet = Snippet;
        mTitle = Title;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
    public int getIcon() {
        return mIcon;
    }
    public String getSnippet() {
        return mSnippet;
    }
    public String getTitle() {
        return mTitle;
    }
}
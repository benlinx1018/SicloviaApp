package com.siclovia;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Randy on 2/20/2016.
 */
public class Marker {
    @SerializedName("name")
    public String name;
    //{123,21}
    @SerializedName("location")
    public String location;
    @SerializedName("subtitle")
    public String subTitle;
    @SerializedName("type")
    public int type;
    @SerializedName("stoptype")
    public String stoptype;
}
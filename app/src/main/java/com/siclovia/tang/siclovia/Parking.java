package com.siclovia.tang.siclovia;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Randy on 2/20/2016.
 */
public class Parking {
    @SerializedName("name")
    public String name;
    @SerializedName("location")
    public String location;
    @SerializedName("subtitle")
    public String subTitle;
    @SerializedName("type")
    public int type;
    @SerializedName("stoptype")
    public String stoptype;
}
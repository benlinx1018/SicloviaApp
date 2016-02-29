package com.siclovia.tang.siclovia;

import com.google.gson.annotations.SerializedName;

public class Photo {
    @SerializedName("name")
    public String title;
    @SerializedName("subtitle")
    public String subTitle;
    @SerializedName("time")
    public String time;
    @SerializedName("ampm")
    public String ampm;
    @SerializedName("lat")
    public String lat;
    @SerializedName("long")
    public String lon;
    @SerializedName("date")
    public String date;

}

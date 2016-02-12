package com.siclovia.tang.siclovia;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;



public class Event {
    @SerializedName("name")
    public String title;
    @SerializedName("subtitle")
    public String subTitle;
    @SerializedName("time")
    public String time;
    @SerializedName("ampm")
    public String ampm;
}

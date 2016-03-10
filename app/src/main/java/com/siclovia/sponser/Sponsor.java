package com.siclovia.sponser;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;


public class Sponsor {
    @SerializedName("name")
    public String name;
    @SerializedName("link")
    public String webLink;
    @SerializedName("logo")
    public String logoFileName;
    @SerializedName("type")
    public String type;

    public Bitmap logoImage;
}

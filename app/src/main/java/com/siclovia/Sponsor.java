package com.siclovia;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lin-PC on 2016/2/21.
 */
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

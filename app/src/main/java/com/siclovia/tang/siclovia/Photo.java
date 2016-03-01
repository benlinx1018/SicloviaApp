package com.siclovia.tang.siclovia;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class Photo {
    @SerializedName("fullpath")
    public String uri;
    @SerializedName("filename")
    public String fileName;

    public Bitmap Image;
}

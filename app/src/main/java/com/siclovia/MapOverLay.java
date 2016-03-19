package com.siclovia;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by PSG-RDAA on 2016/3/18.
 */
public class MapOverLay {
    @SerializedName("map_image")
    public String map_image;
    @SerializedName("date")
    public String date;

    public android_map android_map;
    public ios_map ios_map;
    public class ios_map{
        @SerializedName("map_center")
        public String map_center;
        @SerializedName("load_center")
        public String load_center;
        @SerializedName("top_right")
        public String top_right;
        @SerializedName("bottom_left")
        public String bottom_left;
    }

    public class android_map{
        @SerializedName("map_center")
        public String map_center;
        @SerializedName("load_center")
        public String load_center;
        @SerializedName("width")
        public String width;
        @SerializedName("rotate")
        public String rotate;
    }
}

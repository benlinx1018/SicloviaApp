package com;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Lin on 2016/3/6.
 */
public class Utilities {

    public static final String TAG = "Soclovia";
    public static void openActionView(Context context,Uri uri){

        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(i);
    }
}

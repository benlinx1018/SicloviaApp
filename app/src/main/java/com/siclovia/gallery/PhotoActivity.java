package com.siclovia.gallery;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.components.SmoothImageView;
import com.siclovia.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;

public class PhotoActivity extends AppCompatActivity {
    private boolean isSaved =false;
    private String newUri;
    private Target t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        final String uri = getIntent().getStringExtra("uri");
        final String name = getIntent().getStringExtra("name");
        int mPosition = getIntent().getIntExtra("position", 0);
        int mLocationX = getIntent().getIntExtra("locationX", 0);
        int mLocationY = getIntent().getIntExtra("locationY", 0);
        int mWidth = getIntent().getIntExtra("width", 0);
        int mHeight = getIntent().getIntExtra("height", 0);

        SmoothImageView imageView = new SmoothImageView(this);
        imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
        imageView.transformIn();
        imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        setContentView(imageView);
        Picasso.with(this)
                .load(uri)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imageView);
        t = new TargetPhoneGallery(getBaseContext(),getContentResolver(), name, "siclovia");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSaved)
                {

                    Toast.makeText(PhotoActivity.this, "Has been downloaded", Toast.LENGTH_SHORT).show();
                }
                else {

                    Picasso.with(getBaseContext()).load(uri).into(t);
                }
            }
        });
    }
    public class TargetPhoneGallery implements Target
    {
        private final WeakReference<ContentResolver> resolver;
        private final String name;
        private final String desc;
        private final Context context;
        public TargetPhoneGallery(Context context,ContentResolver r, String name, String desc)
        {
            this.context =context;
            this.resolver = new WeakReference<>(r);
            this.name = name;
            this.desc = desc;
        }

        @Override
        public void onPrepareLoad (Drawable arg0)
        {
        }

        @Override
        public void onBitmapLoaded (Bitmap bitmap, Picasso.LoadedFrom arg1)
        {

            ContentResolver r = resolver.get();
            if (r != null)
            {
                newUri= MediaStore.Images.Media.insertImage(r, bitmap, name, desc);

                if(newUri!=null) {
                    Toast.makeText(context, "Download complete!!", Toast.LENGTH_SHORT).show();
                    isSaved = true;
                }
                else
                {
                    Toast.makeText(context, "Save fail!!", Toast.LENGTH_SHORT).show();
                }
            }


        }

        @Override
        public void onBitmapFailed (Drawable arg0)
        {
            Toast.makeText(context, "Download fail!!", Toast.LENGTH_SHORT).show();
        }
    }

}

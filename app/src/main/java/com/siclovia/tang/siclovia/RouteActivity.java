package com.siclovia.tang.siclovia;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.graphics.Color;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback, ListView.OnItemClickListener,ActionSheet.ActionSheetListener {
    public DrawerLayout drawMenu;
    private SupportMapFragment mapFragment;
    private ListView menuList;
    static final int GET_FROM_CAMERA = 1;
    static final int GET_FROM_FILE = 2;
    private int share_selected=0;
    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancle) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (index ==0)
        {
            callCameraIntent();

        }
        else if(index ==1)
        {
            callGalleryIntent();
        }

    }



    public class Markers {
        @SerializedName("markers")
        public List<Marker> markers;

        public Markers() { markers = new ArrayList<>(); }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.siclovia.tang.siclovia",         PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                Toast.makeText(getApplicationContext(),sign,         Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        FacebookSdk.sdkInitialize(getApplicationContext());

        //建立new Fragment for google map
        mapFragment = SupportMapFragment.newInstance();
        //設定toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //建立選單
        drawMenu = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawMenu, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        drawMenu.setDrawerListener(toggle);


        menuList = (ListView) findViewById(R.id.nav_lvMenu);
        SimpleAdapter adapter = new SimpleAdapter(this, getData(),
                R.layout.nav_menu_item, new String[]{"img", "title", "info"},
                new int[]{R.id.nav_menu_item_ivLogo, R.id.nav_menu_item_tvName, R.id.nav_menu_item_ivSidebar}) {
            private int[] colors = new int[]{0xFF36AD47, 0xFF7F3F98, 0xFFF2B111, 0xFFE21A22, 0xFF12A1DC, 0xFF253792, 0xFFA8A9AC};

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                int colorPos = position % colors.length;
                ImageView ivSlideBar = (ImageView) view.findViewById(R.id.nav_menu_item_ivSidebar);
                ivSlideBar.setBackgroundColor(colors[colorPos]);
                return view;
            }
        };
        menuList.setAdapter(adapter);
        menuList.setOnItemClickListener(this);


        toggle.syncState();


        //取得google maps
        mapFragment.getMapAsync(this);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("img", R.drawable.menu_icon_sponsor);
        map.put("title", "SPONSORS");

        list.add(map);

        map = new HashMap<>();
        map.put("img", R.drawable.menu_icon_schedule);
        map.put("title", "SCHEDULE");

        list.add(map);

        map = new HashMap<>();
        map.put("img", R.drawable.menu_icon_route);
        map.put("title", "ROUTE");

        list.add(map);

        map = new HashMap<>();
        map.put("img", R.drawable.menu_icon_safety);
        map.put("title", "SAFETY");

        list.add(map);

        map = new HashMap<>();
        map.put("img", R.drawable.menu_icon_donate);
        map.put("title", "DONATE");

        list.add(map);

        map = new HashMap<>();
        map.put("img", R.drawable.menu_icon_gallery);
        map.put("title", "GALLERY");

        list.add(map);

        map = new HashMap<>();
        map.put("img", R.drawable.menu_icon_social);
        map.put("title", "SOCIAL");

        list.add(map);

        return list;
    }

    /**
     * 處理返回鍵事件
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Handle navigation view item clicks here.


        Fragment fragment = null;
        android.support.v4.app.FragmentManager sfm = getSupportFragmentManager();
        if (mapFragment.isAdded()) {
            sfm.beginTransaction().hide(mapFragment).commit();
        }

        switch (position) {
            case 0:
                fragment = new SponsorFragment();
                drawMenu.setBackgroundResource(R.drawable.sponsor_bg);
                break;
            case 1:
                fragment = ScheduleFragment.newInstance();
                drawMenu.setBackgroundResource(R.drawable.event_bg);
                break;
            case 2:
                if (!mapFragment.isAdded()) {
                    sfm.beginTransaction().add(R.id.map_frame, mapFragment).commit();
                } else {
                    sfm.beginTransaction().show(mapFragment).commit();
                }
                drawMenu.setBackgroundColor(Color.parseColor("#FCD214"));
                break;
            case 3:
                fragment = SafetyFragment.newInstance("123","321");
                drawMenu.setBackgroundResource(R.drawable.safety_bg);
                break;
            case 4:
                fragment = DonateFragment.newInstance("123","321");
                drawMenu.setBackgroundResource(R.drawable.donate_bg);
                break;
            case 5:
                fragment = GalleryFragment.newInstance();
                drawMenu.setBackgroundResource(R.drawable.gallery_bg);
                break;
            case 6:
                fragment = FeedFragment.newInstance();
                drawMenu.setBackgroundResource(R.drawable.social_bg);
                break;
            default:

                break;

        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        drawMenu.closeDrawer(GravityCompat.START);
        // TODO: 判斷當前選單避免重複讀取
        menuList.setItemChecked(position, true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route, menu);
        return true;
    }

    /**
     * 設定上方Option被選取時的處理
     *
     * @param item 項目
     * @return 是否成功
     **/
    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_info) {
                //dispatchTakePictureIntent();
                //callCameraIntent();
                shareToFb("Test","http://joinymca.org//siclovia//json//share//1443380640_.jpg");
                return true;
            }
            if (id == R.id.action_parking) {

                return true;
            }
            if (id == R.id.action_option_icon) {
                View menuItemView = findViewById(R.id.action_option_icon);
                showShareMenu(menuItemView);
                return true;
            }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //Set{29.438882, -98.478024}
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(29.438882, -98.478024), 13));
        setMapOverLay(googleMap);
        //MARKER
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://joinymca.org/siclovia/json/markers.php", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Gson gson = new GsonBuilder().create();

                        // Define Response class to correspond to the JSON response returned
                        Markers markersObj = gson.fromJson(res, Markers.class);
                        for (Marker obj : markersObj.markers) {
                            Log.d("Map Marker", "onSuccess: " + obj.location);
                            String[] latlong = obj.location.substring(1, obj.location.length() - 1).split(",");
                            double latitude = Double.parseDouble(latlong[0]);
                            double longitude = Double.parseDouble(latlong[1]);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .title(obj.name));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );


    }

    public void setMapOverLay(GoogleMap googleMap) {
        googleMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.map_overlay))
                .position(new LatLng(29.43968, -98.4799), 2100));
    }
    private void callCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, GET_FROM_CAMERA);
        }
    }
    private void callGalleryIntent(){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, GET_FROM_FILE);
    }
    private void showShareMenu(View view) {
        PopupWindow showPopup = PopupHelper
                .newBasicPopupWindow(getApplicationContext());
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.custom_layout, null);

        View.OnClickListener shareEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_selected = v.getId();
                ActionSheet.createBuilder(v.getContext(), getSupportFragmentManager())

                        .setCancelButtonTitle("Cancel Button")

                        .setOtherButtonTitles("Take Photo", "Choose Photo")
                        .setCancelableOnTouchOutside(true)
                        .setListener(RouteActivity.this).show();
            }
        };
        popupView.findViewById(R.id.option_share_fb).setOnClickListener(shareEvent);
        popupView.findViewById(R.id.option_share_tw).setOnClickListener(shareEvent);
        popupView.findViewById(R.id.option_share_ig).setOnClickListener(shareEvent);
        showPopup.setContentView(popupView);

        showPopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        showPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        showPopup.setAnimationStyle(R.style.Animations_GrowFromTop);
        showPopup.showAsDropDown(view);
    }
    private void shareToFb(String msg,String urlToShare){
/*
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, msg); // NB: has no effect!
        intent.putExtra(Intent.EXTRA_SUBJECT, msg); // NB: has no effect!
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

// See if official Facebook app is found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

// As fallback, launch sharer.php in a browser
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        startActivity(intent);
*/
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();

    }
    private void shareToTw(String msg,String fileUri){
        Toast.makeText(this,"Twitter is not installed on this device",Toast.LENGTH_LONG).show();
      //  String url = "http://www.twitter.com/intent/tweet?url="+urlToShare+"&text="+msg;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
       // intent.setData(Uri.parse(url));
        startActivityForResult(intent,-1);
    }
    private void shareToIg(String msg,String urlToShare){

    }
    //照片上傳 By 圖像資料
    private void uploadPhoto(Bitmap img_bit){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if (img_bit != null) {
            byte[] imagebyte;
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            img_bit.compress(Bitmap.CompressFormat.PNG, 100, bao);
            imagebyte = bao.toByteArray();
            params.put("userfile", new ByteArrayInputStream(imagebyte),    System.currentTimeMillis() + ".png");
        }

        client.post("http://joinymca.org/siclovia/json/photo.php", new FileAsyncHttpResponseHandler(getBaseContext()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }
        });
    }
    //照片上傳 By 照片位置
    private void uploadPhoto(String path){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        File myFile = new File(path);
        try {
            params.put("userfile", myFile);
        } catch(FileNotFoundException e) {}

        client.post("http://joinymca.org/siclovia/json/photo.php", new FileAsyncHttpResponseHandler(getBaseContext()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap = null;
        Uri ImageUri = null;//選取檔案實體位置
        if (requestCode == GET_FROM_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
           imageBitmap = (Bitmap) extras.get("data");

        }
        else if(requestCode == GET_FROM_FILE){
            ImageUri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(ImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            imageBitmap =BitmapFactory.decodeFile(picturePath);
        }
       //  uploadPhoto(imageBitmap);//先上傳到server
       if (imageBitmap!=null)
       {
           Intent intent = new Intent();
           intent.setAction(Intent.ACTION_SEND);
           intent.putExtra(Intent.EXTRA_TEXT, "Test");
           intent.putExtra(Intent.EXTRA_STREAM, ImageUri);

           intent.setPackage("com.twitter.android");
           startActivity(intent);
       }
    }

}

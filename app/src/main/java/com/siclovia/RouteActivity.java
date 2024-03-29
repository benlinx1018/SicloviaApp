package com.siclovia;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import com.components.ActionSheet;
import com.components.PopupHelper;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.siclovia.donate.DonateFragment;
import com.siclovia.gallery.GalleryFragment;
import com.siclovia.safety.SafetyFragment;
import com.siclovia.schedule.ScheduleFragment;
import com.siclovia.social.FeedFragment;
import com.siclovia.sponser.SponsorFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import cz.msebera.android.httpclient.Header;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback, ListView.OnItemClickListener, ActionSheet.ActionSheetListener {
    public DrawerLayout drawMenu;
    private SupportMapFragment mapFragment;
    private ListView menuList;
    static final int GET_FROM_CAMERA = 1;
    static final int GET_FROM_FILE = 2;
    private int share_selected = 0;
    private ShareDialog fbShareDialog;
    private Tracker mTracker;
    private ImageView bikeOption,infoOption, parkingOption;
    private TextView txtNavDate;
    private Typeface tfHelvetica;
    View shareOption;
    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (index == 0) {
            callCameraIntent();
        } else if (index == 1) {
            callGalleryIntent();
        }
    }

    public class Markers {
        @SerializedName("markers")
        public List<Marker> markers;
        public Markers() {
            markers = new ArrayList<>();
        }
    }

    public class Parkings {
        @SerializedName("parking")
        public List<Parking> parkings;
        public Parkings() {
            parkings = new ArrayList<>();
        }
    }

    class OwnIconRendered extends DefaultClusterRenderer<AppClusterItem> {
        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<AppClusterItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(AppClusterItem item, MarkerOptions markerOptions) {
            // Draw a single person.
            markerOptions.icon(BitmapDescriptorFactory.fromResource(item.getIcon()));
            markerOptions.snippet(item.getSnippet());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<AppClusterItem> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            for (AppClusterItem item : cluster.getItems()) {
                markerOptions.snippet(item.getSnippet());
                markerOptions.title(item.getTitle());
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon_reclovia));
                break;
            }
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        //初始化FB SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbShareDialog = new ShareDialog(this);

        //建立new Fragment for google map
        mapFragment = SupportMapFragment.newInstance();
        //設定toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bikeOption = (ImageView) toolbar.findViewById(R.id.appbar_ivBike);

        bikeOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBikeMenu(v);
            }
        });
        parkingOption = (ImageView) toolbar.findViewById(R.id.appbar_ivPark);
        parkingOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showParkingMenu(v);
            }
        });

        infoOption = (ImageView) toolbar.findViewById(R.id.appbar_ivInfo);
        infoOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoMenu(v);
            }
        });

        drawMenu = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawMenu, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawMenu.setDrawerListener(toggle);
        //側邊選單
        txtNavDate =  (TextView)findViewById(R.id.nav_txtActDate);
        tfHelvetica = Typeface.createFromAsset(getAssets(), "fonts/helvetica.ttf");
        menuList = (ListView) findViewById(R.id.nav_lvMenu);
        SimpleAdapter adapter = new SimpleAdapter(this, getData(),
                R.layout.nav_menu_item, new String[]{"img", "title", "info"},
                new int[]{R.id.nav_menu_item_ivLogo, R.id.nav_menu_item_tvName, R.id.nav_menu_item_ivSidebar}) {
            private int[] colors = new int[]{0xFF36AD47, 0xFF7F3F98, 0xFFF2B111, 0xFFE21A22, 0xFF12A1DC, 0xFF253792, 0xFFA8A9AC};

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                int colorPos = position % colors.length;
                TextView tvfunName = (TextView) view.findViewById(R.id.nav_menu_item_tvName);
                tvfunName.setTypeface(tfHelvetica);
                ImageView ivSlideBar = (ImageView) view.findViewById(R.id.nav_menu_item_ivSidebar);
                ivSlideBar.setBackgroundColor(colors[colorPos]);
                return view;
            }
        };
        menuList.setAdapter(adapter);
        menuList.setOnItemClickListener(this);

        toggle.syncState();
        //Get GA
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        //add GA Tracker event
        mTracker = analytics.newTracker(getString(R.string.ga_trackingId));
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("siclovia")
                .setAction("Start")
                .build());
        //取得google maps
        mapFragment.getMapAsync(this);
        getSupportFragmentManager().beginTransaction().add(R.id.map_frame, mapFragment).commit();
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
        android.support.v4.app.FragmentManager sfm = getSupportFragmentManager();
        if (mapFragment.isAdded()) {
            sfm.beginTransaction().hide(mapFragment).commit();
        }

        bikeOption.setVisibility(position == 2 ? View.VISIBLE : View.INVISIBLE);
        infoOption.setVisibility(position == 2 ? View.VISIBLE : View.INVISIBLE);
        parkingOption.setVisibility(position == 2 ? View.VISIBLE : View.INVISIBLE);

        shareOption = findViewById(R.id.action_option_icon);
        shareOption.setVisibility(position!=5? View.VISIBLE : View.INVISIBLE);

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new SponsorFragment();
                drawMenu.setBackgroundResource(R.drawable.sponsor_bg);
                break;
            case 1:
                fragment = ScheduleFragment.newInstance(presetObj.date);
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
                fragment = SafetyFragment.newInstance();
                drawMenu.setBackgroundResource(R.drawable.safety_bg);
                break;
            case 4:
                fragment = DonateFragment.newInstance();
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

        int id = item.getItemId();

        if (id == R.id.action_option_icon) {
            View menuItemView = findViewById(R.id.action_option_icon);
            showShareMenu(menuItemView);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        setMapOverLay(googleMap);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://joinymca.org/siclovia/json/markers.php", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        //MARKER
                        ClusterManager<AppClusterItem> mClusterManager = new ClusterManager<>(RouteActivity.this, googleMap);
                        mClusterManager.setRenderer(new OwnIconRendered(RouteActivity.this, googleMap, mClusterManager));
                        //Distinct
                        mClusterManager.setAlgorithm(new DistinctAlgorithm());
                        googleMap.setOnCameraChangeListener(mClusterManager);
                        googleMap.setOnMarkerClickListener(mClusterManager);
                        googleMap.setOnInfoWindowClickListener(mClusterManager);

                        Gson gson = new GsonBuilder().create();

                        // Define Response class to correspond to the JSON response returned
                        Markers markersObj = gson.fromJson(res, Markers.class);
                        for (Marker obj : markersObj.markers) {
                            Log.d("Map Marker", "onSuccess: " + obj.location);
                            String[] latlong = obj.location.substring(1, obj.location.length() - 1).split(",");


                            double latitude = Double.parseDouble(latlong[0]);
                            double longitude = Double.parseDouble(latlong[1]);

                            /*
                            //1 Open Intersection           Green point
                            //3 Start & Finish Line         STOP Icon
                            //4 Water, Restroom, First Aid  R -> 3Icon
                            //8 Parking                     P point
                            //9 Closed Intersection         Red point
                            */
                            switch (obj.type) {
                                case 1:
                                    googleMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon_greenpoint))
                                            .title(obj.name)
                                            .snippet(obj.subTitle)
                                            .position(new LatLng(latitude, longitude)));
                                    //mClusterManager.addItem(new AppClusterItem(latitude, longitude, R.drawable.map_icon_greenpoint, obj.name, obj.subTitle));
                                    break;
                                case 3:
                                    googleMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon_stoppoint))
                                            .title(obj.name)
                                            .snippet(obj.subTitle)
                                            .position(new LatLng(latitude, longitude)));
                                    //mClusterManager.addItem(new AppClusterItem(latitude, longitude, R.drawable.map_icon_stoppoint, obj.name, obj.subTitle));
                                    break;
                                case 4:
                                    //mClusterManager.addItem(new AppClusterItem(latitude, longitude, R.drawable.map_icon_reclovia, obj.name, obj.subTitle));

                                    mClusterManager.addItem(new AppClusterItem(latitude, longitude + 0.0005, R.drawable.map_icon_water, obj.name, obj.subTitle));
                                    mClusterManager.addItem(new AppClusterItem(latitude, longitude, R.drawable.map_icon_hell, obj.name, obj.subTitle));
                                    mClusterManager.addItem(new AppClusterItem(latitude, longitude - 0.0005, R.drawable.map_icon_restroom, obj.name, obj.subTitle));

                                    break;
                                case 8:
                                    googleMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon_parking))
                                            .title(obj.name)
                                            .snippet(obj.subTitle)
                                            .position(new LatLng(latitude, longitude)));
                                    //mClusterManager.addItem(new AppClusterItem(latitude, longitude, R.drawable.map_icon_parking, obj.name, obj.subTitle));
                                    break;
                                case 9:
                                    googleMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon_redpoint))
                                            .title(obj.name)
                                            .snippet(obj.subTitle)
                                            .position(new LatLng(latitude, longitude)));
                                    //mClusterManager.addItem(new AppClusterItem(latitude, longitude, R.drawable.map_icon_redpoint, obj.name, obj.subTitle));
                                    break;
                                default:
                                    break;

                            }


                        }
                        mClusterManager.cluster();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );
        AsyncHttpClient clientforbike = new AsyncHttpClient();
        clientforbike.get("http://joinymca.org/siclovia/json/bike.php", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Gson gson = new GsonBuilder().create();
                        Parkings parkingsObj = gson.fromJson(res, Parkings.class);

                        for (Parking obj : parkingsObj.parkings) {
                            String[] latlong = obj.location.substring(1, obj.location.length() - 1).split(",");
                            double latitude = Double.parseDouble(latlong[0]);
                            double longitude = Double.parseDouble(latlong[1]);
                            googleMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon_bikeparking))
                                    .title(obj.name)
                                    .snippet(obj.subTitle)
                                    .position(new LatLng(latitude, longitude)));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );
    }

    private PresetData presetObj=null;
    public void setMapOverLay(final GoogleMap googleMap) {
        if(presetObj==null) {
            AsyncHttpClient Syncmap = new AsyncHttpClient();
            Syncmap.get("http://joinymca.org/siclovia/json/preset.php", new TextHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String res) {
                            Gson gson = new GsonBuilder().create();
                            presetObj = gson.fromJson(res, PresetData.class);


                            txtNavDate.setText(presetObj.date);

                            String[] load_latlong = presetObj.android_map.load_center.split(",");
                            double load_latitude = Double.parseDouble(load_latlong[0]);
                            double load_longitude = Double.parseDouble(load_latlong[1]);
                            String[] latlong = presetObj.android_map.map_center.split(",");
                            final double latitude = Double.parseDouble(latlong[0]);
                            final double longitude = Double.parseDouble(latlong[1]);

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(load_latitude, load_longitude), 14));

                            final float with = Float.parseFloat(presetObj.android_map.width);
//                            googleMap.addGroundOverlay(new GroundOverlayOptions()
//                                    .bearing(Float.parseFloat(presetObj.android_map.rotate))
//                                    .image(BitmapDescriptorFactory.fromResource(R.drawable.map_overlay))
//                                    .position(new LatLng(latitude, longitude), with));

                            Picasso.with(getApplicationContext())
                                    .load("http://joinymca.org/siclovia/images/"+presetObj.map_image)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                            //Set it in the ImageView
                                            googleMap.addGroundOverlay(new GroundOverlayOptions()
                                                    .bearing(Float.parseFloat(presetObj.android_map.rotate))
                                                    .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                                                    .position(new LatLng(latitude, longitude), with));
                                        }
                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {

                                        }
                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }

                                    });

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        }
                    }
            );
        }else{
            String[] load_latlong = presetObj.android_map.load_center.substring(1, presetObj.android_map.map_center.length() - 1).split(",");
            double load_latitude = Double.parseDouble(load_latlong[0]);
            double load_longitude = Double.parseDouble(load_latlong[1]);
            String[] latlong = presetObj.android_map.map_center.substring(1, presetObj.android_map.map_center.length() - 1).split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);


            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(load_latitude, load_longitude), 14));
            googleMap.addGroundOverlay(new GroundOverlayOptions()
                    .bearing(Float.parseFloat(presetObj.android_map.rotate))
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.map_overlay))
                    .position(new LatLng(latitude, longitude), Float.parseFloat(presetObj.android_map.width.substring(0, presetObj.android_map.width.length() - 2))));
        }
    }

    private void callCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, GET_FROM_CAMERA);
        }
    }

    private void callGalleryIntent() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, GET_FROM_FILE);
    }

    private Parkings bikesObj=null;
    private void showBikeMenu(View view) {
        final PopupWindow showPopup = PopupHelper
                .newBasicPopupWindow(getApplicationContext());
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.map_bar_bike, null);
        showPopup.setContentView(popupView);
        if(bikesObj==null) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://www.joinymca.org/siclovia/json/bike.php", new TextHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String res) {
                            Gson gson = new GsonBuilder().create();
                             bikesObj = gson.fromJson(res, Parkings.class);
                            String tmpName = "";
                            for (Parking obj : bikesObj.parkings) {
                                tmpName += obj.name + '\n';
                            }
                            ((TextView) showPopup.getContentView().findViewById(R.id.map_bike_info_name)).setText(tmpName);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        }
                    }
            );
        }else{
            String tmpName = "";
            for (Parking obj : bikesObj.parkings) {
                tmpName += obj.name + '\n';
            }
            ((TextView) showPopup.getContentView().findViewById(R.id.map_bike_info_name)).setText(tmpName);
        }
        showPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        showPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        showPopup.setAnimationStyle(R.style.Animations_GrowFromTop);
        showPopup.showAsDropDown(view);
    }
    private Parkings parkingsObj=null;
    private void showParkingMenu(View view) {
        final PopupWindow showPopup = PopupHelper
                .newBasicPopupWindow(getApplicationContext());
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.map_bar_parking, null);
        showPopup.setContentView(popupView);
        if(parkingsObj==null) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://joinymca.org/siclovia/json/parking.php", new TextHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String res) {
                            Gson gson = new GsonBuilder().create();
                            parkingsObj = gson.fromJson(res, Parkings.class);
                            String tmpName = "";
                            String tmpRoad = "";
                            for (Parking obj : parkingsObj.parkings) {
                                tmpName += obj.name + '\n';
                                tmpRoad += obj.subTitle + '\n';
                            }
                            ((TextView) showPopup.getContentView().findViewById(R.id.map_bar_parking_info_name)).setText(tmpName);
                            ((TextView) showPopup.getContentView().findViewById(R.id.map_bar_parking_info_road)).setText(tmpRoad);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        }
                    }
            );
        }else{
            String tmpName = "";
            String tmpRoad = "";
            for (Parking obj : parkingsObj.parkings) {
                tmpName += obj.name + '\n';
                tmpRoad += obj.subTitle + '\n';
            }
            ((TextView) showPopup.getContentView().findViewById(R.id.map_bar_parking_info_name)).setText(tmpName);
            ((TextView) showPopup.getContentView().findViewById(R.id.map_bar_parking_info_road)).setText(tmpRoad);
        }
        showPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        showPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        showPopup.setAnimationStyle(R.style.Animations_GrowFromTop);
        showPopup.showAsDropDown(view);
    }

    private void showInfoMenu(View view) {
        PopupWindow showPopup = PopupHelper
                .newBasicPopupWindow(getApplicationContext());
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.map_info, null);
        showPopup.setContentView(popupView);
        showPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        showPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        showPopup.setAnimationStyle(R.style.Animations_GrowFromTop);
        showPopup.showAsDropDown(view);
    }
    private void showShareMenu(View view) {
        PopupWindow showPopup = PopupHelper
                .newBasicPopupWindow(getApplicationContext());
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.custom_layout, null);

        View.OnClickListener shareEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_selected = v.getId();
                showActionSheet(v);
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
    public void showActionSheet(View v){
        ActionSheet.createBuilder(v.getContext(), getSupportFragmentManager())
                .setCancelButtonTitle("Cancel Button")
                .setOtherButtonTitles("Take Photo", "Choose Photo")
                .setCancelableOnTouchOutside(true)
                .setListener(RouteActivity.this).setListener(RouteActivity.this).show();
    }
    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancle) {

    }

    private void shareToFb(Uri link) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Join me at Siclovia")
                    .setContentDescription(
                            "I am at the Siclovia on Broadway.  Come join us!")
                    .setContentUrl(link)
                    .build();

            fbShareDialog.show(linkContent);
        }
    }

    private void shareToTw(Uri filepath) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, "Join me at Siclovia on Broadway. #Siclovia");
        // Set the MIME type
        share.setType("image/*");
        share.setPackage("com.twitter.android");


        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, filepath);
        PackageManager packManager = getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(share, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                share.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(share);
        } else {
            Toast.makeText(RouteActivity.this, "twitter App is not installed", Toast.LENGTH_LONG).show();
        }
    }

    private void shareToIg(Uri filepath) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, "share");
        share.setType("image/*");
        share.setPackage("com.instagram.android");
        share.putExtra(Intent.EXTRA_STREAM, filepath);
        PackageManager packManager = getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(share, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.instagram.android")) {
                share.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(share);
        } else {
            Toast.makeText(RouteActivity.this, "instagram App is not installed", Toast.LENGTH_LONG).show();
        }
    }
    //照片上傳 By 圖像資料
    private void uploadPhoto(String imgPath, final Uri imgUri, Bitmap img_bit) {

        try {
            File file = new File(imgPath);

            if (file.exists()) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("userfile", file);

                client.post("http://joinymca.org/siclovia/json/photo.php", params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (share_selected == R.id.option_share_fb)
                            shareToFb(Uri.parse(responseString));
                        else if (share_selected == R.id.option_share_tw)
                            shareToTw(imgUri);
                        else if (share_selected == R.id.option_share_ig)
                            shareToIg(imgUri);
                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ProgressDialog dialog = ProgressDialog.show(this,
                "", "sharing...", true);
        Bitmap imageBitmap = null;
        Uri imgURi = null;//選取檔案實體位置
        String imgPath = null;
        boolean isOK = false;
        if (requestCode == GET_FROM_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            File file = new File(Environment.getExternalStorageDirectory() + "/images");
            boolean result =false;
            if (!file.isDirectory()) {
                result = file.mkdir();
            }
            if(result) {
                File imgFile = new File(file, "img_" + System.currentTimeMillis() + ".jpg");
                imgPath = imgFile.getAbsolutePath();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(imgFile);


                    if (imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos))
                        isOK = true;
                        //Toast.makeText(getApplicationContext(), "Image saved.", Toast.LENGTH_SHORT).show();
                    else
                        isOK = false;
                        //Toast.makeText(getApplicationContext(), "Image not save.", Toast.LENGTH_SHORT).show();



                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                imgURi = Uri.fromFile(imgFile);
            }
        } else if (requestCode == GET_FROM_FILE && null != data) {
            isOK = true;
            imgURi = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imgURi, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgPath = cursor.getString(columnIndex);
            cursor.close();
            imageBitmap = BitmapFactory.decodeFile(imgPath);
        }
        if (isOK) {

            uploadPhoto(imgPath, imgURi, imageBitmap);//先上傳到server

        }
        dialog.hide();
    }
    public void setShare_selected(int selected){
        this.share_selected=selected;
    }
}

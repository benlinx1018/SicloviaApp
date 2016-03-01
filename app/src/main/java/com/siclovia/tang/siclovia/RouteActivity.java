package com.siclovia.tang.siclovia;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RouteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {
    public DrawerLayout drawMenu;
    private SupportMapFragment mapFragment;

    public class Markers {
        @SerializedName("markers")
        public List<Marker> markers;

        public Markers() { markers = new ArrayList<>(); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        //建立new Fragment for google map
        mapFragment = SupportMapFragment.newInstance();
        //設定toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //建立選單
        drawMenu = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawMenu,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //toggle.setDrawerIndicatorEnabled(false);
        //toggle.setHomeAsUpIndicator(R.drawable.siclovia);

        drawMenu.setDrawerListener(toggle);



        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //取得google maps
        mapFragment.getMapAsync(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route, menu);
        return true;
    }

    /**
     * 設定上方Option被選取時的處理
     * @param item 項目
     * @return 是否成功
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 設定側邊選單被選取時的事件處理
     * @param item 項目
     * @return 是否成功
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        Fragment fragment = null;
        android.support.v4.app.FragmentManager sfm = getSupportFragmentManager();
        if (mapFragment.isAdded())
        {
            sfm.beginTransaction().hide(mapFragment).commit();
        }

        switch (id){
            case R.id.nav_sponsers:
                fragment = new Sponsors();
                break;
            case R.id.nav_schedule:
                fragment = Schedule.newInstance(1);
                break;
            case R.id.nav_route:
                if (!mapFragment.isAdded()) {
                    sfm.beginTransaction().add(R.id.map_frame, mapFragment).commit();
                }
                else
                {
                    sfm.beginTransaction().show(mapFragment).commit();
                }
                break;
            default:

                break;

        }
        if(fragment!=null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        drawMenu.closeDrawer(GravityCompat.START);
        return true;
    }
    //設定地圖相關(加上標記)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //Set{29.438882, -98.478024}
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(29.438882, -98.478024), 13));

        //MARKER
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://joinymca.org/siclovia/json/markers.php", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Gson gson = new GsonBuilder().create();

                        // Define Response class to correspond to the JSON response returned
                        Markers markersObj =  gson.fromJson(res,Markers.class);
                        for(Marker obj : markersObj.markers){
                            Log.d("Map Marker", "onSuccess: " + obj.location);
                            //{123,23}
                            String[] latlong =  obj.location.substring(1,obj.location.length()-1).split(",");
                            double latitude = Double.parseDouble(latlong[0]);
                            double longitude = Double.parseDouble(latlong[1]);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude,longitude))
                                    .title(obj.name));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );
        googleMap.addMarker(new MarkerOptions().position(new LatLng(29.435286,-98.47961)));
    }


}

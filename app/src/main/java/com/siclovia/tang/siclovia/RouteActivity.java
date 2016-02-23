package com.siclovia.tang.siclovia;

import android.app.Fragment;
import android.app.FragmentManager;

import android.graphics.Color;

import android.os.Bundle;


import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback, ListView.OnItemClickListener {
    public DrawerLayout drawMenu;
    private SupportMapFragment mapFragment;
    private ListView menuList;

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
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("img", R.drawable.menu_icon_sponsor);
        map.put("title", "SPONSORS");

        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.menu_icon_sponsor);
        map.put("title", "SCHEDULE");

        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.menu_icon_sponsor);
        map.put("title", "ROUTE");

        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.menu_icon_sponsor);
        map.put("title", "SAFETY");

        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.menu_icon_sponsor);
        map.put("title", "DONATE");

        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.menu_icon_sponsor);
        map.put("title", "GALLERY");

        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.menu_icon_sponsor);
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
                fragment = GalleryFragment.newInstance(3);
                drawMenu.setBackgroundResource(R.drawable.donate_bg);
                break;
            case 6:
                fragment = FeedFragment.newInstance("123","321");
                drawMenu.setBackgroundResource(R.drawable.donate_bg);
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


    //設定地圖相關(加上標記)
    @Override
    public void onMapReady(GoogleMap googleMap) {

    }


}

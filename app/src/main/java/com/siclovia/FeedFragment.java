package com.siclovia;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;



public class FeedFragment extends Fragment implements TabHost.OnTabChangeListener {


    private TabHost mTabHost;
    private View mRoot;
    public FeedFragment() {

    }
    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_feed, container, false);
        mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
        //Tab FB like Button
        ImageButton imgBtnFB = (ImageButton) mTabHost.findViewById(R.id.tab_btn_fb);
        imgBtnFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //http://stackoverflow.com/questions/4810803/open-facebook-page-from-android-app/24547437#24547437
                String url = "https://www.facebook.com/Siclovia";
                Uri uri = Uri.parse(url);
                try {
                    ApplicationInfo applicationInfo =     v.getContext().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                    if (applicationInfo.enabled) {
                        // http://stackoverflow.com/a/24547437/1048340
                        uri = Uri.parse("fb://facewebmodal/f?href=" + url);
                    }
                } catch (Exception e) {

                }
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));



            }
        });
        //Tab TW Follow Button
        ImageButton imgBtnTW = (ImageButton) mTabHost.findViewById(R.id.tab_btn_tw);
        imgBtnTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://twitter.com/Siclovia";
                Uri uri = Uri.parse(url);
                try {
                    ApplicationInfo applicationInfo =     v.getContext().getPackageManager().getApplicationInfo("com.twitter.android", 0);
                    if (applicationInfo.enabled) {

                        uri = Uri.parse("twitter://user?user_id=348660858");
                    }
                } catch (Exception e) {
                }
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));

            }
        });
        mTabHost.setOnTabChangedListener(this);
        setupTabs();

        return mRoot;
    }

    private void setupTabs() {
        mTabHost.setup(); // you must call this before adding your tabs!
        mTabHost.addTab(newTab("tw", R.drawable.tab_tw, R.id.webView1));
        mTabHost.addTab(newTab("fb", R.drawable.tab_fb, R.id.webView2));
    }
    private TabHost.TabSpec newTab(String tag, int ImgRes, int tabContentId) {
        View indicator = LayoutInflater.from(getActivity()).inflate(
                R.layout.tab,
                (ViewGroup) mRoot.findViewById(android.R.id.tabs), false);


        ((ImageView) indicator.findViewById(R.id.tab_ig)).setImageResource(ImgRes);
        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setIndicator(indicator);
        tabSpec.setContent(tabContentId);
        return tabSpec;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setRetainInstance(true);優化用
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onTabChanged(String tabId) {
        String url ="";
        //chang WebView Uri
        if(tabId.equals("tw"))
        {
            url = "http://joinymca.org/siclovia/twfeed.php";

        }
        else if(tabId.equals("fb"))
        {
            url = "http://joinymca.org/siclovia/fbfeed.php";
        }
        ((WebView)mTabHost.getCurrentView()).getSettings().setUseWideViewPort(true);
        ((WebView)mTabHost.getCurrentView()).getSettings().setLoadWithOverviewMode(true);
        ((WebView)mTabHost.getCurrentView()).loadUrl(url);
    }
}

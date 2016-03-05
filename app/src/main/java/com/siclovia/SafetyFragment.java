package com.siclovia;

import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.Utilities;



public class SafetyFragment extends Fragment {

    public static SafetyFragment newInstance() {
        SafetyFragment fragment = new SafetyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_safety, container, false);
        View.OnTouchListener onPressEffect = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        view.getDrawable().clearColorFilter();
                        break;
                    }
                }
                //刷新UI
                view.invalidate();
                //回傳False 讓onclick事件繼續觸發
                return false;
            }
        };
        View.OnClickListener  onClick  = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.openActionView(v.getContext(), Uri.parse("http://www.ymcasatx.org/ymca-of-greater-san-antonio/siclovia-pledge"));
            }
        };
        rootView.findViewById(R.id.safety_igBtnTitle).setOnTouchListener(onPressEffect);
        rootView.findViewById(R.id.safety_igBtnContent).setOnTouchListener(onPressEffect);
        rootView.findViewById(R.id.safety_igBtnArrow).setOnTouchListener(onPressEffect);
        rootView.findViewById(R.id.safety_igBtnTitle).setOnClickListener(onClick);
        rootView.findViewById(R.id.safety_igBtnContent).setOnClickListener(onClick);
        rootView.findViewById(R.id.safety_igBtnArrow).setOnClickListener(onClick);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}

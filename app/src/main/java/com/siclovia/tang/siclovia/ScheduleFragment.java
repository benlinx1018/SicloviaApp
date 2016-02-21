package com.siclovia.tang.siclovia;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ScheduleFragment extends Fragment {
    EventAdapter eventAdapter = new EventAdapter();
    public List<Event> events = new ArrayList<>();


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://joinymca.org/siclovia/json/events.php", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Gson gson = new GsonBuilder().create();
                        // Define Response class to correspond to the JSON response returned
                        EventsObj eventsObj = gson.fromJson(res, EventsObj.class);
                        events.addAll(eventsObj.events);
                        eventAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);


        SwipeMenuListView listView = (SwipeMenuListView) view.findViewById(R.id.schedult_list);
        listView.setAdapter(eventAdapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {


            public void create(SwipeMenu menu) {

                SwipeMenuItem directions = new SwipeMenuItem(
                        getActivity().getApplicationContext());

                directions.setBackground(new ColorDrawable(Color.argb(255,191,16,45)));
                directions.setWidth(dp2px(90));
                directions.setTitle("Directions");
                directions.setTitleSize(18);
                directions.setTitleColor(Color.WHITE);

                menu.addMenuItem(directions);

                SwipeMenuItem reminder = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                reminder.setBackground(new ColorDrawable(Color.argb(255,234,174,40)));
                reminder.setWidth(dp2px(90));
                reminder.setTitle("Reminder");
                reminder.setTitleSize(18);
                reminder.setTitleColor(Color.WHITE);
                menu.addMenuItem(reminder);


            }
        };
        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (position)
                {
                    //Direction
                    case 0:
                        directionTo(events.get(position).lat,events.get(position).lon);
                        break;
                    //Reminder
                    case 1:

                        break;
                }
                return false;
            }
        });
        return view;
    }
    public void directionTo(String lat,String lon){

        Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lon);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        }
        else
        {
            gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lon);
            Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            startActivity(intent);
        }
    }
    public void addToCalender(){
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, "test title");
        //intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
        //        startDateMillis);
        //intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
        //        endDateMillis);
        intent.putExtra(CalendarContract.Events.ALL_DAY, false);// periodicity
        intent.putExtra(CalendarContract.Events.DESCRIPTION, "test des");
        //TODO: redminder to schedule
    }

    class EventsObj {

        public List<Event> events;

        public EventsObj() {
            events = new ArrayList<>();
        }
    }
    class EventAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Event getItem(int position) {
            return events.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity().getApplicationContext(),
                        R.layout.fragment_event, null);
            }
            ViewHolder holder =  new ViewHolder(convertView);
            Event item = getItem(position);

            holder.txtTime.setText(item.time);
            holder.txtAmpm.setText(item.ampm);
            holder.txtTitle.setText(item.title);
            holder.txtSubTitle.setText(item.subTitle);


            return convertView;
        }

        class ViewHolder {
            public final TextView txtTime, txtAmpm, txtTitle, txtSubTitle;

            public ViewHolder(View view) {
                txtAmpm = (TextView) view.findViewById(R.id.event_txtAmpm);
                txtTime = (TextView) view.findViewById(R.id.event_txtTime);
                txtTitle = (TextView) view.findViewById(R.id.event_txtTitle);
                txtSubTitle = (TextView) view.findViewById(R.id.event_txtSubTitle);

            }
        }
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}

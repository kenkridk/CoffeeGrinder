package com.example.kenneth.coffeegrinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CoffeeInquiry extends FragmentActivity {

    ViewPager tab;
    TabPagerAdapter TabAdapter;
    private int pos = 1;
    private int timeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_inquiry);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        playNotificationSound();

        timeout = Integer.parseInt(getIntent().getExtras().getString("timeout"));

        TabAdapter = new TabPagerAdapter(getSupportFragmentManager());
        tab = (ViewPager)findViewById(R.id.pager);
        tab.setAdapter(TabAdapter);
        tab.setCurrentItem(1);
        tab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: //User swiped yes
                        pos = position;
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url ="http://www.nikander-arts.com:8080/respond";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    TextView serverResponse = (TextView)findViewById(R.id.server_response);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            findViewById(R.id.server_progress_bar).setVisibility(View.GONE);
                                        }
                                    });

                                    //JSONObject myJson = new JSONObject(response);
                                    serverResponse.setText(response);
                                    playNotificationSound();
                                    finishIn(getResources().getInteger(R.integer.yes_wait));
                                }
                            }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ((TextView)findViewById(R.id.server_response)).setText(getResources().getString(R.string.server_error));
                                playNotificationSound();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.server_progress_bar).setVisibility(View.GONE);
                                    }
                                });
                                finishIn(getResources().getInteger(R.integer.yes_wait));
                            }
                        }){
                            @Override
                            public Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                                String deviceID = prefs.getString(GCMService.PROPERTY_REG_ID, "DefaultDeviceID");
                                Log.i("CoffeeApp", "Requesting subscription on " + getIntent().getExtras().getString("machine") + " with device ID " + deviceID);
                                params.put("android", deviceID);
                                params.put("machine", getIntent().getExtras().getString("machine"));
                                params.put("answer", "true");
                                params.put("time", getIntent().getExtras().getString("time"));
                                return params;
                            }
                        };

                        queue.add(stringRequest);
                        break;
                    case 1:
                        tab.setCurrentItem(pos);
                        break;
                    case 2:
                        pos = position;
                        //The user doesn't want coffee, for some reason, so finish the activity.
                        finishIn(getResources().getInteger(R.integer.no_wait));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void finishIn(int delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },delayMillis);
    }

    public int getTimeout(){
        return timeout;
    }

    public void playNotificationSound(){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return " " + position;
        }
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class DemoObjectFragment extends Fragment {
        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.activity_coffee_inquiry, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    Integer.toString(args.getInt(ARG_OBJECT)));
            return rootView;
        }
    }

    public int getPosition(){
        return pos;
    }
}
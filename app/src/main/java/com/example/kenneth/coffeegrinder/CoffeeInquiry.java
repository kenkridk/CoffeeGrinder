package com.example.kenneth.coffeegrinder;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class CoffeeInquiry extends FragmentActivity {

    ViewPager tab;
    TabPagerAdapter TabAdapter;
    private int pos = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_inquiry);
        playNotificationSound();


        TabAdapter = new TabPagerAdapter(getSupportFragmentManager());
        tab = (ViewPager)findViewById(R.id.pager);
        tab.setAdapter(TabAdapter);
        tab.setCurrentItem(1);
        tab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: //User swiped yes
                        pos = position;
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
                        WifiInfo info = manager.getConnectionInfo();
                        String address = info.getMacAddress();
                        String url ="http://www.nikander-arts.com:8080/requestAccess?id="+address;

                        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET,url,new JSONObject(),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    TextView serverResponse = (TextView)findViewById(R.id.server_response);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            findViewById(R.id.server_progress_bar).setVisibility(View.GONE);
                                        }
                                    });

                                    try{
                                        //JSONObject myJson = new JSONObject(response);
                                        serverResponse.setText(response.getString("description"));
                                        playNotificationSound();

                                        new Timer().schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        },getResources().getInteger(R.integer.yes_wait));

                                    }catch(JSONException e){
                                        Log.e("CoffeeApp_json",e.getMessage());
                                    }
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
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                },getResources().getInteger(R.integer.yes_wait));
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("User-Agent", "Android Phone");
                                params.put("Accept", "application/json");

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

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, getResources().getInteger(R.integer.no_wait));
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
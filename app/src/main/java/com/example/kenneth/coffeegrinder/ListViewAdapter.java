package com.example.kenneth.coffeegrinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kenneth on 22-04-2015.
 */
public class ListViewAdapter extends ArrayAdapter<ListViewClass>{

    private TranslateAnimation animation;
    private ArrayList<Boolean> isCellsCollapsedList;
    private ListViewClassDataSource datasource;
    private int widthToAnimate;

    public ListViewAdapter(Context context, ArrayList<ListViewClass> list){
        super(context, 0, list);
        isCellsCollapsedList = new ArrayList<Boolean>();

        for(ListViewClass l : list){
            Log.d("ListViewClass id", l.getId() + "");
            isCellsCollapsedList.add(new Boolean(true));
        }

        datasource = new ListViewClassDataSource(context);
    }

    public void animateOpenCell(View v, int position){
        animation = new TranslateAnimation(0, -widthToAnimate, 0, 0);
        animation.setDuration(200);
        animation.setFillAfter(true);
        v.startAnimation(animation);
        isCellsCollapsedList.set(position, false);
    }

    public void animateCloseCell(View v, int position){
        animation = new TranslateAnimation(-widthToAnimate, 0, 0, 0);
        animation.setDuration(200);
        animation.setFillAfter(true);
        v.startAnimation(animation);
        isCellsCollapsedList.set(position, true);
    }

    public void animationDestroy(View v, final ListViewClass lvc){
        animation = new TranslateAnimation(-widthToAnimate, -v.getWidth(), 0 ,0);
        animation.setDuration(200);
        v.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                remove(lvc);
                notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        final ListViewClass lvc = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);

        TextView name = (TextView) convertView.findViewById(R.id.demoTitle);
        name.setText(lvc.getName());

        TextView description = (TextView) convertView.findViewById(R.id.demoDescription);
        description.setText(lvc.getDescription());

        final RelativeLayout leftContainer = (RelativeLayout) convertView.findViewById(R.id.leftContainer);
        final RelativeLayout rightContainer = (RelativeLayout) convertView.findViewById(R.id.rightContainer);

        final Button remove = (Button) convertView.findViewById(R.id.buttonRemove);
        final Button mute = (Button) convertView.findViewById(R.id.buttomMute);

        Log.d("removebutton width", "" + remove.getWidth());
        Log.d("WidthToAnimate", widthToAnimate + "" + remove.getWidth());

        remove.setEnabled(false);
        mute.setEnabled(false);

        rightContainer.setVisibility(View.VISIBLE);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCellsCollapsedList.get(position)) {
                    Log.d("removebutton width", "" + remove.getWidth());
                    widthToAnimate = remove.getWidth() + mute.getWidth();
                    animateOpenCell(leftContainer, position);
                    remove.setEnabled(true);
                    mute.setEnabled(true);
                }else {
                    animateCloseCell(leftContainer, position);
                    remove.setEnabled(false);
                    mute.setEnabled(false);
                }
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first unsubscribe. Send it to server. If deleted from server, remove from listView.
                RequestQueue queue = Volley.newRequestQueue(getContext());

                String name = lvc.getName();
                String[] splittetName = name.split("/");

                final String urlId = splittetName[1];
                String url = "http://" + splittetName[0] + "/unsubscribe";

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            datasource.open();
                            datasource.deleteListViewClass(lvc);
                            isCellsCollapsedList.remove(position);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        datasource.close();

                        animationDestroy(leftContainer, lvc);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Called if something goes wrong in making the request
                        Log.d("Error", "Something went wrong" + error.toString());
                    }
                }) {
                    /**
                     * Override the getParams() method and change it to contain the parameters we
                     * want to pass to the server.
                     */
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        SharedPreferences prefs = getContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                        String deviceId = prefs.getString(GCMService.PROPERTY_REG_ID, "DEFAULT DEVICE ID");
                        params.put("android", deviceId);
                        params.put("machine", urlId);
                        return params;
                    }
                };

                queue.add(request);
            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Mute", "Mute button was pressed");
            }
        });

        return convertView;
    }
}

package dk.au.teamawesome.promulgate.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import dk.au.teamawesome.promulgate.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dk.au.teamawesome.promulgate.activities.MainActivity;
import dk.au.teamawesome.promulgate.containers.ListViewClass;
import dk.au.teamawesome.promulgate.services.GCMService;

public class ListViewAdapter extends ArrayAdapter<ListViewClass>{

    private TranslateAnimation animation;
    private AlphaAnimation alphaAnimation;
    private ArrayList<Boolean> isCellsCollapsedList;
    private ListViewClassDataSource datasource;
    private int widthToAnimate;
    private ArrayList<ListViewClass> list;

    public ListViewAdapter(Context context, ArrayList<ListViewClass> list){
        super(context, 0, list);
        isCellsCollapsedList = new ArrayList<Boolean>();

        for(ListViewClass l : list){
            Log.d("ListViewClass id", l.getId() + "");
            isCellsCollapsedList.add(true);
        }

        datasource = new ListViewClassDataSource(context);
        this.list = list;
    }

    public void refreshList(ArrayList<ListViewClass> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();

    }

    /*public void deleteCellFromListView(int position, View v, ListViewClass lvc){
        isCellsCollapsedList.remove(position);
        animationDestroy(v, lvc);
    }*/

    public void animateOpenCell(final View v) {
        ObjectAnimator oAnimator = ObjectAnimator.ofFloat(v,"translationX",0,-widthToAnimate).setDuration(200);
        oAnimator.start();
    }

    public void animateCloseCell(final View v) {
        ObjectAnimator oAnimator = ObjectAnimator.ofFloat(v,"translationX",-widthToAnimate,0).setDuration(200);
        oAnimator.start();
    }

    public void animatePendingCell(View v) {
        ObjectAnimator oAnimator = ObjectAnimator.ofFloat(v, "translationX", -widthToAnimate, 0).setDuration(200);
        oAnimator.start();
    }
    /*public void animationDestroy(View v, final ListViewClass lvc){
        animation = new TranslateAnimation(-widthToAnimate, -v.getWidth(), 0 ,0);
        animation.setDuration(200);
        v.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                remove(lvc);
                notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }*/



    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        final ListViewClass lvc = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent, false);
            convertView.invalidate();
        }
        Log.d("lars_inflater",convertView.isShown() ? "yes":"no");

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
        image.setImageDrawable(getImage(lvc.getDeviceType()));

        TextView name = (TextView) convertView.findViewById(R.id.demoTitle);
        name.setText(lvc.getName());

        final TextView description = (TextView) convertView.findViewById(R.id.demoDescription);
        description.setText(lvc.getDescription());

        final RelativeLayout leftContainer = (RelativeLayout) convertView.findViewById(R.id.leftContainer);
        final RelativeLayout rightContainer = (RelativeLayout) convertView.findViewById(R.id.rightContainer);

        final ImageView remove = (ImageView) convertView.findViewById(R.id.imageViewRemove);
        final ToggleButton mute = (ToggleButton) convertView.findViewById(R.id.toggleButtonMute);


        Log.i("RemovebuttonLars", remove.getWidth() + mute.getWidth() + "");

        //Set mute according to whether it was muted previously or not
        SharedPreferences prefs = getContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        Set<String> machineIds = prefs.getStringSet("mutedMachines", new HashSet<String>());
        if (machineIds.contains(lvc.getMachineId())) {
            mute.setChecked(false);
            leftContainer.setBackgroundColor(Color.LTGRAY);
        } else mute.setChecked(true);

        remove.setEnabled(false);
        mute.setEnabled(false);

        Log.i("Machine ID", lvc.getMachineId());

        rightContainer.setVisibility(View.VISIBLE);

        leftContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lvc.isCollapsed()) {
                    Log.d("removebutton width", "" + remove.getWidth());
                    widthToAnimate = remove.getWidth() + mute.getWidth();
                    animateOpenCell(leftContainer);
                    lvc.setCollapsed(false);
                    remove.setEnabled(true);
                    mute.setEnabled(true);
                } else {
                    animateCloseCell(leftContainer);
                    lvc.setCollapsed(true);
                    remove.setEnabled(false);
                    mute.setEnabled(false);
                }
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first unsubscribe. Send it to server. If deleted from server, remove from listView.
                animatePendingCell(leftContainer);
                description.setText("Unsubscribing... ");
                RequestQueue queue = Volley.newRequestQueue(getContext());

                String url = lvc.getRoutingServer() + "/unsubscribe";

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                        params.put("machine", lvc.getMachineId());
                        return params;
                    }
                };
                queue.add(request);
            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                Set<String> machineIds = prefs.getStringSet("mutedMachines", new HashSet<String>());

                if (((ToggleButton) v).isChecked()) {
                    machineIds.remove(lvc.getMachineId());
                    leftContainer.setBackgroundColor(Color.parseColor("#82bfb6"));
                }
                if (!((ToggleButton) v).isChecked()) {
                    machineIds.add(lvc.getMachineId());
                    leftContainer.setBackgroundColor(Color.LTGRAY);
                }

                SharedPreferences.Editor editor = prefs.edit();
                editor.putStringSet("mutedMachines", machineIds);
                editor.apply();
            }
        });

        return convertView;
    }

    private Drawable getImage(String deviceType) {
        Drawable image;

        switch (deviceType) {
            case "food":
                image = getContext().getResources().getDrawable(R.mipmap.ic_food);
                break;
            case "misc":
                image = getContext().getResources().getDrawable(R.mipmap.ic_misc);
                break;
            default:
                image = getContext().getResources().getDrawable(R.mipmap.ic_drink);
                break;
        }

        return image;
    }

}

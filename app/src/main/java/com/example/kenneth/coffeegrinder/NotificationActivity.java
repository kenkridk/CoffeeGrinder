package com.example.kenneth.coffeegrinder;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nikander on 17-04-2015.
 */
public class NotificationActivity extends Fragment {


    private int progress = 0;
    private ProgressBar pb;
    private Timer timer;
    private float timerAlpha = 100f;
    private TextView tv;
    private long deadline = 0;
    private CoffeeInquiry activity;
    private int timeOffset;
    private int progressOffset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment, container, false);

        activity = (CoffeeInquiry)getActivity();
        timeOffset = activity.getTimeout();
        progressOffset = timeOffset/1000;
        Log.d("TimeOffset", timeOffset + "");
        startTimer(view);
        return view;
    }


    public void startTimer(View view){
        progress = 0;
        Vibrator v = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
        v.vibrate(500);


        if(timer!=null)
            timer.cancel();

        if(pb==null) {
            pb = (ProgressBar) view.findViewById(R.id.barTimer);
            tv = (TextView) view.findViewById(R.id.timeView);
        }


        deadline = Calendar.getInstance().getTimeInMillis() + timeOffset;

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                progress =(int)(timeOffset-(deadline- Calendar.getInstance().getTimeInMillis()))/progressOffset;
                pb.setProgress(progress);
                if(activity.getPosition()!=1){
                    timer.cancel();
                    return;
                }


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText((int)((progressOffset - (progress / (((float)10/(float)progressOffset)*100)))) + " sec");
                        pb.setAlpha(timerAlpha);
                    }
                });

                if(progress >= 1000) {
                    progress = 0;
                    timer.cancel();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(R.string.time_up);
                            pb.setAlpha(timerAlpha);

                        }
                    });

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getActivity().finish();
                        }
                    },1000);

                }

                if(progress > 800){
                    if(progress %50 < 25){
                        timerAlpha = 0.2f;
                    }else{
                        timerAlpha = 1f;
                    }
                }
            }
        };

        timer = new Timer();
        timer.schedule(t,10,10);
    }

    public void StopTimer(){
        timer.cancel();
    }



}

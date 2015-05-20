package com.example.kenneth.coffeegrinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import java.sql.SQLException;
import java.util.ArrayList;


public class ViewCoffeeMachinesActivity extends ActionBarActivity {

    IntentFilter intentFilter;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_view_coffee_machines);

        intentFilter = new IntentFilter(GcmIntentService.SUBERSCRIBER_LIST_CHANGED);

    }

    @Override
    public void onResume(){
        super.onResume();

        final ListViewClassDataSource datasource = new ListViewClassDataSource(this);
        try {
            datasource.open();

            ArrayList<ListViewClass> list = (ArrayList<ListViewClass>) datasource.getAllListViewClasses();

            final ListViewAdapter adapter = new ListViewAdapter(this, list);

            final ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
            listView.invalidate();

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ListViewClassDataSource datasource = new ListViewClassDataSource(ViewCoffeeMachinesActivity.this);
                    try {
                        datasource.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    switch (intent.getStringExtra("action")) {
                        case "unsubscribe":
                            ArrayList<ListViewClass> list = (ArrayList<ListViewClass>) datasource.getAllListViewClasses();
//                            Log.i("List!", "List goes here:");
//                            for (ListViewClass lvc : list) {
//                                Log.i("LVC", lvc.getName() + " " + lvc.getMachineId());
//                            }
//                            for (int i = 1; i < list.size(); i++) {
//                                Log.i("List items", list.get(i).getMachineId());
//                                Log.i("###### Machine ID", intent.getStringExtra("machineId"));
//                                Log.i("###### List id", list.get(i).getMachineId());
//                                if (list.get(i).getMachineId().equals(intent.getStringExtra("machineId"))) {
//                                    Log.i("HER", "Found it");
//                                    list.remove(i);
//                                }
                                adapter.refreshList(list);
//                                listView.invalidate();
//                            }
                            break;
                        case "subscribe":
                            break;
                    }
                    datasource.close();
                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        datasource.close();


    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}

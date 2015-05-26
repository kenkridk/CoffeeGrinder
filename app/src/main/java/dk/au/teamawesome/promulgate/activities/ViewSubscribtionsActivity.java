package dk.au.teamawesome.promulgate.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;


import dk.au.teamawesome.promulgate.R;

import java.sql.SQLException;
import java.util.ArrayList;

import dk.au.teamawesome.promulgate.services.GcmIntentService;
import dk.au.teamawesome.promulgate.views.ListViewAdapter;
import dk.au.teamawesome.promulgate.containers.ListViewClass;
import dk.au.teamawesome.promulgate.views.ListViewClassDataSource;


public class ViewSubscribtionsActivity extends ActionBarActivity {

    IntentFilter intentFilter;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_view_subscriptions);

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
                    ListViewClassDataSource datasource = new ListViewClassDataSource(ViewSubscribtionsActivity.this);
                    try {
                        datasource.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    switch (intent.getStringExtra("action")) {
                        case "subscribe":
                        case "unsubscribe":
                                ArrayList<ListViewClass> list = (ArrayList<ListViewClass>) datasource.getAllListViewClasses();
                                adapter.refreshList(list);
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

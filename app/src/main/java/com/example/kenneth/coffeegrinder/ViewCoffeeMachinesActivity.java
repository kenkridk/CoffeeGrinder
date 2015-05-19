package com.example.kenneth.coffeegrinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ViewCoffeeMachinesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_coffee_machines);
    }

    @Override
    public void onResume(){
        super.onResume();

        ListViewClassDataSource datasource = new ListViewClassDataSource(this);
        try {
            datasource.open();

            ArrayList<ListViewClass> list = (ArrayList<ListViewClass>) datasource.getAllListViewClasses();
            Log.d("Length of list", list.size() + "");

            ListViewAdapter adapter = new ListViewAdapter(this, list);

            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
            listView.invalidate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        datasource.close();
    }

}

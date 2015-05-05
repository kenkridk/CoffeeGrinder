package com.example.kenneth.coffeegrinder;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

    private ListViewClassDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_coffee_machines);

        datasource = new ListViewClassDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<ListViewClass> list = (ArrayList<ListViewClass>) datasource.getAllListViewClasses();
        ListViewAdapter adapter = new ListViewAdapter(this, list);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        /*
        ArrayList<ListViewClass> list = new ArrayList<ListViewClass>();

        ListViewAdapter adapter = new ListViewAdapter(this, list);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        for(int i = 0; i <6; i++){
            ListViewClass lvc = new ListViewClass("TestName" + i, "TestDescription" + i, i);
            adapter.add(lvc);
        }
        */
    }
}

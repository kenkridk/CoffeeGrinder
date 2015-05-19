package com.example.kenneth.coffeegrinder;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import java.sql.SQLException;
import java.util.ArrayList;


public class ViewCoffeeMachinesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_view_coffee_machines);
    }

    @Override
    public void onResume(){
        super.onResume();

        ListViewClassDataSource datasource = new ListViewClassDataSource(this);
        try {
            datasource.open();

            ArrayList<ListViewClass> list = (ArrayList<ListViewClass>) datasource.getAllListViewClasses();

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

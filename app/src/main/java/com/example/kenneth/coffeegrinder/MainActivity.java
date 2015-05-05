package com.example.kenneth.coffeegrinder;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

    private Intent i;
    private Button viewCoffeeMachines;
    private Button viewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        i = new Intent(MainActivity.this, CoffeeService.class);

        initializeButtons();
        initializeListeners();

        //start service that notifies about new subscribers
        startService(i);
    }

    public void initializeButtons(){
        viewCoffeeMachines = (Button) findViewById(R.id.buttonViewCoffeeMachines);
        viewSettings = (Button) findViewById(R.id.buttonViewSettings);
    }

    public void initializeListeners(){
        viewCoffeeMachines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start new activity showing listed/available coffee machines
                Intent i = new Intent(MainActivity.this, ViewCoffeeMachinesActivity.class);
                startActivity(i);
            }
        });
        viewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start new activity showing settings
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopService(i);
    }
}

package dk.au.teamawesome.promulgate.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ToggleButton;

import dk.au.teamawesome.promulgate.R;


public class SettingsActivity extends ActionBarActivity {

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        ToggleButton muteAllToggle = (ToggleButton) findViewById(R.id.buttonSettingsMuteAll);

        if (!prefs.getString("muteAll", "false").equals("false")) {
            muteAllToggle.setChecked(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void muteAllToggle(View view) {
        SharedPreferences.Editor editor = prefs.edit();

        if (((ToggleButton)view).isChecked()) {
            editor.putString("muteAll", "true");
        } else {
            editor.putString("muteAll", "false");
        }
        editor.apply();
    }
}

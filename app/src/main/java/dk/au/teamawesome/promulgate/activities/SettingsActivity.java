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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import dk.au.teamawesome.promulgate.R;


public class SettingsActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    SharedPreferences prefs;
    TextView ignoreDistanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        final Switch muteAllSwitch = (Switch) findViewById(R.id.settingsSwitchMuteAll);
        if (!prefs.getString("muteAll", "false").equals("false")) {
            muteAllSwitch.setChecked(true);
        }
        muteAllSwitch.setOnCheckedChangeListener(this);

        SeekBar ignoreDistanceSeekbar = (SeekBar) findViewById(R.id.settingsSeekbarIgnoreDistance);

        if (prefs.getString("ignoreDistance", "noIgnore").equals("noIgnore")) {
            ignoreDistanceSeekbar.setProgress(ignoreDistanceSeekbar.getMax());
        } else ignoreDistanceSeekbar.setProgress(Integer.parseInt(prefs.getString("ignoreDistance", "1000")));
        ignoreDistanceSeekbar.setOnSeekBarChangeListener(this);

        ignoreDistanceText = (TextView) findViewById(R.id.settingsIgnoreDistanceNumberText);

        if (ignoreDistanceSeekbar.getProgress() == ignoreDistanceSeekbar.getMax()) {
            ignoreDistanceText.setText("Don't ignore anything");
        } else ignoreDistanceText.setText(ignoreDistanceSeekbar.getProgress() + " meters");

        final TextView updateLocationIntervalTextView = (TextView) findViewById(R.id.settingsTextViewUpdateLocationInterval);
        SeekBar updateLocationIntervalSeekbar = (SeekBar) findViewById(R.id.settingsSeekbarUpdateLocationInterval);

        updateLocationIntervalSeekbar.setProgress(prefs.getInt("updateLocationInterval", 5000)/1000);
        updateLocationIntervalTextView.setText(prefs.getInt("updateLocationInterval", 5000)/1000 + " minutes");

        updateLocationIntervalSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() == 0) {
                    updateLocationIntervalTextView.setText("1 minute");
                } else updateLocationIntervalTextView.setText(seekBar.getProgress() + " minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0) {
                    editor.putInt("updateLocationInterval", 1000);
                } else editor.putInt("updateLocationInterval", seekBar.getProgress()*1000);
                editor.apply();
            }
        });
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

    //OnCheckedChangedListener method
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor editor = prefs.edit();

        if (isChecked) {
            editor.putString("muteAll", "true");
        } else {
            editor.putString("muteAll", "false");
        }
        editor.apply();
    }

    //OnSeekBarChangedListener methods
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getProgress() == seekBar.getMax()) {
            ignoreDistanceText.setText("Don't ignore anything");
        } else if (seekBar.getProgress() == 0) {
            ignoreDistanceText.setText("50 meters");
        } else ignoreDistanceText.setText(progress + " meters");

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        SharedPreferences.Editor editor = prefs.edit();
        if (seekBar.getProgress() == seekBar.getMax()) {
            editor.putString("ignoreDistance", "noIgnore");
        } else editor.putString("ignoreDistance", String.valueOf(seekBar.getProgress() + 50));
        editor.apply();
    }
}

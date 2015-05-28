package dk.au.teamawesome.promulgate.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import dk.au.teamawesome.promulgate.R;
import com.google.android.gms.common.GooglePlayServicesUtil;

import dk.au.teamawesome.promulgate.contentproviders.UpdateLocationReceiver;
import dk.au.teamawesome.promulgate.services.GCMService;

public class MainActivity extends ActionBarActivity {

    public static final boolean DEBUG = true;

    //Tag for log
    private static final String GCM_TAG = "GCM-Test";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Button viewSubscritions;
    private Button viewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        //should hide the actionbar

        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);


        startGPSAlarm();
        initializeButtons();
        initializeListeners();

        //start service that notifies about new subscribers

        if (DEBUG) Log.i(GCM_TAG, "OnCreate");

        /**
         * Register intent filters for the checks for Google Play Services that are performed in
         * the GCMService.checkPlayServices() method.
         */
        IntentFilter filter = new IntentFilter(GCMService.USER_RECOVERABLE_ERROR);
        filter.addAction(GCMService.DEVICE_NOT_SUPPORTED);

        /**
         * Broadcast receiver for the intents registered above. The first case displays a
         * prompt to download Play Services, or enable in the settings. The second case is for
         * the event that it is not possible to get Play Services running; thus the app will not be
         * usable on the device as it relies on Play Services for GCM.
         */
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case GCMService.USER_RECOVERABLE_ERROR:
                        GooglePlayServicesUtil.getErrorDialog(intent.getExtras().getInt("resultCode"), MainActivity.this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                        break;
                    case GCMService.DEVICE_NOT_SUPPORTED:
                        Toast.makeText(MainActivity.this, "Device not supported", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
            }
        }, filter);

        startGCMService(GCMService.INIT);
    }

    public void initializeButtons(){
        viewSubscritions = (Button) findViewById(R.id.buttonViewSubscriptions);
        viewSettings = (Button) findViewById(R.id.buttonViewSettings);
    }

    public void initializeListeners(){
        viewSubscritions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start new activity showing listed/available coffee machines
                Intent i = new Intent(MainActivity.this, ViewSubscribtionsActivity.class);
                startActivity(i);
            }
        });
        viewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start new activity showing settings
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                /*Intent i = new Intent(MainActivity.this, ShowMapActivity.class);
                i.putExtra("latitude", "56.153498");
                i.putExtra("longitude", "10.208037");*/
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
        if (DEBUG) Log.i(GCM_TAG, "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.i(GCM_TAG, "onResume");
        startGCMService(GCMService.CHECK_PLAY_SERVICES);
    }

    /**
     * Helper method for starting the GCMService
     * @param action What action should be performed by the service
     */
    private void startGCMService(String action) {
        Intent intent = new Intent(this, GCMService.class);
        intent.setAction(action);
        startService(intent);
    }

    private void startGPSAlarm() {
        Intent intent = new Intent(this, UpdateLocationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 5*1000, pendingIntent);
    }

    /**
     * Debugging method for printing the ID that the device gets when registering on GCM.
     */
    public void printID(View view) {
        SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String id = prefs.getString(GCMService.PROPERTY_REG_ID, "DefValue");
        /*TextView textView = (TextView) findViewById(R.id.mDisplay);
        textView.setText("ID is: " + id + "\n");*/
        Log.i(GCM_TAG, "regId: " + id);
    }

    /**
     * Debugging method for re-registering the device on GCM. Should hopefully not be necessary
     * when the app is finalized and does not change. Probably related to the fact that Google
     * requires re-registering all devices whenever a new version of the app is released.
     */
    public void reRegister(View view) {
        startGCMService(GCMService.REREGISTER);
    }
}

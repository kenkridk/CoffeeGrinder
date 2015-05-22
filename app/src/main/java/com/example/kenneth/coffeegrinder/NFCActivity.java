package com.example.kenneth.coffeegrinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class NFCActivity extends ActionBarActivity {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "Nfcdemo";
    private TextView textViewNfc;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_nfc);

        /*datasource = new ListViewClassDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        textViewNfc = (TextView) findViewById(R.id.textViewNfc);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            // Device does not support NFC
            Toast.makeText(this, "This device doesn't support NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if(!nfcAdapter.isEnabled()){
            Log.d("NFC", "NFC is disabled");
        }else{
            Log.d("NFC", "NFC is enabled");
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onResume(){
        super.onResume();
        setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause(){
        stopForegroundDispatch(this, nfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        String action = intent.getAction();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            String type = intent.getType();
            if(MIME_TEXT_PLAIN.equals(type)){
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            }else{
                Log.d(TAG, "wrong mime type: " + type);
            }
        }else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for(String tech : techList){
                if(searchedTech.equals(tech)){
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }else if(intent.ACTION_VIEW.equals(action)){

            Log.d(TAG,intent.getDataString());

            final String result = intent.getDataString().replace("p2papp://","");

            DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Check if this machine is already in the list of machines we are subscribed to
                            if (isRegistered(result.split("/")[1])) {
                                Toast.makeText(NFCActivity.this, "You are already subscribed to this device", Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            }

                            final String arr[] = result.split("/");
//                      String url = "http://" + arr[0] + "/subscribe?android=" + "<Get device registration ID and insert here>" + "&machine=" + arr[1];
                            String url = "http://" + arr[0] + "/subscribe";
                            textViewNfc.setText(url);
                            //maybe here we should add the coffee machine to the list of coffee machines.
                            RequestQueue queue = Volley.newRequestQueue(NFCActivity.this);
                            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //We do not receive response from the server for this so do nothing.
                                    //Could possibly add a response so we only add coffee machine on a 200 OK?
                                    textViewNfc.setText("Something went right!\n" + response);
//                                datasource.createListViewClass(result);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Called if something goes wrong in making the request
                                    textViewNfc.setText("Something went wrong!\n" + error.toString());
                                }
                            }) {
                                /**
                                 * Override the getParams() method and change it to contain the parameters we
                                 * want to pass to the server.
                                 */
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                                    String deviceID = prefs.getString(GCMService.PROPERTY_REG_ID, "DefaultDeviceID");
                                    Log.i("CoffeeApp", "Requesting subscription on " + arr[1] + " with device ID " + deviceID);
                                    params.put("android", deviceID);
                                    params.put("machine", arr[1]);
                                    return params;
                                }
                            };
                            queue.add(request);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                    finish();

                }
            };


            AlertDialog.Builder builder = new AlertDialog.Builder(NFCActivity.this);
            builder.setTitle("Promulgate.JS").setMessage("Do you want to subscribe to this machine?").setNegativeButton("No", dialogListener).setPositiveButton("Yes", dialogListener).show();
        }
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter){
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter){
        adapter.disableForegroundDispatch(activity);
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        private String result = "";

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if(ndef == null){
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for(NdefRecord ndefRecord : records){
                if(ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)){
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException{
            byte[] payload = record.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0063;
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result){
            if(result == null) return;

            this.result = result;
            Log.d("Read content", result);

            AlertDialog.Builder builder = new AlertDialog.Builder(NFCActivity.this);
            builder.setTitle("Promulgate.JS").setMessage("Do you want to subscribe to this machine?").setNegativeButton("No", dialogListener).setPositiveButton("Yes", dialogListener).show();
        }

        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        final String arr[] = result.split("/");

                        //Check if this machine is already in the list of machines we are subscribed to
                        if (isRegistered(arr[1])) {
                            Toast.makeText(NFCActivity.this, "You are already subscribed to this device", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        }
                        String url = "http://" + arr[0] + "/subscribe";
                        textViewNfc.setText(url);

                        RequestQueue queue = Volley.newRequestQueue(NFCActivity.this);
                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                textViewNfc.setText("Something went right!\n" + response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Called if something goes wrong in making the request
                                textViewNfc.setText("Something went wrong!\n" + error.toString());
                            }
                        }) {
                            /**
                             * Override the getParams() method and change it to contain the parameters we
                             * want to pass to the server.
                             */
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                                String deviceID = prefs.getString(GCMService.PROPERTY_REG_ID, "DefaultDeviceID");
                                Log.i("CoffeeApp", "Requesting subscription on " + arr[1] + " with device ID " + deviceID);
                                params.put("android", deviceID);
                                params.put("machine", arr[1]);
                                return params;
                            }
                        };
                        queue.add(request);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
                finish();
            }
        };
    }

    public boolean isRegistered(String machineId) {
        boolean result = false;

        ListViewClassDataSource datasource = new ListViewClassDataSource(this);
        try {
            datasource.open();
            for (ListViewClass lvc : datasource.getAllListViewClasses()) {
                if (lvc.getMachineId().equals(machineId)) result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}

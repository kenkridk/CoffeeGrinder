package com.example.kenneth.coffeegrinder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "GCM Intent Service";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        //The toast gets stuck
        //Toast.makeText(getApplicationContext(), "intentService onHandleIntent", Toast.LENGTH_SHORT).show();

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            try {
                JSONObject message = new JSONObject(extras.getString("message"));
                JSONObject config = new JSONObject(message.getString("config"));
                Log.d("GCM-message",message.toString());
                JSONObject requestDescription = message.getJSONObject("response");

                if (isMachineMuted(config.getString("id"))) {
                    GcmBroadcastReceiver.completeWakefulIntent(intent); //is a return needed here as well?
                    return;
                }

                switch (messageType) {
                    case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
//                    sendNotification("Send error: " + extras.toString());
                        Log.i(TAG, "GCM: MESSAGE_TYPE_SEND_ERROR");
                        break;
                    case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
//                    sendNotification("Deleted messages on server: " + extras.toString());
                        Log.i(TAG, "GCM: MESSAGE_TYPE_DELETED");
                        break;
                    case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
//                    sendNotification("Received " + extras.toString());
                        Log.i(TAG, "GCM received " + extras.toString());

                        Log.i(TAG, "JSON toString" + message.toString());

                        switch (message.getString("type")) {
                            case "request":
                                //Request for whether the user wants coffee or not
                                Log.i(TAG, "Received request GCM message");
                                Intent inquiryIntent = new Intent(this, CoffeeInquiry.class);
                                inquiryIntent.putExtra("time", message.getString("time"));
                                inquiryIntent.putExtra("machine", config.getString("id"));
                                inquiryIntent.putExtra("timeout", config.getString("timeout"));
                                inquiryIntent.putExtra("text", requestDescription.getString("text"));
                                inquiryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                inquiryIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                startActivity(inquiryIntent);
                                break;
                            case "notification":
                                /**
                                 * -Coffee is being brewed
                                 * -Coffee is done
                                 * -The user is on the "waiting list"
                                 * -The user is NOT on the "waiting list"
                                 */
                                JSONObject description = message.getJSONObject("description");

                                if (description.getJSONArray("extras").toString().contains("show-map")) {
                                    String lat = config.getString("lat");
                                    String lon = config.getString("lon");
                                    String flavorText = String.format("%s\nGet it at %s",
                                            message.getJSONObject("description").getString("text"),
                                            config.getString("name"));

                                    sendNotificationWithMap(description.getString("text"), lat, lon, flavorText);

                                } else sendNotification(description.getString("text"));

                                Log.i(TAG, "Received notification GCM message");
                                break;
                            case "subscription": //Received when we successfully subscribe to a machine
                                //Add hidden in the subscriber list so we can check for mute
                                //Maybe as tag on the view?
                                String machineId = config.getString("id");

                                ListViewClassDataSource datasource = new ListViewClassDataSource(this);
                                try {
                                    datasource.open();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                datasource.createListViewClass(config.getString("name"), machineId);

                                Log.i(TAG, "Received subscription GCM message");
                                break;
                            case "unsubscribed":
                                //Received when unsubscribing from a machine
                                //Remove the machine in question from list etc.
                                break;
                        }
                        break;
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSON parse error");
                e.printStackTrace();
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private boolean isMachineMuted(String id) {
        SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        Set<String> machineIds = prefs.getStringSet("mutedMachines", new HashSet<String>());
        return machineIds != null && machineIds.contains(id);
    }

    // Put the message into a notification and post it.
    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = makeNotificationBuilder(msg);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotificationWithMap(String msg, String latitude, String longitude, String flavorText) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = makeNotificationBuilder(msg);

        mBuilder.setDefaults(Notification.DEFAULT_ALL);

        Intent intent = new Intent(this, CoffeeReady.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("flavorText", flavorText);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private NotificationCompat.Builder makeNotificationBuilder(String msg) {
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_coffee)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
    }
}
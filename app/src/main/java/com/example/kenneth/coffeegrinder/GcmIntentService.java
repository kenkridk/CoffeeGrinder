package com.example.kenneth.coffeegrinder;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

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

        Toast.makeText(getApplicationContext(), "intentService onHandleIntent", Toast.LENGTH_SHORT).show();

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
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
                    try {
                        JSONObject message = new JSONObject(extras.getString("message"));
                        Log.i(TAG, "JSON toString" + message.toString());
                        //TODO Actual implementation of work
                        switch (message.getString("type")) {
                            case "request":
                                //Request for whether the user wants coffee or not
                                Log.i(TAG, "Received request GCM message");
                                break;
                            case "notification":
                                /**
                                 * Has a few different uses:
                                 * -Coffee is done
                                 * -The user is on the "waiting list"
                                 * -The user is NOT on the "waiting list"
                                 */
                                Log.i(TAG, "Received notification GCM message");
                                break;
                            case "subscription":
                                //Received when we successfully subscribe to a machine
                                Log.i(TAG, "Received subscription GCM message");
                                break;
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parse error");
                        e.printStackTrace();
                    }

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    /*private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }*/
}
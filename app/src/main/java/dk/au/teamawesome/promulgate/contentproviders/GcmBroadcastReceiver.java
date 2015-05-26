package dk.au.teamawesome.promulgate.contentproviders;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import dk.au.teamawesome.promulgate.activities.MainActivity;
import dk.au.teamawesome.promulgate.services.GcmIntentService;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (MainActivity.DEBUG) Log.i("GCM-Test", "onReceive");

        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());

        startWakefulService(context, intent.setComponent(comp));
        if(intent.getExtras().getString("message")!=null)
            setResultCode(Activity.RESULT_OK);
    }
}


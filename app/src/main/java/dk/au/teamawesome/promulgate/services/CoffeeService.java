package dk.au.teamawesome.promulgate.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Kenneth on 22-04-2015.
 */
public class CoffeeService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("CoffeeService", "Service onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
        Log.d("CoffeeService", "Service onStart");
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("CoffeeService", "Service onDestroy");
    }
}

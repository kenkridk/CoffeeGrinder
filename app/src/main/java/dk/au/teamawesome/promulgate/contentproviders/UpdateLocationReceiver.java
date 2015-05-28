package dk.au.teamawesome.promulgate.contentproviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.HashSet;
import java.util.Set;

import dk.au.teamawesome.promulgate.activities.MainActivity;

public class UpdateLocationReceiver extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    GoogleApiClient googleApiClient;
    Context ctx;

    public UpdateLocationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                /*.addOnConnectionFailedListener(this)*/
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        Log.i("UpdateLocationReceiver", "Received broadcast");
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //LocationListener
    @Override
    public void onLocationChanged(Location location) {

        SharedPreferences prefs = ctx.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> latLon = new HashSet<>();
        latLon.add(Double.toString(location.getLatitude()));
        latLon.add(Double.toString(location.getLongitude()));

        editor.putStringSet("lastKnownLocation", latLon);
        editor.apply();

//        Toast.makeText(ctx, "Current location: \n" + prefs.getStringSet("lastKnownLocation", new HashSet<String>()).toString(), Toast.LENGTH_SHORT).show();

        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }
}

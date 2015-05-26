package dk.au.teamawesome.promulgate.activities;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import dk.au.teamawesome.promulgate.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dk.au.teamawesome.promulgate.fragments.ShowMapTextFragment;


public class ShowMapActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleApiClient;
    float lat = 0.0f, lon = 0.0f;
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_show_map);

        buildGoogleApiClient();
        googleApiClient.connect();

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.showMapFragment);
        mapFragment.getMapAsync(this);
    }

    //OnMapReadyCallback method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

        lat = Float.parseFloat(getIntent().getStringExtra("latitude"));
        lon = Float.parseFloat(getIntent().getStringExtra("longitude"));
        LatLng pos = new LatLng(lat, lon);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 19));
        googleMap.addMarker(new MarkerOptions().position(pos));

    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_map, menu);
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

    //ConnectionCallbacks methods
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        ShowMapTextFragment fragment = (ShowMapTextFragment) getSupportFragmentManager().findFragmentById(R.id.showMapTextFragment);
        fragment.setText(getIntent().getStringExtra("flavorText"));
        request(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()), Float.toString(lat), Float.toString(lon));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //OnConnectionFailedListener method
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void request(String originLat, String originLon, String destLat, String destLon) {
        final String output = "json",
                origin = originLat + "," + originLon,
                destination = destLat + "," + destLon,
                mode = "walking",
                key = "AIzaSyB59zUPJI29EBk6aRmcseO0ZAQYTMFpONw";
        String url = String.format("https://maps.googleapis.com/maps/api/directions/%s?origin=%s&destination=%s&mode=%s&key=%s",
                output, origin, destination, mode, key);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONObject(response).getJSONArray("routes");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.optJSONObject(i);
                        if (obj != null) {
                            /*JSONObject polyline = obj.optJSONObject("overview_polyline");
                            Log.i("Directions", decodePoly(polyline.getString("points")).toString());

                            mapFragment.getMap().addPolyline(new PolylineOptions().addAll(decodePoly(polyline.getString("points"))));*/

                            JSONObject result;
                            if ((result = obj.optJSONObject("overview_polyline")) != null) {
                                mapFragment.getMap().addPolyline(new PolylineOptions().addAll(decodePoly(result.getString("points"))));
                                Log.i("Directions", decodePoly(result.getString("points")).toString());
                            }

                            if ((result = obj.optJSONObject("bounds")) != null) {
                                LatLng northEast = new LatLng((new JSONObject(result.getString("northeast")).getDouble("lat")),(new JSONObject(result.getString("northeast")).getDouble("lng")));
                                LatLng southWest = new LatLng((new JSONObject(result.getString("southwest")).getDouble("lat")),(new JSONObject(result.getString("southwest")).getDouble("lng")));
                                mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southWest,northEast), 10));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    /**
     * http://stackoverflow.com/questions/15924834/decoding-polyline-with-new-google-maps-api
     */
    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}

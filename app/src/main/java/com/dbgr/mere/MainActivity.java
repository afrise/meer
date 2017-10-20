package com.dbgr.mere;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private LatLng spot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 0);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        spot = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(spot));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.50f));
        googleMap.setMyLocationEnabled(true);
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                spot = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(spot));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.50f));
            }

            public void onStatusChanged(String s, int i, Bundle b) {
            }

            public void onProviderEnabled(String s) {
            }

            public void onProviderDisabled(String s) {
            }
        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                final String url = "https://www.google.com/maps/dir/?api=1&destination=" + spot.latitude + "%2C" + spot.longitude + "&zoom=17";
                JSONObject longUrl = new JSONObject();
                try {
                    longUrl.put("longUrl", url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                queue.add(new JsonObjectRequest(Request.Method.POST,
                        "https://www.googleapis.com/urlshortener/v1/url?fields=id&key=AIzaSyBszlwslur8Dk6rUfeFH9x6YAhiQ-kOvIc",
                        longUrl, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String u = url;
                        try {
                            u = response.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent().setAction(Intent.ACTION_SEND)
                                .putExtra(Intent.EXTRA_TEXT, u + "\n" + getString(R.string.custom_message))
                                .setType("text/plain"));
                    }
                },
                        new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                startActivity(new Intent().setAction(Intent.ACTION_SEND)
                                        .putExtra(Intent.EXTRA_TEXT, url + "\n" + getString(R.string.custom_message))
                                        .setType("text/plain"));
                            }
                        }));
            }
        });
    }
}



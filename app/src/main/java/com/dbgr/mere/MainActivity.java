package com.dbgr.mere;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.android.volley.Request;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.50f));
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new gps().getLatLng()));
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            getSystemService(LocationManager.class).requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 1, new LocationListener() {
                public void onLocationChanged(Location location) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(new gps().getLatitude(), location.getLongitude())));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.50f));
                }
                public void onStatusChanged(String s, int i, Bundle b) {
                }

                public void onProviderEnabled(String s) {
                }

                public void onProviderDisabled(String s) {
                }
            });
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    try {
                        Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST,
                                "https://www.googleapis.com/urlshortener/v1/url?fields=id&key=AIzaSyBszlwslur8Dk6rUfeFH9x6YAhiQ-kOvIc",
                                new JSONObject("{\"longUrl\":  \"https://www.google.com/maps/dir/?api=1&destination=" +
                                        new gps().getLatitude() + "%2C" + new gps().getLongitude() + "\"}"),
                                new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            startActivity(new Intent().setAction(Intent.ACTION_SEND)
                                                    .putExtra(Intent.EXTRA_TEXT, response.getString("id") + "\n" + getString(R.string.custom_message))
                                                    .setType("text/plain"));
                                        } catch (JSONException e) {/**/}
                                    }
                                },
                                new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        startActivity(new Intent()
                                                .setAction(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, "https://www.google.com/maps/dir/?api=1&destination=" + new gps().getLatitude() + "%2C" + new gps().getLongitude() + "\n" + getString(R.string.custom_message)).setType("text/plain"));
                                    }
                                }));
                    } catch (JSONException e) {/**/}
                }
            });
        } else System.exit(0);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
}

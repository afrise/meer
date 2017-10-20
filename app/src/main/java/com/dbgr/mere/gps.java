package com.dbgr.mere;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class gps extends Activity {
    public gps() {
    }

    public double getLatitude() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
            return getSystemService(LocationManager.class).getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
        return 0;
    }

    public double getLongitude() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
            return getSystemService(LocationManager.class).getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
        return 0;
    }

    public LatLng getLatLng() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
            return new LatLng(getSystemService(LocationManager.class).getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(),
                    getSystemService(LocationManager.class).getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());
        return new LatLng(0, 0);
    }

    public Location getLocation() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
            return new Location(getSystemService(LocationManager.class).getLastKnownLocation(LocationManager.GPS_PROVIDER));
        return new Location(LocationManager.GPS_PROVIDER);
    }
}

package com.test.kovilapauvaday.prototype_connect;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by ARAM on 05/12/2017.
 */

public class LocalisationGPS extends Service implements LocationListener {

    private final Context context;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation;

    Location location;
    protected LocationManager locationManager;

    public LocalisationGPS(Context context) {
        this.context = context;
    }

    public Location getLocation(){
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                // trouve depuis GPS
                if(isGPSEnabled) {
                    if(location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
                        if(locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }

                // trouve depuis Internet
                if(location == null) {
                    if(isNetworkEnabled) {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
                            if(locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            }

                    }
                }
            }
        } catch (Exception ex) {

        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        //this.location = location;
    }

    @Override
    public void onStatusChanged(String Provider, int status, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String Provider) {

    }

    @Override
    public void onProviderDisabled(String Provider) {

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }






}

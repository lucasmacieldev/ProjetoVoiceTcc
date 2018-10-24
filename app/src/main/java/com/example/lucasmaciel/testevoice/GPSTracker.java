package com.example.lucasmaciel.testevoice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.widget.Toast;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // no minimo 10m de deslocamento pra atualizar
    private static final long MIN_TIME_BW_UPDATES = 1000000000; //1000 * 60 * 1; // 1 minuto no minimo
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation(); //inicia busca das coordenadas
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);//status do gps
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); //status internet

            if (!isGPSEnabled && !isNetworkEnabled) {
                //sem rede e sem gps
                this.canGetLocation = false;
                toastHandlerGPSeREDEoff.sendEmptyMessage(0); //alerta
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) { //rede ok
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            toastHandlerREDE.sendEmptyMessage(0); //alerta
                        }
                    }
                }
                if (isGPSEnabled) { //gps ok
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                toastHandlerGPS.sendEmptyMessage(0); //alerta
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    //verifica se wifi ou gps estão ligados
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
    //abrir tela ativação gps
    public void AbreConfigGPS(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        mContext.startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        toastHandlerGPS.sendEmptyMessage(0); //alerta
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    //ALERTAS


    private final Handler toastHandlerREDE = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Toast.makeText(mContext, "REDE - Latitude: "+ latitude +" Longitude: "+ longitude, Toast.LENGTH_SHORT).show();
        }
    };
    private final Handler toastHandlerGPS = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Toast.makeText(mContext, "GPS - Latitude: "+ latitude +" Longitude: "+ longitude, Toast.LENGTH_SHORT).show();
        }
    };
    private final Handler toastHandlerGPSeREDEoff = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Toast.makeText(mContext, "GPS e Rede off", Toast.LENGTH_SHORT).show();
        }
    };
}
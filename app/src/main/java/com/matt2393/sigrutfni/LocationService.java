package com.matt2393.sigrutfni;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.matt2393.sigrutfni.Model.ClassConductor;

public class LocationService extends Service {


    private LocationCallback locationCallback;
    private Location location;
    private LatLng ubicAnterior;
    private DatabaseReference reference;
    private DatabaseReference databaseReference,dref;
    private boolean isTracking;
    private LocationRequest req;


    private ClassConductor conductor;
    private String key;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        conductor = intent.getParcelableExtra("CONDUCTOR");
      //  id_user=intent.getStringExtra("ID");

        if(conductor!=null) {

            key=FirebaseInit.user().getUid();

            isTracking=true;

            dref = FirebaseInit.refRoot().child("Conductor")
                    .child(key);


            Log.e("TRACKING", "Comenzo");

            req = new LocationRequest()
                    .setInterval(3000)
                    .setFastestInterval(2000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    //super.onLocationResult(locationResult);
                    location = locationResult.getLastLocation();
                    mandarUbicacion(location);

                    Log.e("SigRutFni", "lat: " + location.getLatitude() +
                            " lng: " + location.getLongitude());
                }
            };


            if (ActivityCompat.checkSelfPermission(LocationService.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(LocationService.this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                stopSelf();

            }
            LocationServices.getFusedLocationProviderClient(getApplicationContext())
                    .requestLocationUpdates(req, locationCallback, null);


        }
        else {
            stopSelf();
            Log.e("MATT_TRACKING","no hay usuario");
        }

        conductor.setActivo(true);

        dref.updateChildren(conductor.toMapAct());

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TRACKING","TERMINO");
        if(isTracking)
            LocationServices.getFusedLocationProviderClient(getApplicationContext())
                    .removeLocationUpdates(locationCallback);
        isTracking=false;

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(LocationService.this);
        preferences.edit().putBoolean("TRACKING",false).apply();

        conductor.setActivo(false);
        dref.updateChildren(conductor.toMapAct());
    }

    public static double distPuntosGeo(LatLng origen, Location destino) {
        double dLat, dLon, a, c, radio;
        double lat1,lat2,lng1,lng2;
        lat1=origen.latitude*Math.PI/180;
        lng1=origen.longitude*Math.PI/180;
        lat2=destino.getLatitude()*Math.PI/180;
        lng2=destino.getLongitude()*Math.PI/180;
        radio = 6371;
        dLat = Math.abs(lat1 - lat2);
        dLon = Math.abs(lng1 - lng2);
        a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1)
                * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radio * c * 1000;
    }

    private void mandarUbicacion(Location loc){

        if(ubicAnterior==null){
            conductor.getUbicacion().setLat(loc.getLatitude());
            conductor.getUbicacion().setLng(loc.getLongitude());
            dref.updateChildren(conductor.toMapUbic());
            ubicAnterior=new LatLng(loc.getLatitude(),loc.getLongitude());
        }
        else{
            if(distPuntosGeo(ubicAnterior,loc)>10){
                conductor.getUbicacion().setLat(loc.getLatitude());
                conductor.getUbicacion().setLng(loc.getLongitude());
                dref.updateChildren(conductor.toMapUbic());
                ubicAnterior=new LatLng(loc.getLatitude(),loc.getLongitude());
            }
        }
    }
}

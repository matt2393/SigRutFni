package com.matt2393.sigrutfni;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.matt2393.sigrutfni.Model.ClassConductor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton tracking, cerrar;
    private TextView estadoT, est,textLogout;
    private ClassConductor conductor;
    private ArrayList<ClassConductor> conductors;
    private Map<String, Marker> markerMap;
    private Map<String, ClassConductor> conductorMap;
    private Query query, query2;
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;


    private Animation fadeIn, fadeOut;
    private LocalFadeInAnimationListener myFadeInAnimationListener = new LocalFadeInAnimationListener();
    private LocalFadeOutAnimationListener myFadeOutAnimationListener = new LocalFadeOutAnimationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tracking = findViewById(R.id.sw_tracking);
        cerrar = findViewById(R.id.logout);
        estadoT = findViewById(R.id.estado_tracking);
        est = findViewById(R.id.estado);
        textLogout=findViewById(R.id.textoLogout);

        fadeIn= AnimationUtils.loadAnimation(this,R.anim.fadein);
        fadeOut= AnimationUtils.loadAnimation(this,R.anim.fadeout);

        tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!conductor.isActivo()) {
                    startService(new Intent(MapsActivity.this, LocationService.class)
                            .putExtra("CONDUCTOR", conductor));
                } else {
                    stopService(new Intent(MapsActivity.this, LocationService.class));
                }
                updateUI();
            }
        });
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseInit.getAuth().signOut();
                startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                finish();
            }
        });


        query = FirebaseInit.refRoot().child("Conductor").orderByChild("activo").equalTo(true);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conductor = dataSnapshot.getValue(ClassConductor.class);
                condBotones();
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        conductorMap = new HashMap<>();
        markerMap = new HashMap<>();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ClassConductor coo=dataSnapshot.getValue(ClassConductor.class);
                if(FirebaseInit.user()!=null){
                    if(!FirebaseInit.user().getUid().equals(dataSnapshot.getKey())){
                        conductorMap.put(dataSnapshot.getKey(), coo);
                        addMarker(coo, dataSnapshot.getKey());
                    }
                    else{
                        if(conductor.isActivo()){
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(coo.getUbicacion().getLat(),coo.getUbicacion().getLng())));
                        }
                    }
                }
                else{
                    conductorMap.put(dataSnapshot.getKey(), coo);
                    addMarker(coo, dataSnapshot.getKey());
                    Log.e("MATT_MARK","marker añadido");
                }

                Log.e("MATT_MARK","marker añadido");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ClassConductor coo=dataSnapshot.getValue(ClassConductor.class);

                if(FirebaseInit.user()!=null){
                    if(!FirebaseInit.user().getUid().equals(dataSnapshot.getKey())){
                        conductorMap.remove(dataSnapshot.getKey());
                        conductorMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(ClassConductor.class));
                        editMarker(dataSnapshot.getKey(), dataSnapshot.getValue(ClassConductor.class));
                    }
                    else{
                        if(conductor.isActivo()){
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(coo.getUbicacion().getLat(),coo.getUbicacion().getLng())));
                        }
                    }
                }
                else{
                    conductorMap.remove(dataSnapshot.getKey());
                    conductorMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(ClassConductor.class));
                    editMarker(dataSnapshot.getKey(), dataSnapshot.getValue(ClassConductor.class));
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(FirebaseInit.user()!=null) {
                    if (!FirebaseInit.user().getUid().equals(dataSnapshot.getKey())) {
                        conductorMap.remove(dataSnapshot.getKey());
                        removeMarker(dataSnapshot.getKey());
                    }
                }
                else{
                    conductorMap.remove(dataSnapshot.getKey());
                    removeMarker(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera


        Log.e("MATT_MAP","Se activo el mapa");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"Necesita permisos de ubicacion",Toast.LENGTH_LONG).show();
            finish();
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-17.97279181568855,-67.10422412899175),17));

/*        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseInit.user() != null) {
            query2 = FirebaseInit.refRoot().child("Conductor")
                    .child(FirebaseInit.user().getUid());
            query2.addValueEventListener(valueEventListener);
        } else {
            univBotones();

        }
        query.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(FirebaseInit.user()!=null){
            query2.removeEventListener(valueEventListener);
        }
        query.removeEventListener(childEventListener);
    }

    private void condBotones(){
        textLogout.setVisibility(View.VISIBLE);
        tracking.setVisibility(View.VISIBLE);
        cerrar.setVisibility(View.VISIBLE);
        estadoT.setVisibility(View.VISIBLE);
        est.setVisibility(View.VISIBLE);
    }
    private void univBotones(){

        textLogout.setVisibility(View.GONE);
        tracking.setVisibility(View.GONE);

        cerrar.setVisibility(View.GONE);
        estadoT.setVisibility(View.GONE);
        est.setVisibility(View.GONE);
    }
    private void addMarker(ClassConductor cond, String key){
        Marker mm=mMap.addMarker(new MarkerOptions()
                .position(new LatLng(cond.getUbicacion().getLat(),cond.getUbicacion().getLng()))
                .title(cond.getNombre())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name))
                .snippet("Placa: "+cond.getPlaca()));
        markerMap.put(key, mm);
    }
    private void editMarker(String key,ClassConductor cond){
        markerMap.get(key)
                .setPosition(new LatLng(cond.getUbicacion().getLat(),cond.getUbicacion().getLng()));
        markerMap.get(key)
                .setTitle(cond.getNombre());
        markerMap.get(key)
                .setSnippet("Placa: "+cond.getPlaca());
    }
    private void removeMarker(String key){
        markerMap.get(key).remove();
        markerMap.remove(key);
    }

    private void updateUI(){
        if(conductor.isActivo()){
            tracking.setBackgroundTintList(new ColorStateList(new int[][]{new int[0]},new int[]{getResources().getColor(R.color.rojo)}));
            tracking.setImageResource(R.drawable.ic_location_off_white_48dp);
            est.setText("Detener");
            runAnimations();
        }
        else{
            tracking.setBackgroundTintList(new ColorStateList(new int[][]{new int[0]},new int[]{getResources().getColor(R.color.verde)}));
            tracking.setImageResource(R.drawable.ic_location_on_white_48dp);
            est.setText("Activar");
            stopAnimations();
        }
    }


    /**
     * Animacion de parpadeo
     */

    private void launchOutAnimation() {
        estadoT.startAnimation(fadeOut);

    }

    /**
     * Performs the actual fade-in
     */
    private void launchInAnimation() {
        estadoT.startAnimation(fadeIn);
    }

    /**
     * Comienzo de la animación
     */
    private void runAnimations() {
        //uso de las animaciones

        fadeIn.setAnimationListener( myFadeInAnimationListener );
        fadeOut.setAnimationListener( myFadeOutAnimationListener );

        launchInAnimation();

        estadoT.setBackground(getResources().getDrawable(R.drawable.borde_red_verde));
        estadoT.setText("Rastreando");
    }
    private void stopAnimations(){
        estadoT.clearAnimation();
        fadeIn.setAnimationListener(null);
        fadeOut.setAnimationListener(null);
        estadoT.setBackground(getResources().getDrawable(R.drawable.borde_red_rojo));
        estadoT.setText("Sin Rastreo");
    }


    // Runnable que arranca la animación
    private Runnable mLaunchFadeOutAnimation = new Runnable() {
        public void run() {
            launchOutAnimation();
        }
    };

    private Runnable mLaunchFadeInAnimation = new Runnable() {
        public void run() {
            launchInAnimation();
        }
    };


    private class LocalFadeInAnimationListener implements Animation.AnimationListener {
        public void onAnimationEnd(Animation animation) {
            estadoT.post(mLaunchFadeOutAnimation);
        }
        public void onAnimationRepeat(Animation animation){
        }
        public void onAnimationStart(Animation animation) {
        }
    };

    /**
     * Listener de animación para el Fadein
     */
    private class LocalFadeOutAnimationListener implements Animation.AnimationListener {
        public void onAnimationEnd(Animation animation) {
            estadoT.post(mLaunchFadeInAnimation);
        }
        public void onAnimationRepeat(Animation animation) {
        }
        public void onAnimationStart(Animation animation) {
        }
    };
}

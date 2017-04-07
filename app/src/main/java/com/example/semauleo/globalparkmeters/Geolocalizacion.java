
package com.example.semauleo.globalparkmeters;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Geolocalizacion extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocalizacion);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnPos = (Button) findViewById(R.id.btnPosicion);
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Ayala jajajajaja LOL WTF BBQ", Toast.LENGTH_LONG).show();
            }
        });
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

//        Código para usarlo en el móvil
//        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        String provider = service.getBestProvider(criteria, false);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//
//            return;
//        }
//        Location loc = service.getLastKnownLocation(provider);
//        LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());

        //Código para usarlo con el emulador
        LatLng pos = new LatLng(42.46642, -2.442385);
        mMap.addMarker(new MarkerOptions().position(pos).title("Posición actual"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

    }
}
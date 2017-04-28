package com.example.semauleo.globalparkmeters;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuGeo extends AppCompatActivity {

    private Button btnGeoCoche;
    private Button btnGeoZonas;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_geo);

        id = getIntent().getStringExtra("id");

        btnGeoCoche = (Button) findViewById(R.id.btnGeoCoche);
        btnGeoCoche.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Geolocalizacion.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 0); }
        });

        btnGeoZonas = (Button) findViewById(R.id.btnGeoZonas);
        btnGeoZonas.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), GeoZonas.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 0); }
        });

    }
}

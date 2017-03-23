package com.example.semauleo.globalparkmeters;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Pagos extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    private String id;
    private Spinner spCiudades;
    private Spinner spZonas;
    private SeekBar barraTiempo;
    private TextView txtTiempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos);

        id = getIntent().getStringExtra("id");

        ArrayList<String> listCiudades = new ArrayList<String>();
        listCiudades.add("Logroño");listCiudades.add("Vitoria");listCiudades.add("Pamplona");
        ArrayAdapter<String> adapterCiudades = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listCiudades);
        spCiudades = (Spinner) findViewById(R.id.spCiudades);
        spCiudades.setAdapter(adapterCiudades);

        spCiudades.setOnItemSelectedListener(this);

        barraTiempo = (SeekBar) findViewById(R.id.barraTiempo);
        barraTiempo.setMax(2000);

        barraTiempo.setOnSeekBarChangeListener(this);
        txtTiempo = (TextView) findViewById(R.id.txtTiempo);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<String> listZonas = new ArrayList<String>();
        String nomCiudad = parent.getSelectedItem().toString();
        if (nomCiudad.equals("Logroño")) {
            listZonas.add("Logroño 1");
            listZonas.add("Logroño 2");
            listZonas.add("Logroño 3");
        }
        if (nomCiudad.equals("Vitoria")) {
            listZonas.add("Vitoria 1");
            listZonas.add("Vitoria 2");
            listZonas.add("Vitoria 3");
        }
        if (nomCiudad.equals("Pamplona")) {
            listZonas.add("Pamplona 1");
            listZonas.add("Pamplona 2");
            listZonas.add("Pamplona 3");
        }

        ArrayAdapter<String> adapterZonas = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listZonas);
        spZonas = (Spinner) findViewById(R.id.spZonas);
        spZonas.setAdapter(adapterZonas);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        txtTiempo.setText(String.valueOf(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

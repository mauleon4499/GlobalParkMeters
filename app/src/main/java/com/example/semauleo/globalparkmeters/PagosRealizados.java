package com.example.semauleo.globalparkmeters;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PagosRealizados extends AppCompatActivity {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos_realizados);

        id = getIntent().getStringExtra("id");
    }
}

package com.example.semauleo.globalparkmeters;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Listado extends AppCompatActivity {

    private String id;

    private Button btnMultas;
    private Button btnPagos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        id = getIntent().getStringExtra("id");

        //Método para acceder al listado de pagos
        btnPagos = (Button) findViewById(R.id.btnPagos);
        btnPagos.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), PagosRealizados.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 0); }
        });

        //Método para acceder a las multas
        btnMultas = (Button) findViewById(R.id.btnMultas);
        btnMultas.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Multas.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 0); }
        });
    }
}

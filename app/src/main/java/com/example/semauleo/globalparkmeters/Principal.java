package com.example.semauleo.globalparkmeters;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Principal extends AppCompatActivity {

    private Button btnPerfil;
    private Button btnPagar;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        id = getIntent().getStringExtra("id");

        //Método para acceder al perfil
        btnPerfil = (Button) findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Perfil.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 0); }
        });
    }
}

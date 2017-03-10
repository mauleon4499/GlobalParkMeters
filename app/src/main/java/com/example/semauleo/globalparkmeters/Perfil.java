package com.example.semauleo.globalparkmeters;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Perfil extends AppCompatActivity {

    private Button btnDatos;
    private Button btnPassword;
    private Button btnMatriculas;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        id = getIntent().getStringExtra("id");

        //Método para acceder a editar los datos
        btnDatos = (Button) findViewById(R.id.btnDatos);
        btnDatos.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Editar.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 0); }
        });

        //Método para acceder a cambiar contraseña
        btnPassword = (Button) findViewById(R.id.btnPassword);
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), EditarPassword.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 0); }
        });

        //Método para acceder a editar matriculas
        btnMatriculas = (Button) findViewById(R.id.btnMatriculas);
        btnMatriculas.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Matriculas.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 0); }
        });
    }
}

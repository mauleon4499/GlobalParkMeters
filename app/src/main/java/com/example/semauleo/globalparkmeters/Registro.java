package com.example.semauleo.globalparkmeters;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by semauleo on 23/02/2017.
 */

public class Registro extends AppCompatActivity{

    EditText UserName;
    EditText PassWord;
    EditText RePassWord;
    EditText Nombre;
    EditText Apellido1;
    EditText Apellido2;
    EditText Email;
    EditText Telefono;

    private  boolean UserName_OK;
    private  boolean Email_OK;

    private String ip = "10.128.3.245";

    private Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        UserName = (EditText) findViewById(R.id.txtUserNane);
        PassWord = (EditText) findViewById(R.id.txtPwd);
        RePassWord = (EditText) findViewById(R.id.txtRePwd);
        Nombre = (EditText) findViewById(R.id.txtNombre);
        Apellido1 = (EditText) findViewById(R.id.txtApel1);
        Apellido2 = (EditText) findViewById(R.id.txtApel2);
        Email = (EditText) findViewById(R.id.txtEmail);
        Telefono = (EditText) findViewById(R.id.txtTlf);

        //Método para enviar los datos a la base de datos.
        //IMPORTANTE: La ip puesta cambian en fución de la ip del pc, además hace falta el archivo de conexion.php
        guardar = (Button) findViewById(R.id.txtRegistrarse);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean error = false;
                new comprobarNombreUsuario().execute("http://"+ip+"/movil/consultarUser.php?user="+UserName.getText().toString().trim());
                new comprobarEmail().execute("http://"+ip+"/movil/consultarEmail.php?email="+Email.getText().toString().trim());

                System.out.print("boleano2: "+UserName_OK);


                if(UserName.getText().toString().trim().equals("")){
                    error = true;
                    UserName.setError("Este campo es requerido");
                }

                if(PassWord.getText().toString().trim().equals("")){
                    error = true;
                    PassWord.setError("Este campo es requerido");
                }

                if(RePassWord.getText().toString().trim().equals("")){
                    error = true;
                    RePassWord.setError("Este campo es requerido");
                }

                if(Nombre.getText().toString().trim().equals("")){
                    error = true;
                    Nombre.setError("Este campo es requerido");
                }

                if(Apellido1.getText().toString().trim().equals("")){
                    error = true;
                    Apellido1.setError("Este campo es requerido");
                }

                if(Apellido2.getText().toString().trim().equals("")){
                    error = true;
                    Apellido2.setError("Este campo es requerido");
                }

                if(Email.getText().toString().trim().equals("")){
                    error = true;
                    Email.setError("Este campo es requerido");
                }

                if(Telefono.getText().toString().trim().equals("")){
                    error = true;
                    Telefono.setError("Este campo es requerido");
                }

                if(!PassWord.getText().toString().trim().equals(RePassWord.getText().toString().trim())){
                    error = true;
                    PassWord.setError("Las contraseñas son diferentes");
                    RePassWord.setError("Las contraseñas son diferentes");
                }

               if(!Email_OK){
                    error = true;
                    Email.setError("El email ya está registrado");
               }

                if(!UserName_OK){
                    error = true;
                    UserName.setError("El usuario ya está registrado");
                }

                Toast.makeText(getApplicationContext(), "boleano2: "+UserName_OK, Toast.LENGTH_LONG).show();

                if(!error){
                    new guardarDatos().execute("http://"+ip+"/movil/guardar.php?username="+UserName.getText().toString()+"&password="+PassWord.getText().toString()+"&nombre="+Nombre.getText().toString()+"&apellido1="+Apellido1.getText().toString()+"&apellido2="+Apellido2.getText().toString()+"&email="+Email.getText().toString()+"&telefono="+Telefono.getText().toString());
                   Intent intent = new Intent (v.getContext(), Login.class);
                   startActivityForResult(intent, 0);
               }
            }
        });
    }

    //Método para comprobar el email
    private class comprobarEmail extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            JSONObject ja = null;

            try {
                ja = new JSONObject(result);
                if(ja.getBoolean("esta")){
                    Email_OK = false;
                }else{
                    Email_OK = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Método para ocomprobar el user
    private class comprobarNombreUsuario extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            JSONObject ja = null;

            try {
                ja = new JSONObject(result);
                if(ja.getBoolean("esta")){
                    UserName_OK = false;
                }else{
                    UserName_OK = true;
                }
                System.out.print("boleano: "+UserName_OK);
                Toast.makeText(getApplicationContext(), "boleano1: "+UserName_OK, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //Método para enviar datos a la base de datos
    private class guardarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(), "Se almacenaron los datos correctamente", Toast.LENGTH_LONG).show();
        }
    }

    //Método para la conexión con la base de datos
    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl = myurl.replace(" ","%20");
        InputStream is = null;

        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            int response = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + response);
            is = conn.getInputStream();

            String contentAsString = readIt(is, len);
            return contentAsString;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    //Nétodo para leer los datos que envia el servidor
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}

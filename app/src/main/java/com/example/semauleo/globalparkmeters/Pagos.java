package com.example.semauleo.globalparkmeters;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Pagos extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String id;
    Spinner spCiudades;
    Spinner spZonas;
    Spinner spMatricula;
    private ArrayList<String> matriculas = new ArrayList<String>();
    private ArrayAdapter<String> adaptadorMatriculas;
    private ArrayList<String> ciudades = new ArrayList<String>();
    private ArrayAdapter<String> adaptadorCiudades;
    private ArrayList<String> zonas = new ArrayList<String>();
    private ArrayAdapter<String> adaptadorZonas;
    private JSONObject datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos);

        id = getIntent().getStringExtra("id");
        spMatricula = (Spinner) findViewById(R.id.spMatricula);
        spCiudades = (Spinner) findViewById(R.id.spCiudades);
        spZonas = (Spinner) findViewById(R.id.spZonas);
        new Pagos.obtenerDatos().execute("http://"+getString(R.string.ip)+"/movil/datosZonas.php?id="+id.toString().trim());

        spCiudades.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<String> listZonas = new ArrayList<String>();
        String nomCiudad = parent.getSelectedItem().toString();
        zonas = new ArrayList<String>();

        JSONArray jc = null;
        int pos = 100;
        try {
            jc = datos.getJSONArray("ciudades");
            for(int i =0;i < jc.length();i++){
                if (nomCiudad.equals(jc.getJSONObject(i).getString("ciudad"))) {
                    pos = i;
                }
            }

            JSONArray jz = jc.getJSONObject(pos).getJSONArray("zonas");
            for(int i =0;i < jz.length();i++){
                zonas.add(jz.getJSONObject(i).getString("nombre"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adaptadorZonas = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,zonas);
        spZonas = (Spinner) findViewById(R.id.spZonas);
        spZonas.setAdapter(adaptadorZonas);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Método para comprobar el nombre de usuario y la contraseña
    private class obtenerDatos extends AsyncTask<String, Void, String> {

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
                datos = new JSONObject(result);
                //Cargar matrículas del usuario
                JSONArray jm = ja.getJSONArray("matriculas");
                for(int i =0;i < jm.length();i++){
                    matriculas.add(jm.getString(i));
                }
                adaptadorMatriculas = new ArrayAdapter<String>(Pagos.this, android.R.layout.simple_list_item_1,matriculas);
                spMatricula.setAdapter(adaptadorMatriculas);

                //Cargar ciudades
                JSONArray jc = ja.getJSONArray("ciudades");
                for(int i =0;i < jc.length();i++){
                    ciudades.add(jc.getJSONObject(i).getString("ciudad"));
                }
                System.out.println(ciudades);
                adaptadorCiudades = new ArrayAdapter<String>(Pagos.this, android.R.layout.simple_list_item_1,ciudades);
                spCiudades.setAdapter(adaptadorCiudades);

                //Cargar zonas de la primera ciudad
                /*JSONArray jz = jc.getJSONObject(0).getJSONArray("zonas");
                for(int i =0;i < jz.length();i++){
                    zonas.add(jz.getJSONObject(i).getString("nombre"));
                }
                System.out.println(zonas);
                adaptadorZonas = new ArrayAdapter<String>(Pagos.this, android.R.layout.simple_list_item_1,zonas);
                spZonas.setAdapter(adaptadorZonas);*/

            } catch (JSONException e) {
                e.printStackTrace();
            }

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

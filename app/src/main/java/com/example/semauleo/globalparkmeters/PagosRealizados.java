package com.example.semauleo.globalparkmeters;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PagosRealizados extends AppCompatActivity {

    private String id;

    private ArrayList<String> pagos;
    private ArrayAdapter<String> adaptadorPagos;
    private ListView lvPagos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos_realizados);

        id = getIntent().getStringExtra("id");

        pagos=new ArrayList<String>();
        adaptadorPagos=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,pagos);
        lvPagos = (ListView) findViewById(R.id.lvPagos);
        lvPagos.setAdapter(adaptadorPagos);

        //Obtener los datos de las ciudades, zonas, precios y tiempos máximos
        new PagosRealizados.obtenerPagos().execute("http://"+getString(R.string.ip)+"/movil/datosPagos.php?id="+id.toString().trim());
    }

    //Método para comprobar el nombre de usuario y la contraseña
    private class obtenerPagos extends AsyncTask<String, Void, String> {

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

            JSONObject jo = null;

            try {
                jo = new JSONObject(result);
                JSONArray ja = jo.getJSONArray("pagos");
                //System.out.println(ja);

                //Mostramos loa datos de las multas
                for(int i =0;i < ja.length();i++){
                    JSONObject jop = ja.getJSONObject(i);
                    String pago = "";

                    pago = pago + "Id: "+ jop.getString("id") +"\n";
                    pago = pago + "Ciudad:  "+ jop.getString("ciudad") + "\n";
                    pago = pago + "Zona:  " + jop.getString("zona") + "\n";
                    pago = pago + "Matricula: " + jop.getString("matricula") + "\n";
                    pago = pago + "Importe:  " + jop.getString("importe") + " euros"  + "\n";
                    pago = pago + "Fecha inicio: " + jop.getString("fecha_inicio") + "\n";
                    pago = pago + "Fecha fin: " + jop.getString("fecha_fin");

                    pagos.add(pago);
                    adaptadorPagos.notifyDataSetChanged();
                }

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

        int len = 10000000;

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
        reader = new InputStreamReader(stream, StandardCharsets.ISO_8859_1);
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}

package com.example.semauleo.globalparkmeters;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Tarifas extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private  String id;
    private JSONObject datos;

    Spinner spCiudades;
    ListView lvZonas;

    private ArrayList<String> ciudades = new ArrayList<String>();
    private ArrayAdapter<String> adaptadorCiudades;
    private ArrayList<String> zonas;
    private ArrayAdapter<String> adaptadorZonas;
    private String ciudad_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarifas);

        id = getIntent().getStringExtra("id");
        spCiudades = (Spinner) findViewById(R.id.spCiudades);

        zonas=new ArrayList<String>();
        adaptadorZonas=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,zonas);
        lvZonas = (ListView) findViewById(R.id.lvZonas);
        lvZonas.setAdapter(adaptadorZonas);

        //Obtener los datos de las ciudades, zonas, precios y tiempos máximos
        new Tarifas.obtenerDatos().execute("http://"+getString(R.string.ip)+"/movil/datosZonas.php?id="+id.toString().trim());

        spCiudades.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Obtenemos nombre de la ciudad
        String nomCiudad = parent.getSelectedItem().toString();

        //Reseteamos el ListView
        zonas.clear();
        adaptadorZonas.notifyDataSetChanged();

        JSONArray jc = null;
        int pos = 100;
        try {
            jc = datos.getJSONArray("ciudades");
            for(int i =0;i < jc.length();i++){
                if (nomCiudad.equals(jc.getJSONObject(i).getString("ciudad"))) {
                    pos = i;
                    ciudad_id = jc.getJSONObject(i).getString("ciudadID");
                }
            }

            JSONArray jz = jc.getJSONObject(pos).getJSONArray("zonas");
            for(int i =0;i < jz.length();i++){
                String datoZona = "";

                datoZona = datoZona + "Zona:  "+ jz.getJSONObject(i).getString("nombre") + "\n";
                datoZona = datoZona + "Precio:  " + jz.getJSONObject(i).getDouble("precio") + " euros/minuto" +"\n";
                datoZona = datoZona + "Tiempo máximo:  " + jz.getJSONObject(i).getString("tiempo") + " horas";

                zonas.add(datoZona);
                adaptadorZonas.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                //Cargar ciudades
                JSONArray jc = ja.getJSONArray("ciudades");
                for(int i =0;i < jc.length();i++){
                    ciudades.add(jc.getJSONObject(i).getString("ciudad"));
                }

                adaptadorCiudades = new ArrayAdapter<String>(Tarifas.this, android.R.layout.simple_list_item_1,ciudades);
                spCiudades.setAdapter(adaptadorCiudades);

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

        int len = 5000000;

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

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

import static com.example.semauleo.globalparkmeters.R.layout.activity_multas;

public class Multas extends AppCompatActivity {

    private String id;

    //private ArrayList<String> multas;
    //private ArrayAdapter<String> adaptadorMultas;
    private ListView lvMultas;
    private AdapterMulta adapter;
    ArrayList<multa> ListaMultas = new ArrayList<multa>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_multas);

        id = getIntent().getStringExtra("id");

        lvMultas = (ListView) findViewById(R.id.lvMultas);
        adapter = new AdapterMulta(this, ListaMultas);
        lvMultas.setAdapter(adapter);

        /*multas=new ArrayList<String>();
        adaptadorMultas=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,multas);
        lvMultas = (ListView) findViewById(R.id.lvMultas);
        lvMultas.setAdapter(adaptadorMultas);*/
        //Obtener los datos de las ciudades, zonas, precios y tiempos máximos
        new Multas.obtenerMultas().execute("http://"+getString(R.string.ip)+"/movil/datosMultas.php?id="+id.toString().trim());
    }

    //Método para comprobar el nombre de usuario y la contraseña
    private class obtenerMultas extends AsyncTask<String, Void, String> {

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

            JSONArray ja = null;

            try {
                ja = new JSONArray(result);

                //Mostramos loa datos de las multas
                for(int i =0;i < ja.length();i++){
                    JSONObject jo = ja.getJSONObject(i);
                    String estado = jo.getString("pagada");
                    String valor;
                    if(estado.equals("0")){
                         valor = "No";
                    }else{
                        valor = "Si";
                    }
                    ListaMultas.add(new multa(jo.getString("matricula"),jo.getString("fecha_hora"),jo.getString("importe"),valor,jo.getString("motivo")));

                    /*String multa = "";

                    multa = multa + "Matrícula:  "+ jo.getString("matricula") + "\n";
                    multa = multa + "Importe:  " + jo.getString("importe") + " euros" + "\n";
                    multa = multa + "Fecha:  " + jo.getString("fecha_hora") + "\n";
                    String estado = jo.getString("pagada");
                    if(estado.equals("0")){
                        multa = multa + "Estado:  No pagada" + "\n";
                    }else{
                        multa = multa + "Estado:  Pagada"  +"\n";
                    }
                    multa = multa + "Motivo:  " + jo.get("motivo");

                    multas.add(multa);
                    adaptadorMultas.notifyDataSetChanged();*/
                }
                adapter = new AdapterMulta(adapter.getActivity(), ListaMultas);
                lvMultas.setAdapter(adapter);
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

        int len = 50000000;

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

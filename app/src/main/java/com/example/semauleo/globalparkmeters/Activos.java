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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Activos extends AppCompatActivity {

    private String id;

    private ArrayList<String> pagos;
    private ArrayAdapter<String> adaptadorPagos;
    private ListView lvPagosActivos;

    private AdapterActivo adapter;
    ArrayList<activo> ListaActios = new ArrayList<activo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activos);

        id = getIntent().getStringExtra("id");

        lvPagosActivos = (ListView) findViewById(R.id.lvPagosActivos);
        adapter = new AdapterActivo(this, ListaActios);
        lvPagosActivos.setAdapter(adapter);

        /*pagos=new ArrayList<String>();
        adaptadorPagos=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,pagos);
        lvPagosActivos = (ListView) findViewById(R.id.lvPagosActivos);
        lvPagosActivos.setAdapter(adaptadorPagos);*/

        //Obtener los datos de las ciudades, zonas, precios y tiempos máximos
        new Activos.obtenerActivos().execute("http://"+getString(R.string.ip)+"/movil/datosActivos.php?id="+id.toString().trim());
    }

    //Método para comprobar el nombre de usuario y la contraseña
    private class obtenerActivos extends AsyncTask<String, Void, String> {

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

                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.HOUR, 2);
                    Date actual = c.getTime();
                    long ta = actual.getTime();

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String t = jop.getString("fecha_fin");
                    Date tiempo_final = df.parse(t);
                    long tf = tiempo_final.getTime();

                    long td = tf-ta;
                    ListaActios.add(new activo(jop.getString("id"),jop.getString("ciudad"),jop.getString("zona"),jop.getString("matricula"),jop.getString("importe"),jop.getString("fecha_inicio"),jop.getString("fecha_fin"),td));

                    /*String pago = "";

                    pago = pago + "Id: "+ jop.getString("id") +"\n";
                    pago = pago + "Ciudad:  "+ jop.getString("ciudad") + "\n";
                    pago = pago + "Zona:  " + jop.getString("zona") + "\n";
                    pago = pago + "Matricula: " + jop.getString("matricula") + "\n";
                    pago = pago + "Importe:  " + jop.getString("importe") + " euros"  + "\n";
                    pago = pago + "Fecha inicio: " + jop.getString("fecha_inicio") + "\n";
                    pago = pago + "Fecha fin: " + jop.getString("fecha_fin");

                    pagos.add(pago);
                    adaptadorPagos.notifyDataSetChanged();*/
                }

                adapter = new AdapterActivo(adapter.getActivity(), ListaActios);
                lvPagosActivos.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
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
